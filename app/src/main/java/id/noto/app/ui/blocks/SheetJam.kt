package id.noto.app.ui.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/** Menit di roda dipilih per 5 menit (desain "Overlay - Jam Mulai": 50, 55, 00, 05, 10). */
private const val LANGKAH_MENIT = 5
private val TINGGI_BARIS = 40.dp

/** Berapa kali angka diulang di roda; cukup banyak supaya ujungnya tidak pernah tercapai. */
private const val PUTARAN_RODA = 1000

/**
 * Isi "Overlay - Jam Mulai" (node 4528) / "Overlay - Jam Selesai" (node 4580): Kepala Sheet, Roda Waktu
 * (jam dan menit, 5 baris setinggi 40, baris tengah disorot Permukaan), lalu tombol Simpan Jam.
 * [lawanMenit] = jam di ujung lain jadwal; jam yang sama berarti rentang 0 menit, jadi tombol dinonaktifkan.
 */
@Composable
fun ColumnScope.SheetJam(
    judul: Int,
    subjudul: Int,
    awalMenit: Int,
    lawanMenit: Int,
    onTutup: () -> Unit,
    onSimpan: (Int) -> Unit,
) {
    var jam by remember { mutableIntStateOf(awalMenit / 60 % 24) }
    var menit by remember { mutableIntStateOf((awalMenit % 60 + LANGKAH_MENIT / 2) / LANGKAH_MENIT % (60 / LANGKAH_MENIT)) }
    val hasil = jam * 60 + menit * LANGKAH_MENIT

    NotoKepalaSheet(stringResource(judul), stringResource(subjudul), onTutup)
    Box(Modifier.fillMaxWidth().height(TINGGI_BARIS * 5), contentAlignment = Alignment.Center) {
        // Sorotan baris terpilih: Permukaan, sudut 12, setinggi satu baris.
        Box(Modifier.fillMaxWidth().height(TINGGI_BARIS).background(NotoWarna.Permukaan, RoundedCornerShape(12.dp)))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            KolomRoda(24, jam, onPilih = { jam = it })
            Text(":", Modifier.tinggiBarisFigCraft(NotoTeks.AngkaHero), style = NotoTeks.AngkaHero, color = NotoWarna.TeksUtama)
            KolomRoda(60 / LANGKAH_MENIT, menit, onPilih = { menit = it }, kali = LANGKAH_MENIT)
        }
    }
    NotoTombol(stringResource(R.string.simpan_jam), onKlik = { onSimpan(hasil) }, aktif = hasil != lawanMenit % (24 * 60))
}

/**
 * Satu kolom roda (lebar 72): angka 0 sampai [jumlah] − 1 (dikali [kali]) yang berputar terus (setelah 55
 * kembali ke 00, seperti desain: 50, 55, 00, 05, 10), digulir dan berhenti tepat di satu baris (snap).
 * Baris tengah = terpilih (Angka Hero, teks utama), satu baris di sebelahnya Judul Kartu teks sekunder,
 * dua baris Judul Item sage pucet.
 */
@Composable
private fun KolomRoda(jumlah: Int, terpilih: Int, onPilih: (Int) -> Unit, kali: Int = 1) {
    // Roda "tanpa ujung": daftar panjang berisi angka yang berulang, mulai dari tengahnya.
    val total = jumlah * PUTARAN_RODA
    val keadaan = rememberLazyListState(initialFirstVisibleItemIndex = jumlah * (PUTARAN_RODA / 2) + terpilih)
    // Padding atas/bawah 2 baris membuat item ke-i tepat di tengah saat firstVisibleItemIndex = i.
    val tengah by remember {
        derivedStateOf {
            val lewatSetengah = keadaan.firstVisibleItemScrollOffset > keadaan.layoutInfo.viewportSize.height / 10
            (keadaan.firstVisibleItemIndex + if (lewatSetengah) 1 else 0).coerceIn(0, total - 1)
        }
    }
    LaunchedEffect(keadaan) { snapshotFlow { tengah % jumlah }.collect(onPilih) }

    LazyColumn(
        Modifier.width(72.dp).height(TINGGI_BARIS * 5),
        state = keadaan,
        contentPadding = PaddingValues(vertical = TINGGI_BARIS * 2),
        flingBehavior = rememberSnapFlingBehavior(keadaan),
    ) {
        items(total) { i ->
            val nilai = i % jumlah * kali
            val jarak = kotlin.math.abs(i - tengah)
            val (gaya, warna) = when (jarak) {
                0 -> NotoTeks.AngkaHero to NotoWarna.TeksUtama
                1 -> NotoTeks.JudulKartu to NotoWarna.TeksSekunder
                else -> NotoTeks.JudulItem to NotoWarna.Pasif
            }
            Box(Modifier.width(72.dp).height(TINGGI_BARIS), contentAlignment = Alignment.Center) {
                Text("%02d".format(nilai), style = gaya, color = warna, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(name = "Overlay - Jam Mulai", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetJam() {
    NOTOAPPTheme {
        NotoSheet(tampil = true, onTutup = {}) { SheetJam(R.string.jam_mulai, R.string.jam_mulai_ket, 7 * 60, 21 * 60, {}, {}) }
    }
}
