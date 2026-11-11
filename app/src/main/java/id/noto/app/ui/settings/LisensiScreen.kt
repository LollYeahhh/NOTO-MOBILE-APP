package id.noto.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna

/** Satu lisensi pihak ketiga. [berkas] = teks lisensinya di folder assets. */
data class Lisensi(val ikon: Int, val nama: Int, val keterangan: Int, val pendek: Int, val panjang: Int, val berkas: String)

val daftarLisensi = listOf(
    Lisensi(R.drawable.ic_dokumen, R.string.lisensi_poppins, R.string.lisensi_poppins_ket, R.string.lisensi_ofl_pendek, R.string.lisensi_ofl, "Poppins-OFL.txt"),
    Lisensi(R.drawable.ic_kode, R.string.lisensi_compose, R.string.lisensi_compose_ket, R.string.lisensi_apache_pendek, R.string.lisensi_apache, "Apache-2.0.txt"),
    Lisensi(R.drawable.ic_kode, R.string.lisensi_kotlin, R.string.lisensi_kotlin_ket, R.string.lisensi_apache_pendek, R.string.lisensi_apache, "Apache-2.0.txt"),
)

/** "Setelan - Lisensi" (node 5220): satu kartu berisi daftar lisensi; ketuk untuk membuka teksnya. */
@Composable
fun LisensiScreen(onKembali: () -> Unit, onPilih: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.lisensi_open_source_judul), onIkonKiri = onKembali)
        IsiSetelan {
            SeksiSetelan(label = null) {
                daftarLisensi.forEachIndexed { i, lisensi ->
                    NotoBarisSetelan(
                        painterResource(lisensi.ikon),
                        stringResource(lisensi.nama),
                        keterangan = stringResource(lisensi.keterangan),
                        nilai = stringResource(lisensi.pendek),
                        onKlik = { onPilih(i) },
                    )
                }
            }
        }
    }
}

@Preview(name = "Setelan - Lisensi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewLisensi() {
    NOTOAPPTheme { LisensiScreen({}, {}) }
}
