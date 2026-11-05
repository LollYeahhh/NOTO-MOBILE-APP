package id.noto.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.noto.app.ui.components.NotoHeaderLogo
import id.noto.app.ui.theme.NotoWarna

// Kerangka sementara Tahap 2A: hanya Header, isinya putih kosong.
// Isi layar Beranda dibuat di Tahap 3 sesuai FigCraft.
@Composable
fun BerandaScreen() {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderLogo()
    }
}
