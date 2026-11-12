package id.noto.app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import id.noto.app.MainActivity
import id.noto.app.R
import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.JenisKejadian
import id.noto.app.data.KejadianDao
import id.noto.app.data.KuotaDao
import id.noto.app.data.jendelaSekarang
import id.noto.app.data.selesaiJendela
import id.noto.app.ui.settings.JAM_TENANG_MULAI
import id.noto.app.ui.settings.JAM_TENANG_SELESAI
import id.noto.app.ui.settings.SetelanNoto
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam
import java.time.LocalDateTime
import java.time.ZoneId

// Notifikasi pengingat (Tahap 8D-2): sisa kuota, kuota habis, jadwal mulai dan selesai.
// Dicek PemantauService tiap putaran (hanya saat layar menyala). Tiap pengingat ditandai di tabel kejadian
// supaya terkirim sekali per aturan per jendela jadwal.

/** Satu pengingat untuk aturan [aturan]: [jenis] = penanda di tabel kejadian, [menit] = ambang sisa kuota. */
data class Pengingat(val jenis: String, val aturan: Aturan, val mulaiJendela: LocalDateTime, val menit: Int = 0)

/** Jam tenang 22.00–06.00 (melewati tengah malam). */
fun diJamTenang(t: LocalDateTime): Boolean {
    val m = t.hour * 60 + t.minute
    return m >= JAM_TENANG_MULAI || m < JAM_TENANG_SELESAI
}

/**
 * Pengingat yang perlu ditangani untuk aturan [a] pada [t], sesuai sakelar di [setelan].
 * [terpakaiDetik] = pemakaian di jendela yang dimulai pada argumennya; [sudah] = penanda itu pernah ditangani.
 * Hasil berisi semua yang harus ditandai; yang dikirim hanya yang [Pengingat.menit] terkecil untuk sisa kuota
 * (lihat [yangDikirim]).
 */
suspend fun pengingatAturan(
    a: Aturan,
    t: LocalDateTime,
    setelan: SetelanNoto,
    terpakaiDetik: suspend (LocalDateTime) -> Long,
    sudah: suspend (String, LocalDateTime) -> Boolean,
): List<Pengingat> {
    if (!a.aktif) return emptyList()
    val hasil = mutableListOf<Pengingat>()
    val mulai = jendelaSekarang(a, t)
    if (mulai == null) {
        // Jadwal selesai dalam 1 jam terakhir. Lebih lama dari itu (layar mati semalaman) tidak dikabarkan lagi.
        // shortcut: jadwal yang lebih pendek dari 1 jam dan dimulai setelah [t]−1 jam terlewat; perbaiki kalau ada keluhan.
        val lalu = jendelaSekarang(a, t.minusHours(1))
        if (setelan.saatJadwal && lalu != null && !selesaiJendela(a, lalu).isAfter(t) &&
            !sudah(JenisKejadian.NOTIF_SELESAI, lalu)
        ) {
            hasil += Pengingat(JenisKejadian.NOTIF_SELESAI, a, lalu)
        }
        return hasil
    }
    if (setelan.saatJadwal && !sudah(JenisKejadian.NOTIF_MULAI, mulai)) hasil += Pengingat(JenisKejadian.NOTIF_MULAI, a, mulai)
    val sisaDetik = a.kuotaMenit * 60L - terpakaiDetik(mulai)
    if (sisaDetik <= 0) {
        if (setelan.saatKuotaHabis && !sudah(JenisKejadian.NOTIF_HABIS, mulai)) hasil += Pengingat(JenisKejadian.NOTIF_HABIS, a, mulai)
    } else if (setelan.pengingatSisa) {
        // Ambang yang tidak lebih kecil dari kuota dilewati: sisanya sudah segitu sejak jadwal mulai.
        for (m in setelan.sisaMenit.sorted().filter { it < a.kuotaMenit }) {
            val jenis = JenisKejadian.notifSisa(m)
            if (sisaDetik <= m * 60L && !sudah(jenis, mulai)) hasil += Pengingat(jenis, a, mulai, m)
        }
    }
    return hasil
}

/** Dari hasil [pengingatAturan]: sisa kuota cukup dikirim satu, yaitu ambang terkecil yang terlewati. */
fun yangDikirim(semua: List<Pengingat>): List<Pengingat> {
    val (sisa, lain) = semua.partition { it.menit > 0 }
    return lain + listOfNotNull(sisa.minByOrNull { it.menit })
}

/** Cek dan kirim pengingat semua aturan. Saat jam tenang, pengingat tetap ditandai tapi tidak ditampilkan. */
suspend fun periksaPengingat(
    context: Context,
    aturan: List<AturanLengkap>,
    setelan: SetelanNoto,
    t: LocalDateTime,
    kuotaDao: KuotaDao,
    kejadianDao: KejadianDao,
) {
    hapusPengingatKuotaUsang(context, aturan, t)
    for (a in aturan) {
        val semua = pengingatAturan(
            a.aturan, t, setelan,
            terpakaiDetik = { kuotaDao.terpakai(a.aturan.id, milidetik(it)) },
            sudah = { jenis, mulai -> kejadianDao.ada(jenis, a.aturan.id, milidetik(mulai)) },
        )
        if (semua.isEmpty()) continue
        val waktu = milidetik(t)
        semua.forEach { kejadianDao.catatSekali(waktu, it.jenis, a.aturan.id, "", milidetik(it.mulaiJendela)) }
        if (setelan.jamTenang && diJamTenang(t)) continue
        yangDikirim(semua).forEach { kirim(context, it) }
    }
}

/** Notifikasi sisa kuota/time out dihapus begitu aturannya di luar jadwal atau dijeda: aplikasinya sudah bebas. */
private fun hapusPengingatKuotaUsang(context: Context, aturan: List<AturanLengkap>, t: LocalDateTime) {
    val bebas = aturan.filter { !it.aturan.aktif || jendelaSekarang(it.aturan, t) == null }.map { idNotifikasi(it.aturan.id, KELOMPOK_KUOTA) }
    if (bebas.isEmpty()) return
    val manajer = context.getSystemService(NotificationManager::class.java)
    manajer.activeNotifications.filter { it.id in bebas }.forEach { manajer.cancel(it.id) }
}

private fun kirim(context: Context, p: Pengingat) {
    val a = p.aturan
    val selesai = selesaiJendela(a, p.mulaiJendela).let { formatJam(it.hour * 60 + it.minute) }
    val (judul, isi) = when (p.jenis) {
        JenisKejadian.NOTIF_MULAI -> context.getString(R.string.notif_mulai_judul, a.nama) to
            context.getString(R.string.notif_mulai_isi, formatDurasi(a.kuotaMenit), selesai)
        JenisKejadian.NOTIF_SELESAI -> context.getString(R.string.notif_selesai_judul, a.nama) to
            context.getString(R.string.notif_selesai_isi)
        JenisKejadian.NOTIF_HABIS -> context.getString(R.string.notif_habis_judul, a.nama) to
            context.getString(R.string.notif_habis_isi, selesai)
        else -> context.getString(R.string.notif_sisa_judul, formatDurasi(p.menit), a.nama) to
            context.getString(R.string.contoh_pengingat_isi)
    }
    // Dua notifikasi per aturan (jadwal dan kuota): yang baru menggantikan yang lama di kelompok yang sama.
    val kelompok = if (p.jenis == JenisKejadian.NOTIF_MULAI || p.jenis == JenisKejadian.NOTIF_SELESAI) KELOMPOK_JADWAL else KELOMPOK_KUOTA
    tampilkanNotifikasi(context, idNotifikasi(a.id, kelompok), judul, isi, keStatistik = false)
}

/** Tampilkan notifikasi di saluran "Pengingat". Diketuk: buka Noto ([keStatistik] = langsung tab Statistik). */
fun tampilkanNotifikasi(context: Context, id: Int, judul: String, isi: String, keStatistik: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    buatSaluranPengingat(context)
    val intent = Intent(context, MainActivity::class.java).putExtra(MainActivity.KE_STATISTIK, keStatistik)
    // Kode permintaan berbeda supaya extra ke Statistik tidak tertukar dengan notifikasi lain.
    val buka = PendingIntent.getActivity(context, if (keStatistik) 1 else 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val notifikasi = NotificationCompat.Builder(context, SALURAN_PENGINGAT)
        .setSmallIcon(R.drawable.ic_notifikasi)
        .setContentTitle(judul)
        .setContentText(isi)
        .setContentIntent(buka)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(id, notifikasi)
}

private fun buatSaluranPengingat(context: Context) {
    val saluran = NotificationChannel(SALURAN_PENGINGAT, context.getString(R.string.saluran_pengingat), NotificationManager.IMPORTANCE_DEFAULT)
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(saluran)
}

private const val SALURAN_PENGINGAT = "pengingat"
// ID 1 dipakai notifikasi pemantau.
private const val ID_PENGINGAT_AWAL = 1000
const val ID_RINGKASAN = 999
private const val KELOMPOK_JADWAL = 0
private const val KELOMPOK_KUOTA = 1

private fun idNotifikasi(aturanId: Long, kelompok: Int) = ID_PENGINGAT_AWAL + aturanId.toInt() * 2 + kelompok

private fun milidetik(t: LocalDateTime): Long = t.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
