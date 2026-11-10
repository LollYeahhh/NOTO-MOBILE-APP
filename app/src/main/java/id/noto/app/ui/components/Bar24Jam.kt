package id.noto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

private const val MENIT_SEHARI = 24 * 60

/**
 * "Bar 24 Jam" (node 1697): track 12 (blok "Bebas" sage bocil) berisi blok "Terbatas" sage lebih muda
 * dari [mulaiMenit] sampai [selesaiMenit], dan garis "sekarang" 3 dp teks utama. Di bawahnya sumbu
 * 00.00–24.00 dan legenda. Semua posisi dalam menit sejak 00.00 (0..1440).
 * Jadwal yang melewati tengah malam (selesai < mulai) digambar sebagai dua blok.
 */
@Composable
fun NotoBar24Jam(
    mulaiMenit: Int,
    selesaiMenit: Int,
    modifier: Modifier = Modifier,
    sekarangMenit: Int? = null,
    tampilkanLegenda: Boolean = true,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(NotoWarna.Garis)
                .drawBehind {
                    fun x(menit: Int) = size.width * menit / MENIT_SEHARI
                    fun blok(dari: Int, sampai: Int) =
                        drawRect(NotoWarna.PrimerMuda, Offset(x(dari), 0f), Size(x(sampai) - x(dari), size.height))
                    if (selesaiMenit >= mulaiMenit) {
                        blok(mulaiMenit, selesaiMenit)
                    } else {
                        blok(mulaiMenit, MENIT_SEHARI)
                        blok(0, selesaiMenit)
                    }
                    if (sekarangMenit != null) {
                        drawRect(NotoWarna.TeksUtama, Offset(x(sekarangMenit), 0f), Size(3.dp.toPx(), size.height))
                    }
                },
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            for (jam in 0..24 step 6) {
                Text("%02d.00".format(jam), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
            }
        }
        if (tampilkanLegenda) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Legenda(NotoWarna.PrimerMuda, stringResource(R.string.legenda_terbatas))
                Legenda(NotoWarna.Garis, stringResource(R.string.legenda_bebas))
            }
        }
    }
}

/** Titik 8 + label NOTO/Kecil Label, jarak 6. */
@Composable
private fun Legenda(warna: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(warna, CircleShape))
        Text(label, style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
    }
}

// Nilai di Preview sama dengan bawaan komponen di FigCraft: 07.00–21.00, sekarang 14.00.
@Preview(name = "Bar 24 Jam", widthDp = 329)
@Composable
private fun PreviewBar24Jam() {
    NOTOAPPTheme {
        Box(Modifier.width(329.dp).background(NotoWarna.Putih)) { NotoBar24Jam(7 * 60, 21 * 60, sekarangMenit = 14 * 60) }
    }
}
