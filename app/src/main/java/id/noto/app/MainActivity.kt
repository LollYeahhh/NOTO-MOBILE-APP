package id.noto.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import id.noto.app.service.PemantauService
import id.noto.app.service.jadwalkanRingkasan
import kotlinx.coroutines.launch
import id.noto.app.ui.navigation.NotoNavGraph
import id.noto.app.ui.theme.NOTOAPPTheme

class MainActivity : ComponentActivity() {
    // true = pindah ke tab Statistik begitu Beranda tampil.
    private var keStatistik by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge: aplikasi digambar di belakang status bar dan navigation bar.
        // Keduanya transparan dengan ikon gelap, supaya latar Permukaan header dan nav terlihat
        // utuh seperti di FigCraft (tanpa lapisan putih tipis bawaan Android di navigation bar).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        keStatistik = intent.getBooleanExtra(KE_STATISTIK, false)
        setContent {
            NOTOAPPTheme {
                NotoNavGraph(keStatistik = keStatistik, onKeStatistik = { keStatistik = false })
            }
        }
    }

    // Dibuka lagi saat sudah berjalan (misalnya dari "Lihat statistik hari ini" di layar Terkunci).
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(KE_STATISTIK, false)) keStatistik = true
    }

    override fun onResume() {
        super.onResume()
        // Setiap aplikasi dibuka (termasuk kembali dari halaman izin): nyalakan pemantau kalau Akses penggunaan sudah diizinkan.
        PemantauService.mulai(this)
        lifecycleScope.launch { jadwalkanRingkasan(this@MainActivity) }
    }

    companion object {
        const val KE_STATISTIK = "ke_statistik"
    }
}
