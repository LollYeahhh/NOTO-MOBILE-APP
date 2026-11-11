package id.noto.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import id.noto.app.MainActivity
import id.noto.app.R
import id.noto.app.data.AturanLengkap
import id.noto.app.data.KuotaDao
import id.noto.app.data.NotoDatabase
import id.noto.app.data.jendelaSekarang
import id.noto.app.ui.permission.Izin
import id.noto.app.ui.permission.izinDiberikan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Pemantau pemakaian. Foreground service = layanan latar yang wajib menampilkan notifikasi tetap
 * ("Noto sedang berjalan"), sehingga Android tidak mudah menghentikannya.
 *
 * Selama layar menyala, setiap 2 detik membaca kejadian baru dari UsageStatsManager, lalu menambahkan
 * waktu sejak putaran sebelumnya ke aplikasi yang sedang di layar (tabel pemakaian, per jam).
 * Semua aplikasi dicatat; aturan dan kuota dihitung dari tabel ini (Tahap 6D).
 * Saat layar mati, putaran berhenti sampai layar menyala lagi.
 */
class PemantauService : Service() {
    // Putaran berjalan di thread utama (sama dengan penerima layar), jadi [pelacak] tidak diakses bersamaan.
    // Pembacaan UsageStatsManager dipindah ke thread IO; Room menulis di thread-nya sendiri.
    private val cakupan = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var putaran: Job? = null
    private val pelacak = PelacakLatar()
    // Aturan dari database (selalu terbaru), untuk menghitung kuota aplikasi yang sedang di layar.
    private var aturan: List<AturanLengkap> = emptyList()

    // Waktu layar terakhir mati; null = pemantau baru menyala.
    private var layarMatiPada: Long? = null

    // Layar menyala/mati: putaran dijalankan hanya saat layar menyala supaya hemat baterai.
    private val penerimaLayar = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_ON) mulaiPutaran() else hentikanPutaran()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        buatSaluran(this)
        // Android 14+ wajib menyebut jenis foreground service; sebelumnya cukup dari Manifest.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(ID_NOTIFIKASI, notifikasi(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(ID_NOTIFIKASI, notifikasi())
        }
        ContextCompat.registerReceiver(
            this,
            penerimaLayar,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        cakupan.launch { NotoDatabase.ambil(this@PemantauService).aturanDao().semua().collect { aturan = it } }
        if (getSystemService(PowerManager::class.java).isInteractive) mulaiPutaran()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Akses penggunaan dicabut: tidak ada yang bisa dihitung, pemantau berhenti.
        if (!izinDiberikan(this, Izin.AksesPenggunaan)) stopSelf()
        // Dihentikan sistem → dijalankan ulang otomatis.
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(penerimaLayar)
        cakupan.cancel()
        super.onDestroy()
    }

    private fun mulaiPutaran() {
        if (putaran?.isActive == true) return
        putaran = cakupan.launch {
            val usm = getSystemService(UsageStatsManager::class.java)
            val db = NotoDatabase.ambil(this@PemantauService)
            val dao = db.pemakaianDao()
            val kuotaDao = db.kuotaDao()
            // Noto sendiri dan launcher tidak dicatat (pilihan pemilik proyek 3b).
            val abaikan = setOfNotNull(packageName, paketLauncher())
            var sebelumnya = System.currentTimeMillis()
            // Cari aplikasi yang sedang di layar. Pemantau baru menyala: dari kejadian 1 jam terakhir, tidak
            // dihitung "dibuka". Layar baru menyala: dari saat layar mati, karena aplikasi bisa sudah tampil
            // sebelum putaran ini mulai; aplikasi itu dihitung dibuka. Waktunya baru dihitung mulai sekarang.
            val matiPada = layarMatiPada
            pelacak.proses(bacaPeristiwa(usm, matiPada ?: (sebelumnya - 60 * 60 * 1000L), sebelumnya), hitungBuka = matiPada != null)
                .filterNot { it in abaikan }
                .forEach { paket ->
                    val waktu = LocalDateTime.now()
                    dao.tambah(waktu.toLocalDate().toEpochDay(), waktu.hour, paket, 0, 1)
                    catatKuota(kuotaDao, waktu, paket, 0, 1)
                }
            while (isActive) {
                delay(JEDA_MS)
                if (!izinDiberikan(this@PemantauService, Izin.AksesPenggunaan)) {
                    stopSelf()
                    return@launch
                }
                val sekarang = System.currentTimeMillis()
                val dibuka = pelacak.proses(bacaPeristiwa(usm, sebelumnya, sekarang))
                val waktu = LocalDateTime.now()
                val tanggal = waktu.toLocalDate().toEpochDay()
                dibuka.filterNot { it in abaikan }.forEach {
                    dao.tambah(tanggal, waktu.hour, it, 0, 1)
                    catatKuota(kuotaDao, waktu, it, 0, 1)
                }
                pelacak.diLayar?.takeIf { it !in abaikan }?.let {
                    val detik = (sekarang - sebelumnya) / 1000
                    dao.tambah(tanggal, waktu.hour, it, detik, 0)
                    catatKuota(kuotaDao, waktu, it, detik, 0)
                }
                // Sisa milidetik tidak dibuang: dibawa ke putaran berikutnya.
                sebelumnya = sekarang - (sekarang - sebelumnya) % 1000
            }
        }
    }

    /**
     * Tambahkan pemakaian [paket] ke kuota setiap aturan aktif yang memuatnya dan sedang dalam jadwal.
     * Di luar jadwal tidak ada yang dicatat (aplikasi bebas dipakai).
     */
    private suspend fun catatKuota(kuotaDao: KuotaDao, waktu: LocalDateTime, paket: String, detik: Long, dibuka: Int) {
        for (a in aturan) {
            if (!a.aturan.aktif || paket !in a.paket) continue
            val mulai = jendelaSekarang(a.aturan, waktu) ?: continue
            kuotaDao.tambah(a.aturan.id, mulai.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), paket, detik, dibuka)
        }
    }

    private fun hentikanPutaran() {
        putaran?.cancel()
        putaran = null
        layarMatiPada = System.currentTimeMillis()
        pelacak.proses(listOf(Peristiwa.LayarMati))
    }

    /** Paket launcher (layar utama) yang sedang dipakai. */
    private fun paketLauncher(): String? =
        packageManager.resolveActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0)?.activityInfo?.packageName

    /** Kejadian pindah aplikasi dan layar mati pada [dari]..[sampai]. */
    @Suppress("DEPRECATION") // MOVE_TO_FOREGROUND/BACKGROUND = ACTIVITY_RESUMED/PAUSED, tersedia sejak API 26.
    private suspend fun bacaPeristiwa(usm: UsageStatsManager, dari: Long, sampai: Long): List<Peristiwa> = withContext(Dispatchers.IO) {
        val hasil = mutableListOf<Peristiwa>()
        val kejadian = usm.queryEvents(dari, sampai)
        val e = UsageEvents.Event()
        while (kejadian.hasNextEvent()) {
            kejadian.getNextEvent(e)
            when (e.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> hasil += Peristiwa.Masuk(e.packageName)
                UsageEvents.Event.MOVE_TO_BACKGROUND -> hasil += Peristiwa.Keluar(e.packageName)
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> hasil += Peristiwa.LayarMati
            }
        }
        hasil
    }

    private fun notifikasi(): Notification {
        val buka = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, SALURAN)
            .setSmallIcon(R.drawable.ic_notifikasi)
            .setContentTitle(getString(R.string.pemantau_judul))
            .setContentText(getString(R.string.pemantau_isi))
            .setContentIntent(buka)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val SALURAN = "pemantau"
        private const val ID_NOTIFIKASI = 1
        private const val JEDA_MS = 2_000L

        private fun buatSaluran(context: Context) {
            val saluran = NotificationChannel(SALURAN, context.getString(R.string.saluran_pemantau), NotificationManager.IMPORTANCE_LOW)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(saluran)
        }

        /** Menyalakan pemantau kalau Akses penggunaan sudah diizinkan. Aman dipanggil berkali-kali. */
        fun mulai(context: Context) {
            if (!izinDiberikan(context, Izin.AksesPenggunaan)) return
            ContextCompat.startForegroundService(context, Intent(context, PemantauService::class.java))
        }
    }
}
