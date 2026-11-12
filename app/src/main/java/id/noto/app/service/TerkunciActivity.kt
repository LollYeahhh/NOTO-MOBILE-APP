package id.noto.app.service

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import id.noto.app.MainActivity
import androidx.lifecycle.lifecycleScope
import id.noto.app.data.JenisKejadian
import id.noto.app.data.KunciAplikasi
import id.noto.app.data.jendelaSekarang
import id.noto.app.data.SetelanRepository
import id.noto.app.ui.blocks.InfoTerkunci
import id.noto.app.ui.blocks.TerkunciScreen
import id.noto.app.ui.blocks.rememberWaktu
import id.noto.app.ui.theme.NOTOAPPTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * "Blokir - Terkunci Time Out" di atas aplikasi yang kuotanya habis. Dibuka PemantauService.
 * Tombol back sama dengan "Kembali ke layar utama": ke layar utama Android, bukan ke aplikasi yang dikunci.
 */
class TerkunciActivity : ComponentActivity() {
    // Intent terakhir; berganti kalau aplikasi lain terkunci saat layar ini masih terbuka.
    private var data by mutableStateOf(Intent())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        data = intent
        onBackPressedDispatcher.addCallback(this) { batalMembuka() }
        val repository = SetelanRepository(this)
        // Layar baru muncul (bukan dibuat ulang karena layar diputar): catat latihan napas kalau panelnya tampil.
        if (savedInstanceState == null) {
            lifecycleScope.launch { if (repository.setelan.first().latihanNapas) catat(JenisKejadian.NAPAS) }
        }
        setContent {
            // null sampai DataStore terbaca, supaya Panel Napas/badge yang dimatikan tidak sempat berkedip.
            val setelan by remember { repository.setelan }.collectAsState(null)
            // Berganti tiap menit, supaya "Bebas lagi dalam …" ikut berkurang.
            val waktu = rememberWaktu()
            val selesai = data.getLongExtra(SELESAI, 0)
            val sisaMenit = remember(waktu, selesai) {
                ((selesai - System.currentTimeMillis() + 59_999) / 60_000).toInt().coerceAtLeast(0)
            }
            // Jadwal selesai: aplikasi bebas lagi, layar Terkunci menutup sendiri ke layar utama.
            LaunchedEffect(selesai) {
                delay(selesai - System.currentTimeMillis())
                keLayarUtama()
            }
            val s = setelan ?: return@setContent
            NOTOAPPTheme {
                TerkunciScreen(
                    InfoTerkunci(
                        namaAturan = data.getStringExtra(NAMA_ATURAN).orEmpty(),
                        kuotaMenit = data.getIntExtra(KUOTA_MENIT, 0),
                        aplikasi = data.getStringExtra(APLIKASI).orEmpty(),
                        bebasMenit = data.getIntExtra(BEBAS_MENIT, 0),
                        sisaMenit = sisaMenit,
                    ),
                    onKembali = ::batalMembuka,
                    onStatistik = ::keStatistik,
                    tampilNapas = s.latihanNapas,
                    tampilSisa = s.sisaWaktu,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        data = intent
    }

    /** Pengguna memilih tidak membuka aplikasinya: dicatat "batal membuka", lalu ke layar utama. */
    private fun batalMembuka() {
        catat(JenisKejadian.BATAL)
        keLayarUtama()
    }

    private fun catat(jenis: String) =
        catatKejadian(this, jenis, data.getLongExtra(ATURAN_ID, 0), data.getStringExtra(PAKET).orEmpty(), data.getLongExtra(MULAI, 0))

    private fun keLayarUtama() {
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    private fun keStatistik() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(MainActivity.KE_STATISTIK, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        )
        finish()
    }

    companion object {
        private const val NAMA_ATURAN = "nama_aturan"
        private const val KUOTA_MENIT = "kuota_menit"
        private const val APLIKASI = "aplikasi"
        private const val BEBAS_MENIT = "bebas_menit"
        private const val SELESAI = "selesai"
        private const val ATURAN_ID = "aturan_id"
        private const val PAKET = "paket"
        private const val MULAI = "mulai"

        /** Intent layar Terkunci untuk aplikasi [paket] bernama [aplikasi] yang dikunci [kunci]. */
        fun intent(context: Context, kunci: KunciAplikasi, aplikasi: String, paket: String): Intent =
            Intent(context, TerkunciActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(NAMA_ATURAN, kunci.aturan.nama)
                .putExtra(KUOTA_MENIT, kunci.aturan.kuotaMenit)
                .putExtra(APLIKASI, aplikasi)
                .putExtra(BEBAS_MENIT, kunci.selesai.hour * 60 + kunci.selesai.minute)
                .putExtra(SELESAI, kunci.selesai.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .putExtra(ATURAN_ID, kunci.aturan.id)
                .putExtra(PAKET, paket)
                .putExtra(MULAI, jendelaSekarang(kunci.aturan, LocalDateTime.now())?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L)
    }
}
