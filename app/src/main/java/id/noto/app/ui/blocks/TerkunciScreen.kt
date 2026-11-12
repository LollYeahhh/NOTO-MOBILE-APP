package id.noto.app.ui.blocks

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBadge
import id.noto.app.ui.components.NotoHeaderLogo
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeBadge
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam

/** Isi layar Terkunci: aturan yang habis kuotanya, aplikasi yang dibuka, jam bebas lagi, dan sisa waktunya. */
data class InfoTerkunci(
    val namaAturan: String,
    val kuotaMenit: Int,
    val aplikasi: String,
    val bebasMenit: Int,
    val sisaMenit: Int,
)

/** Data contoh dari FigCraft untuk Preview; data asli diisi TerkunciActivity. */
val contohTerkunci = InfoTerkunci("Sosmed", 90, "Instagram", bebasMenit = 21 * 60, sisaMenit = 134)

/** Warna efek blur dekoratif dari FigCraft (pengecualian hex langsung, lihat CLAUDE.md §8). */
private val WarnaBlur = Color(0xFFC4ECCF)

/**
 * "Blokir - Terkunci Time Out" (node 2976): latar Permukaan, Header Tipe=Logo, blur dekoratif,
 * Konten (Ikon Jeda, Pesan, Panel Napas; jarak 24) di y 196, dan Aksi di bawah. Tanpa Bottom Nav.
 * Saat time out tidak ada tombol tambahan waktu (CLAUDE.md §5).
 * [tampilNapas] dan [tampilSisa] = setelan "Latihan napas" dan "Tampilkan sisa waktu" (Aturan Pembatasan).
 */
@Composable
fun TerkunciScreen(
    info: InfoTerkunci,
    onKembali: () -> Unit,
    onStatistik: () -> Unit,
    tampilNapas: Boolean = true,
    tampilSisa: Boolean = true,
) {
    Box(Modifier.fillMaxSize().background(NotoWarna.Permukaan)) {
        BlurDekoratif(Modifier.offset(x = 46.dp, y = 150.dp).size(300.dp))
        Column(
            Modifier.fillMaxWidth().padding(start = 16.dp, top = 196.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IkonJeda()
            Pesan(info, tampilSisa)
            if (tampilNapas) PanelNapas()
        }
        // Aksi: bawahnya 52 dari tepi layar (y 700 + 100 di frame 852), jarak 4.
        Column(
            Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 52.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            NotoTombol(stringResource(R.string.kembali_layar_utama), onKlik = onKembali)
            NotoTombol(stringResource(R.string.lihat_statistik_hari_ini), onKlik = onStatistik, tipe = TipeTombol.Teks)
        }
        NotoHeaderLogo()
    }
}

/** Ruang kosong di sekeliling lingkaran blur, supaya sebaran blur tidak terpotong tepi kotaknya. */
private val RUANG_BLUR = 270.dp

/** Radius blur Compose yang sebarannya setara layer blur 90 di FigCraft (disetel dari perbandingan piksel). */
private val RADIUS_BLUR = 90.dp

/** Lingkaran #C4ECCF 300, opacity 70%, layer blur 90. Android < 12 tidak punya blur: pakai gradasi radial. */
@Composable
private fun BlurDekoratif(modifier: Modifier) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Box(
            // requiredSize melebar sama rata ke semua sisi dari kotak 300, lingkarannya tetap di tengah.
            modifier
                .requiredSize(300.dp + RUANG_BLUR * 2)
                .alpha(0.7f)
                .blur(RADIUS_BLUR, BlurredEdgeTreatment.Unbounded)
                .drawBehind { drawCircle(WarnaBlur, radius = 150.dp.toPx()) },
        )
    } else {
        // shortcut: perkiraan blur untuk API 26–30, belum dibandingkan piksel; cek di HP lama di Tahap 9.
        Box(
            modifier.alpha(0.7f).background(
                Brush.radialGradient(listOf(WarnaBlur, WarnaBlur.copy(alpha = 0.5f), Color.Transparent)),
            ),
        )
    }
}

/** Ikon Jeda: lingkaran Putih 96, cincin 48 garis 5 (di dalam) teks utama, jarum 5×18 primer muda. */
@Composable
private fun IkonJeda() {
    Canvas(Modifier.size(96.dp).background(NotoWarna.Putih, CircleShape)) {
        val tebal = 5.dp.toPx()
        drawCircle(NotoWarna.TeksUtama, radius = (48.dp.toPx() - tebal) / 2, style = Stroke(tebal))
        drawRoundRect(
            NotoWarna.PrimerMuda,
            topLeft = Offset(45.5.dp.toPx(), 31.dp.toPx()),
            size = Size(tebal, 18.dp.toPx()),
            cornerRadius = CornerRadius(tebal / 2),
        )
    }
}

/** Pesan (jarak 8, rata tengah): eyebrow, judul, isi, Badge Info. */
@Composable
private fun Pesan(info: InfoTerkunci, tampilSisa: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.terkunci_eyebrow, info.namaAturan).uppercase(),
            Modifier.tinggiBarisFigCraft(NotoTeks.Eyebrow),
            style = NotoTeks.Eyebrow,
            color = NotoWarna.Primer,
        )
        Text(stringResource(R.string.terkunci_judul), Modifier.tinggiBarisFigCraft(NotoTeks.AngkaHero), style = NotoTeks.AngkaHero, color = NotoWarna.TeksUtama)
        Text(
            stringResource(R.string.terkunci_isi, info.namaAturan, formatDurasi(info.kuotaMenit), info.aplikasi, formatJam(info.bebasMenit)),
            Modifier.fillMaxWidth().tinggiBarisFigCraft(NotoTeks.Teks),
            style = NotoTeks.Teks,
            color = NotoWarna.TeksSekunder,
            textAlign = TextAlign.Center,
        )
        if (tampilSisa) NotoBadge(stringResource(R.string.terkunci_bebas_lagi, formatDurasi(info.sisaMenit)), TipeBadge.Info)
    }
}

/** Panel Napas: Putih sudut 16, padding 16, kotak ikon Permukaan 40 (sudut 12) + teks, jarak 12. */
@Composable
private fun PanelNapas() {
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.Putih, RoundedCornerShape(16.dp)).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).background(NotoWarna.Permukaan, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.ic_jeda), contentDescription = null, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.napas_judul), Modifier.tinggiBarisFigCraft(NotoTeks.JudulItem), style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama)
            Text(stringResource(R.string.napas_ket), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
    }
}

@Preview(name = "Blokir - Terkunci Time Out", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTerkunci() {
    NOTOAPPTheme { TerkunciScreen(contohTerkunci, {}, {}) }
}
