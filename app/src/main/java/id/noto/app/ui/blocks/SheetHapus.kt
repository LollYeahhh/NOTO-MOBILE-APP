package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/**
 * Isi "Overlay - Hapus Aturan" (node 3715): Kepala Sheet (judul dengan nama aturan), Panel Alternatif
 * "Cuma butuh jeda?", lalu Aksi (jarak 8): "Ya, hapus aturan" (Utama) dan "Batal" (Permukaan).
 */
@Composable
fun ColumnScope.SheetHapus(nama: String, jumlahAplikasi: Int, onTutup: () -> Unit, onHapus: () -> Unit) {
    NotoKepalaSheet(
        stringResource(R.string.hapus_judul, nama),
        stringResource(R.string.hapus_ket, subjekAplikasi(jumlahAplikasi)),
        onTutup,
    )
    // Panel Alternatif: Permukaan sudut 12, padding 12, ikon jeda 24 + teks, jarak 8.
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_jeda), contentDescription = null, modifier = Modifier.size(24.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.jeda_judul), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksUtama)
            Text(stringResource(R.string.jeda_ket), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NotoTombol(stringResource(R.string.ya_hapus_aturan), onKlik = onHapus)
        NotoTombol(stringResource(R.string.batal), onKlik = onTutup, tipe = TipeTombol.Permukaan)
    }
}

/** "Aplikasinya" (1), "Kedua aplikasinya" … "Kesembilan aplikasinya" (2–9), "Semua aplikasinya" (≥ 10). */
@Composable
private fun subjekAplikasi(jumlah: Int): String {
    val angka = stringArrayResource(R.array.angka_kata)
    return when {
        jumlah <= 1 -> stringResource(R.string.hapus_subjek_satu)
        jumlah - 2 < angka.size -> stringResource(R.string.hapus_subjek_banyak, angka[jumlah - 2])
        else -> stringResource(R.string.hapus_subjek_semua)
    }
}

@Preview(name = "Overlay - Hapus Aturan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetHapus() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetHapus("Sosmed", 5, {}, {}) } }
}
