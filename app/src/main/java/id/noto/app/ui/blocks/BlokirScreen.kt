package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import id.noto.app.ui.components.NotoKartuBlokir
import id.noto.app.ui.components.NotoTombolTambahBlokir
import id.noto.app.ui.components.StatusAturan
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

// Tinggi Bottom Nav (114) yang menutupi bagian bawah layar.
private val TINGGI_NAV = 114.dp

/**
 * Tab Blokir: Daftar aturan (Blokir - Daftar) atau Empty State (Blokir - Kosong).
 * [onBukaDetail] dipanggil saat kartu yang punya halaman detail diketuk; null = kartu tidak bisa diketuk.
 */
@Composable
fun BlokirScreen(
    aturan: List<AturanDaftar> = contohAturanDaftar,
    onBukaDetail: ((Long) -> Unit)? = {},
    onTambah: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderLogo()
        if (aturan.isEmpty()) BlokirKosong(onTambah) else BlokirDaftar(aturan, onBukaDetail, onTambah)
    }
}

/**
 * Konten (node 1757): padding 24/16, jarak antarbagian 24. Judul halaman, tiga seksi
 * (Sedang Berlaku, Di Luar Jadwal, Nonaktif), lalu tombol tambah.
 */
@Composable
private fun BlokirDaftar(aturan: List<AturanDaftar>, onBukaDetail: ((Long) -> Unit)?, onTambah: () -> Unit) {
    val berlaku = aturan.filter { it.status in setOf(StatusAturan.Berlaku, StatusAturan.HampirHabis, StatusAturan.TimeOut) }
    val luarJadwal = aturan.filter { it.status == StatusAturan.DiLuarJadwal }
    val nonaktif = aturan.filter { it.status == StatusAturan.Nonaktif }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = TINGGI_NAV + 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.aturan_blokir), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
            Text(
                stringResource(R.string.aturan_blokir_ringkasan, berlaku.size, aturan.size),
                Modifier.tinggiBarisFigCraft(NotoTeks.Teks),
                style = NotoTeks.Teks,
                color = NotoWarna.TeksSekunder,
            )
        }
        Seksi(R.string.seksi_sedang_berlaku, berlaku, onBukaDetail)
        Seksi(R.string.seksi_di_luar_jadwal, luarJadwal, onBukaDetail)
        Seksi(R.string.seksi_nonaktif, nonaktif, onBukaDetail)
        NotoTombolTambahBlokir(onKlik = onTambah)
    }
}

/** Label seksi + kartu (jarak 8, antarkartu 16). Seksi tanpa kartu tidak ditampilkan. */
@Composable
private fun Seksi(label: Int, aturan: List<AturanDaftar>, onBukaDetail: ((Long) -> Unit)?) {
    if (aturan.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            aturan.forEach { a ->
                NotoKartuBlokir(
                    judul = a.judul,
                    jadwal = a.jadwal,
                    status = a.status,
                    labelStatus = a.labelStatus,
                    sisa = a.sisa,
                    total = a.total,
                    progres = a.progres,
                    logo = a.logo.map { painterResource(it) },
                    // Setiap kartu membuka Detail aturannya sendiri.
                    onKlik = onBukaDetail?.let { buka -> { buka(a.id) } },
                )
            }
        }
    }
}

/** Empty State (node 1740) di x56 y330, lalu tombol tambah di y496 (jarak 26). */
@Composable
private fun BlokirKosong(onTambah: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 216.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.width(281.2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
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
            Text(
                stringResource(R.string.blokir_kosong_ajakan),
                Modifier.fillMaxWidth(),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
                textAlign = TextAlign.Center,
            )
        }
        NotoTombolTambahBlokir(onKlik = onTambah, modifier = Modifier.padding(top = 26.dp))
    }
}

@Preview(name = "Blokir - Daftar", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewBlokirDaftar() {
    NOTOAPPTheme { BlokirScreen() }
}

@Preview(name = "Blokir - Kosong", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewBlokirKosong() {
    NOTOAPPTheme { BlokirScreen(aturan = emptyList()) }
}
