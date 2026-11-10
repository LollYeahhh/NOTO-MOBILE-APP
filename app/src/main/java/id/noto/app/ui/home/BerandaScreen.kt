package id.noto.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoHeaderLogo
import id.noto.app.ui.components.NotoTombolTambahBlokir
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

// Tinggi Bottom Nav (114) yang menutupi bagian bawah layar.
private val TINGGI_NAV = 114.dp

/** Beranda: daftar aturan (Beranda - Terisi) atau Empty State (Beranda - Kosong) kalau belum ada aturan. */
@Composable
fun BerandaScreen(
    aturan: List<AturanBeranda> = contohAturanBeranda,
    onBukaDetail: () -> Unit = {},
    onTambah: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderLogo()
        if (aturan.isEmpty()) BerandaKosong() else BerandaTerisi(aturan, onBukaDetail, onTambah)
    }
}

/**
 * Header 114 → label y138 (jarak 24) → kartu y164 (jarak 10), antarbagian 24, tombol tambah di akhir.
 * Kartu membuka Detail Aturan (push), tombol + membuka Tambah Aturan (move in), seperti FigCraft.
 */
@Composable
private fun BerandaTerisi(aturan: List<AturanBeranda>, onBukaDetail: () -> Unit, onTambah: () -> Unit) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = TINGGI_NAV + 24.dp),
    ) {
        val (aktif, nonaktif) = aturan.partition { it.aktif }
        Bagian(R.string.blokir_aktif, aktif, onBukaDetail)
        Bagian(R.string.blokir_nonaktif, nonaktif, onBukaDetail)
        NotoTombolTambahBlokir(onKlik = onTambah)
    }
}

/** Label bagian + kartu-kartunya (jarak 10), lalu jarak 24 ke bagian berikutnya. Bagian kosong tidak ditampilkan. */
@Composable
private fun Bagian(label: Int, aturan: List<AturanBeranda>, onKlik: () -> Unit) {
    if (aturan.isEmpty()) return
    Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
    Column(
        Modifier.padding(top = 10.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        aturan.forEach { KartuBlokirBeranda(it, onKlik) }
    }
}

/** Empty State (node 214) di tengah ruang antara Header dan Bottom Nav, jarak antarisi 6. */
@Composable
private fun BerandaKosong() {
    Box(Modifier.fillMaxSize().padding(bottom = TINGGI_NAV), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Ikon 75,94 (hitam 9%) dari vector berukuran 125,16 yang punya ruang tepi.
            Box(Modifier.size(75.94.dp), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.ic_lama_blokir_kosong), null, Modifier.requiredSize(125.16.dp))
            }
            Text(
                stringResource(R.string.kosong_judul),
                style = NotoTeks.JudulKartu,
                color = NotoWarna.TeksUtama,
                textAlign = TextAlign.Center,
            )
            Row(Modifier.size(281.2.dp, 18.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.kosong_ajakan), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder, maxLines = 1)
                Box(Modifier.size(10.2.dp), contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.ic_lama_blokir_inline), null, Modifier.requiredSize(15.143.dp))
                }
            }
        }
    }
}

@Preview(name = "Beranda - Terisi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewBerandaTerisi() {
    NOTOAPPTheme { BerandaScreen() }
}

@Preview(name = "Beranda - Kosong", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewBerandaKosong() {
    NOTOAPPTheme { BerandaScreen(aturan = emptyList()) }
}
