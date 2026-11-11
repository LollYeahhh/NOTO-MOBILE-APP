package id.noto.app.ui.stats

import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatDurasiRinci
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Isi "Statistik - Screen Time" (node 282) di bawah Tab Periode. Pola lama: posisi diambil apa adanya
 * dari frame (total di x143 y202, grafik di y264, Daftar Aplikasi di x18 y378).
 */
@Composable
fun ScreenTimeIsi(data: StatistikHariIni, onAplikasi: (PemakaianHariIni) -> Unit = {}) {
    Column {
        Spacer(Modifier.height(23.dp))
        // Teks total: kotak 78 rata tengah di x143 (tidak persis di tengah layar, seperti desain).
        Text(
            formatDurasiRinci(data.totalMenit),
            Modifier.padding(start = 143.dp).width(78.dp).tinggiBarisFigCraft(NotoTeks.AngkaHero),
            style = NotoTeks.AngkaHero,
            color = NotoWarna.TeksUtama,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
        )
        Spacer(Modifier.height(30.dp))
        GrafikPerJam(data.menitPerJam)
        Spacer(Modifier.height(29.dp))
        Column(Modifier.padding(start = 18.dp).width(357.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            val maks = data.aplikasi.maxOf { it.menit }.coerceAtLeast(1)
            data.aplikasi.forEach { BarisAplikasi(it, it.menit.toFloat() / maks) { onAplikasi(it) } }
        }
    }
}

// Grafik Per Jam (node 441), koordinat di dalam kotak setinggi 85 yang mulai di y264 frame.
private const val MENIT_SKALA = 60f // garis teratas = 1j
private val TINGGI_SKALA = 78.dp // jarak garis 0m ke garis 1j
private val DASAR_GRAFIK = 83.dp // garis 0m
private val JARAK_GARIS = 26.dp
private const val OPACITY_GARIS = 0.17f // frame garis (node 450) ber-opacity 17%
private val LABEL_SUMBU = listOf(60, 40, 20, 0)

/** Batang Primer lebar 11 (jarak 13, mulai x21, sudut atas 2), empat garis 1 dp, label sumbu di x357. */
@Composable
private fun GrafikPerJam(menitPerJam: List<Float>) {
    Box(Modifier.fillMaxWidth().height(85.dp)) {
        Canvas(Modifier.fillMaxWidth().height(85.dp)) {
            val dasar = DASAR_GRAFIK.toPx()
            val lebar = 11.dp.toPx()
            val sudut = CornerRadius(2.dp.toPx())
            menitPerJam.forEachIndexed { i, menit ->
                val tinggi = menit / MENIT_SKALA * TINGGI_SKALA.toPx()
                if (tinggi <= 0f) return@forEachIndexed
                val kiri = (21 + 13 * i).dp.toPx()
                val batang = Path().apply {
                    addRoundRect(RoundRect(kiri, dasar - tinggi, kiri + lebar, dasar, topLeftCornerRadius = sudut, topRightCornerRadius = sudut))
                }
                drawPath(batang, NotoWarna.Primer)
            }
            // Garis digambar di atas batang (urutan layer di FigCraft). Tebal dan posisinya dibulatkan ke piksel
            // utuh supaya garis tajam seperti FigCraft, tidak melebar abu-abu di antara dua baris piksel.
            val tebal = 1.dp.toPx().roundToInt().coerceAtLeast(1).toFloat()
            repeat(4) { i ->
                val atas = floor(dasar - i * JARAK_GARIS.toPx() - tebal / 2 + 0.5f)
                drawRect(NotoWarna.TeksUtama.copy(alpha = OPACITY_GARIS), Offset(15.dp.toPx(), atas), Size(337.dp.toPx(), tebal))
            }
        }
        LABEL_SUMBU.forEachIndexed { i, menit ->
            Text(
                formatDurasi(menit),
                Modifier.offset(x = 357.dp, y = (-3.5).dp + JARAK_GARIS * i),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksUtama,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/** Satu baris (tinggi 30): logo 30, nama + bar progres (lebar 271, jarak 5), durasi; diratakan spaceBetween. */
@Composable
private fun BarisAplikasi(app: PemakaianHariIni, porsi: Float, onKlik: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(app.logo), contentDescription = null, modifier = Modifier.size(30.dp))
        Column(Modifier.width(271.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(app.nama, style = NotoTeks.Label, color = NotoWarna.TeksUtama, maxLines = 1)
            Box(Modifier.fillMaxWidth().height(4.dp).background(NotoWarna.InfoLatar, RoundedCornerShape(4.dp))) {
                Box(Modifier.fillMaxWidth(porsi).fillMaxHeight().background(NotoWarna.PrimerMuda, RoundedCornerShape(4.dp)))
            }
        }
        Text(formatDurasiRinci(app.menit), style = NotoTeks.KecilLabel, color = NotoWarna.TeksUtama, maxLines = 1)
    }
}

@Preview(name = "Isi Screen Time", widthDp = 393)
@Composable
private fun PreviewScreenTimeIsi() {
    NOTOAPPTheme { Box(Modifier.background(NotoWarna.Putih)) { ScreenTimeIsi(contohHariIni) } }
}
