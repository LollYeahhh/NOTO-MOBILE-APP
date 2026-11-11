package id.noto.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.noto.app.service.PemantauService
import id.noto.app.ui.navigation.NotoNavGraph
import id.noto.app.ui.theme.NOTOAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge: aplikasi digambar di belakang status bar dan navigation bar.
        // Keduanya transparan dengan ikon gelap, supaya latar Permukaan header dan nav terlihat
        // utuh seperti di FigCraft (tanpa lapisan putih tipis bawaan Android di navigation bar).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            NOTOAPPTheme {
                NotoNavGraph()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Setiap aplikasi dibuka (termasuk kembali dari halaman izin): nyalakan pemantau kalau Akses penggunaan sudah diizinkan.
        PemantauService.mulai(this)
    }
}
