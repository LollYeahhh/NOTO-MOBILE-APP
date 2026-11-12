package id.noto.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.rapikanTeksLisensi

// Halaman teks di bawah Tentang Noto: Catatan Rilis, FAQ, dan Teks Lisensi.
// Polanya sama: Header Halaman, Label Seksi, lalu kartu teks.

/** "Setelan - Catatan Rilis" (node 5038). */
@Composable
fun CatatanRilisScreen(onKembali: () -> Unit) {
    HalamanTeks(stringResource(R.string.catatan_rilis_judul), onKembali) {
        SeksiTeks(R.string.rilis_02_label) {
            KartuTeks(stringResource(R.string.rilis_02_judul), stringResource(R.string.rilis_02_isi))
        }
        SeksiTeks(R.string.rilis_label) {
            KartuTeks(stringResource(R.string.rilis_judul), stringResource(R.string.rilis_isi))
        }
    }
}

private val FAQ = listOf(
    R.string.faq_tanya_1 to R.string.faq_jawab_1,
    R.string.faq_tanya_2 to R.string.faq_jawab_2,
    R.string.faq_tanya_3 to R.string.faq_jawab_3,
    R.string.faq_tanya_4 to R.string.faq_jawab_4,
)

/** "Setelan - FAQ" (node 5158): tanya-jawab, lalu "Masih ada pertanyaan?" yang langsung membuka email. */
@Composable
fun FaqScreen(onKembali: () -> Unit) {
    val context = LocalContext.current
    val subjek = stringResource(R.string.subjek_pertanyaan)
    HalamanTeks(stringResource(R.string.faq), onKembali) {
        SeksiTeks(R.string.faq_label) {
            FAQ.forEach { (tanya, jawab) -> KartuTeks(stringResource(tanya), stringResource(jawab)) }
        }
        SeksiSetelan(R.string.faq_masih_tanya) {
            NotoBarisSetelan(
                painterResource(R.drawable.ic_surat),
                stringResource(R.string.kirim_email),
                keterangan = stringResource(R.string.kirim_email_ket),
                onKlik = { kirimEmail(context, subjek) },
            )
        }
    }
}

/** "Setelan - Teks Lisensi" (node 5344): judul = nama pustaka, label = nama lisensi, kartu = teks lengkapnya. */
@Composable
fun TeksLisensiScreen(lisensi: Lisensi, onKembali: () -> Unit) {
    val context = LocalContext.current
    val teks = remember(lisensi.berkas) {
        rapikanTeksLisensi(context.assets.open(lisensi.berkas).bufferedReader().use { it.readText() })
    }
    HalamanTeks(stringResource(lisensi.nama), onKembali) {
        SeksiTeks(lisensi.panjang) { KartuTeks(judul = null, isi = teks) }
    }
}

@Composable
private fun HalamanTeks(judul: String, onKembali: () -> Unit, isi: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(judul, onIkonKiri = onKembali)
        IsiSetelan(isi)
    }
}

/** Label Seksi lalu kartu-kartu teks, jarak 8. */
@Composable
private fun SeksiTeks(label: Int, isi: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        isi()
    }
}

/** Kartu Permukaan sudut 16, padding 16: judul (NOTO/Judul Item) lalu isi (NOTO/Teks), jarak 8. */
@Composable
private fun KartuTeks(judul: String?, isi: String) {
    Column(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (judul != null) Text(judul, Modifier.tinggiBarisFigCraft(NotoTeks.JudulItem), style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama)
        Text(isi, Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
    }
}

@Preview(name = "Setelan - Catatan Rilis", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewCatatanRilis() {
    NOTOAPPTheme { CatatanRilisScreen {} }
}

@Preview(name = "Setelan - FAQ", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewFaq() {
    NOTOAPPTheme { FaqScreen {} }
}
