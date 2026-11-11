package id.noto.app.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.noto.app.R
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.settings.IsiSetelan
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna

/**
 * "Statistik - Detail Distribusi" (node 5558), dibuka dari "Detail" di Minggu Ini:
 * kartu Distribusi berjudul "Rata-rata per hari" dengan semua aplikasi. Baris aplikasi membuka Detail Aplikasi.
 */
@Composable
fun DetailDistribusiScreen(onKembali: () -> Unit, onAplikasi: (AplikasiMingguan) -> Unit) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.distribusi_penggunaan), onIkonKiri = onKembali)
        IsiSetelan {
            KartuDistribusi(
                contohMingguan.copy(aplikasi = contohDistribusiLengkap),
                judul = R.string.rata_rata_per_hari_judul,
                onDetail = null,
                onAplikasi = onAplikasi,
            )
        }
    }
}

@Preview(name = "Statistik - Detail Distribusi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewDetailDistribusi() {
    NOTOAPPTheme { DetailDistribusiScreen({}, {}) }
}
