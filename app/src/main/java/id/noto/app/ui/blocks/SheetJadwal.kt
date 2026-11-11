package id.noto.app.ui.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBar24Jam
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoChipHari
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoKolomWaktu
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatJam

/**
 * Isi "Overlay - Atur Jadwal" (node 3612): Kepala Sheet, Hari berlaku, Jam terbatas, Template cepat,
 * lalu tombol Simpan Jadwal. Perubahan baru dipakai form setelah Simpan; X/di luar sheet = batal.
 * [draf] disimpan pemanggil supaya tetap ada saat sheet ditukar dengan Sheet Jam (Kolom Waktu).
 */
@Composable
fun ColumnScope.SheetJadwal(
    draf: JadwalForm,
    onUbah: (JadwalForm) -> Unit,
    onPilihJam: (mulai: Boolean) -> Unit,
    onTutup: () -> Unit,
    onSimpan: (JadwalForm) -> Unit,
) {
    val namaHari = namaHariSingkat()

    NotoKepalaSheet(stringResource(R.string.atur_jadwal), stringResource(R.string.atur_jadwal_ket), onTutup)
    SeksiSheet(R.string.hari_berlaku) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            namaHari.forEachIndexed { i, nama ->
                val aktif = i in draf.hari
                NotoChipHari(nama, aktif, onKlik = { onUbah(draf.copy(hari = if (aktif) draf.hari - i else draf.hari + i)) })
            }
        }
    }
    SeksiSheet(R.string.jam_terbatas) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NotoKolomWaktu(stringResource(R.string.mulai), formatJam(draf.mulaiMenit), onKlik = { onPilihJam(true) }, modifier = Modifier.weight(1f))
            NotoKolomWaktu(stringResource(R.string.selesai), formatJam(draf.selesaiMenit), onKlik = { onPilihJam(false) }, modifier = Modifier.weight(1f))
        }
        NotoBar24Jam(draf.mulaiMenit, draf.selesaiMenit)
        Text(stringResource(R.string.catatan_jam_terbatas), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
    }
    SeksiSheet(R.string.template_cepat) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            templateJadwal.forEach { t ->
                val label = stringResource(
                    R.string.template_label,
                    stringResource(t.nama),
                    stringResource(R.string.rentang_singkat, formatJam(t.mulaiMenit), formatJam(t.selesaiMenit)),
                )
                NotoChip(
                    label,
                    aktif = draf.mulaiMenit == t.mulaiMenit && draf.selesaiMenit == t.selesaiMenit,
                    onKlik = { onUbah(draf.copy(mulaiMenit = t.mulaiMenit, selesaiMenit = t.selesaiMenit)) },
                )
            }
        }
    }
    // Tanpa hari berlaku, aturan tidak pernah aktif: tombol dinonaktifkan (disepakati 4a).
    NotoTombol(stringResource(R.string.simpan_jadwal), onKlik = { onSimpan(draf) }, aktif = draf.hari.isNotEmpty())
}

/** Label seksi sheet (NOTO/Label, teks sekunder) lalu isinya, jarak 8. */
@Composable
internal fun SeksiSheet(label: Int, isi: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        isi()
    }
}

/** Sen … Min dari strings.xml, urutan sama dengan indeks [JadwalForm.hari]. */
@Composable
internal fun namaHariSingkat() = listOf(
    R.string.hari_sen, R.string.hari_sel, R.string.hari_rab, R.string.hari_kam,
    R.string.hari_jum, R.string.hari_sab, R.string.hari_min,
).map { stringResource(it) }

/** Senin … Minggu dari strings.xml. */
@Composable
internal fun namaHariPanjang() = listOf(
    R.string.hari_senin, R.string.hari_selasa, R.string.hari_rabu, R.string.hari_kamis,
    R.string.hari_jumat, R.string.hari_sabtu, R.string.hari_minggu,
).map { stringResource(it) }

@Preview(name = "Overlay - Atur Jadwal", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetJadwal() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetJadwal(jadwalBawaan, {}, {}, {}, {}) } }
}
