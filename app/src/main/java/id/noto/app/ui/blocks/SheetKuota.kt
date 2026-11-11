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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoStepper
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi

// shortcut: langkah stepper 15 menit, batas 15m–24j; belum ada di desain, ubah kalau pemilik proyek menentukan lain.
private const val LANGKAH_KUOTA_MENIT = 15
private const val KUOTA_MAKS_MENIT = 24 * 60

/**
 * Isi "Overlay - Atur Kuota" (node 3678): Kepala Sheet, Stepper, Pilihan cepat, Panel Kesadaran,
 * catatan, lalu tombol Simpan Kuota. Perubahan baru dipakai form setelah Simpan.
 */
@Composable
fun ColumnScope.SheetKuota(awalMenit: Int, onTutup: () -> Unit, onSimpan: (Int) -> Unit) {
    var kuota by remember { mutableIntStateOf(awalMenit) }

    NotoKepalaSheet(stringResource(R.string.atur_kuota), stringResource(R.string.atur_kuota_ket), onTutup)
    NotoStepper(
        formatDurasi(kuota),
        stringResource(R.string.kuota_satuan),
        onKurangi = { kuota = (kuota - LANGKAH_KUOTA_MENIT).coerceAtLeast(LANGKAH_KUOTA_MENIT) },
        onTambah = { kuota = (kuota + LANGKAH_KUOTA_MENIT).coerceAtMost(KUOTA_MAKS_MENIT) },
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
        pilihanKuotaCepat.forEach { m -> NotoChip(formatDurasi(m), aktif = kuota == m, onKlik = { kuota = m }) }
    }
    PanelKesadaran(kuota)
    Text(stringResource(R.string.catatan_kuota), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
    NotoTombol(stringResource(R.string.simpan_kuota), onKlik = { onSimpan(kuota) })
}

/** Panel Kesadaran: Permukaan sudut 12, padding 12, ikon info 24 + judul dan keterangan, jarak 8. */
@Composable
private fun PanelKesadaran(kuota: Int) {
    val biasa = CONTOH_PEMAKAIAN_BIASA_MENIT
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_info), contentDescription = null, modifier = Modifier.size(24.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                stringResource(R.string.kesadaran_judul, formatDurasi(biasa)),
                Modifier.tinggiBarisFigCraft(NotoTeks.Label),
                style = NotoTeks.Label,
                color = NotoWarna.TeksUtama,
            )
            // Keterangan hanya masuk akal kalau kuota lebih kecil dari pemakaian biasa.
            if (kuota < biasa) {
                Text(
                    stringResource(R.string.kesadaran_ket, formatDurasi(kuota), formatDurasi(biasa - kuota)),
                    style = NotoTeks.KecilTeks,
                    color = NotoWarna.TeksSekunder,
                )
            }
        }
    }
}

@Preview(name = "Overlay - Atur Kuota", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetKuota() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetKuota(KUOTA_BAWAAN_MENIT, {}, {}) } }
}
