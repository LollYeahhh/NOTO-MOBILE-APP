package id.noto.app.ui.stats

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasiRinci

/**
 * Isi "Statistik - Minggu Ini" (node 667) di bawah Tab Periode: kartu hero (y195), Ritme 7 Hari (y356),
 * dan Distribusi Penggunaan (y643). Semua kartu Permukaan, sudut 16, padding 16.
 */
@Composable
fun MingguIniIsi(data: StatistikMingguan) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        KartuHero(data)
        Spacer(Modifier.height(16.5.dp))
        KartuRitme(data)
        Spacer(Modifier.height(16.dp))
        KartuDistribusi(data)
    }
}

// ---------- Kartu hero (node 677) ----------

/** Rata-rata harian, badge perubahan, dan Skor Keseimbangan Batin. Kartu memotong blur dekoratifnya. */
@Composable
private fun KartuHero(data: StatistikMingguan) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(NotoWarna.Permukaan)) {
        BlurHero()
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.padding(vertical = 4.5.dp), verticalArrangement = Arrangement.spacedBy(5.5.dp)) {
                    Text(
                        stringResource(R.string.rata_rata_waktu_layar).uppercase(),
                        style = NotoTeks.Eyebrow,
                        color = NotoWarna.TeksSekunder,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            formatDurasiRinci(data.rataRataMenit),
                            Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.AngkaHero),
                            style = NotoTeks.AngkaHero,
                            color = NotoWarna.TeksUtama,
                        )
                        Text(
                            stringResource(R.string.per_hari),
                            Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.Teks),
                            style = NotoTeks.Teks,
                            color = NotoWarna.TeksSekunder,
                        )
                    }
                }
                // Badge tren: Garis, sudut penuh, padding 4/10, ikon + teks jarak 4.
                Row(
                    Modifier.background(NotoWarna.Garis, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(R.drawable.ic_tren_turun), contentDescription = null)
                    Text(stringResource(R.string.lebih_sadar, data.perubahanPersen), style = NotoTeks.KecilLabel, color = NotoWarna.TeksUtama)
                }
            }
            Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.skor_keseimbangan), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
                    Text(
                        stringResource(R.string.skor_nilai, data.skor, stringResource(data.labelSkor)),
                        style = NotoTeks.KecilTebal,
                        color = NotoWarna.Primer,
                    )
                }
                Box(Modifier.fillMaxWidth().height(8.dp).background(NotoWarna.InfoLatar, CircleShape)) {
                    Box(Modifier.fillMaxWidth(data.skor / 100f).fillMaxHeight().background(NotoWarna.PrimerMuda, CircleShape))
                }
            }
        }
    }
}

/** Ruang di sekeliling lingkaran supaya sebaran blur tidak terpotong kotaknya sendiri (3 × radius blur). */
private val RUANG_BLUR_HERO = 72.dp

/** "Overlay+Blur": lingkaran 128 di (257, 40.5), #C4ECCF 30%, layer blur 24. Android < 12: gradasi radial. */
@Composable
private fun BoxScope.BlurHero() {
    Box(Modifier.matchParentSize()) {
        val warna = NotoWarna.BlurDekoratif.copy(alpha = 0.3f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                Modifier
                    .offset(x = 257.dp - RUANG_BLUR_HERO, y = 40.5.dp - RUANG_BLUR_HERO)
                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .requiredSize(128.dp + RUANG_BLUR_HERO * 2)
                    .blur(24.dp, BlurredEdgeTreatment.Unbounded)
                    .drawBehind { drawCircle(warna, radius = 64.dp.toPx()) },
            )
        } else {
            // shortcut: perkiraan blur untuk API 26–30, belum dibandingkan piksel; cek di HP lama di Tahap 9.
            Box(
                Modifier
                    .offset(x = 257.dp, y = 40.5.dp)
                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .requiredSize(128.dp)
                    .background(Brush.radialGradient(listOf(warna, Color.Transparent))),
            )
        }
    }
}

// ---------- Ritme 7 Hari (node 700) ----------

/** Jarak dasar batang (y116) ke garis Batas Harian (y48): batang setinggi ini = tepat di batas. */
private const val TINGGI_BATAS_DP = 68f

/** Tinggi batang (dp) sebanding menit: menit = batas → setinggi garis Batas Harian. Lewat batas → di atas garis. */
internal fun tinggiBatang(menit: Int, batasMenit: Int): Float = menit * TINGGI_BATAS_DP / batasMenit

/** "Mendekati batas" mulai 90% batas harian; di bawahnya "Sesuai niat sadar". */
internal fun mendekatiBatas(menit: Int, batasMenit: Int): Boolean = menit * 10 >= batasMenit * 9

/** Judul + batas, kanvas grafik setinggi 160 (padding 24/8), lalu legenda dengan garis atas. */
@Composable
private fun KartuRitme(data: StatistikMingguan) {
    Column(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.ritme_7_hari), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
            Text(stringResource(R.string.batas_nilai, formatDurasiRinci(data.batasMenit)), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
        }
        Box(Modifier.fillMaxWidth().height(160.dp)) {
            // Kolom hari: tinggi 96 mulai y40 (padding 24 + 16), jarak 8, isi rata bawah.
            Row(Modifier.padding(top = 40.dp).fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                data.ritme.forEach { KolomHari(it, data.batasMenit) }
            }
            // Garis Batas Harian: putus-putus 3/2 seperti FigCraft.
            Box(
                Modifier.offset(y = 48.dp).fillMaxWidth().height(1.dp).drawBehind {
                    val putus = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 2.dp.toPx()))
                    val y = size.height / 2
                    drawLine(NotoWarna.Garis, Offset(0f, y), Offset(size.width, y), strokeWidth = size.height, pathEffect = putus)
                },
            )
            Text(
                stringResource(R.string.batas_harian),
                Modifier.offset(x = 263.dp, y = 36.dp).background(NotoWarna.Garis).padding(horizontal = 4.dp),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
                maxLines = 1,
                softWrap = false,
            )
        }
        Column {
            Box(Modifier.fillMaxWidth().height(1.dp).background(NotoWarna.Garis))
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Legenda(NotoWarna.Primer, R.string.sesuai_niat_sadar)
                Legenda(NotoWarna.TeksSekunder, R.string.mendekati_batas)
            }
        }
    }
}

/**
 * Satu kolom: nilai (tak terlihat kecuali hari ini), batang lebar 26 sudut atas 8, nama hari; jarak 6.
 * Isinya bisa lebih tinggi dari kolom 96 (Senin), jadi kelebihannya menjulur ke atas seperti di FigCraft.
 */
@Composable
private fun androidx.compose.foundation.layout.RowScope.KolomHari(hari: HariRitme, batasMenit: Int) {
    Box(Modifier.weight(1f).fillMaxHeight()) {
        Column(
            Modifier.align(Alignment.BottomCenter).wrapContentHeight(Alignment.Bottom, unbounded = true),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val (gaya, warna) = if (hari.hariIni) NotoTeks.KecilTebal to NotoWarna.Primer else NotoTeks.KecilTeks to NotoWarna.TeksSekunder
            Text(formatDurasiRinci(hari.menit), Modifier.alpha(if (hari.hariIni) 1f else 0f), style = gaya, color = warna, maxLines = 1, softWrap = false)
            Box(
                Modifier
                    .width(26.dp)
                    .height(tinggiBatang(hari.menit, batasMenit).dp)
                    .background(
                        if (mendekatiBatas(hari.menit, batasMenit)) NotoWarna.TeksSekunder else NotoWarna.Primer,
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    ),
            )
            Text(
                stringResource(hari.hari),
                style = if (hari.hariIni) NotoTeks.KecilTebal else NotoTeks.KecilLabel,
                color = warna,
                maxLines = 1,
            )
        }
    }
}

/** Titik 10 + label, jarak 6. */
@Composable
private fun Legenda(warna: Color, label: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(warna, CircleShape))
        Text(stringResource(label), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
    }
}

// ---------- Distribusi Penggunaan (node 764) ----------

/** Lebar segmen untuk 100% di FigCraft (42% = 136.92). Jumlahnya melebihi track, ujungnya terpotong track. */
private val LEBAR_SEGMEN_PENUH = 326.dp

private val warnaKategori = listOf(NotoWarna.Primer, NotoWarna.PrimerMuda, NotoWarna.Pasif)

@Composable
private fun KartuDistribusi(data: StatistikMingguan) {
    Column(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.distribusi_penggunaan), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
            Text(stringResource(R.string.detail), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BarProporsi(data.kategori)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                data.kategori.forEachIndexed { i, k ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(warnaKategori[i % warnaKategori.size], CircleShape))
                        Text(
                            stringResource(R.string.kategori_persen, stringResource(k.nama), k.persen),
                            style = NotoTeks.KecilTeks,
                            color = NotoWarna.TeksSekunder,
                        )
                    }
                }
            }
        }
        Column(Modifier.padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.aplikasi.forEachIndexed { i, app -> BarisAplikasiMingguan(app, pertama = i == 0) }
        }
    }
}

/** Track Garis tinggi 10 (padding 2, jarak 2), segmen setinggi 6; ujung kiri dan kanan membulat penuh. */
@Composable
private fun BarProporsi(kategori: List<KategoriPersen>) {
    Canvas(Modifier.fillMaxWidth().height(10.dp)) {
        val track = Path().apply { addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(size.height / 2))) }
        clipPath(track) {
            drawPath(track, NotoWarna.Garis)
            val tepi = 2.dp.toPx()
            val tinggi = 6.dp.toPx()
            val bulat = CornerRadius(tinggi / 2)
            var x = tepi
            kategori.forEachIndexed { i, k ->
                val lebar = LEBAR_SEGMEN_PENUH.toPx() * k.persen / 100f
                val kiri = if (i == 0) bulat else CornerRadius.Zero
                val kanan = if (i == kategori.lastIndex) bulat else CornerRadius.Zero
                val segmen = Path().apply {
                    addRoundRect(RoundRect(x, tepi, x + lebar, tepi + tinggi, kiri, kanan, kanan, kiri))
                }
                drawPath(segmen, warnaKategori[i % warnaKategori.size])
                x += lebar + tepi
            }
        }
    }
}

/**
 * Baris aplikasi (padding 4): kotak logo Garis 36 sudut 12 berisi logo 27, nama + keterangan, lalu durasi
 * dan badge selisih rata kanan. Pola lama: item pertama punya jarak 2 di kolom kanan dan durasi
 * berwarna teks sekunder; item lain jarak 0 dan teks utama. Ditiru apa adanya.
 */
@Composable
private fun BarisAplikasiMingguan(app: AplikasiMingguan, pertama: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).background(NotoWarna.Garis, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Image(painterResource(app.logo), contentDescription = null, modifier = Modifier.size(27.dp))
            }
            Column {
                Text(app.nama, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama, maxLines = 1)
                Text(
                    stringResource(app.keterangan, *app.argumen.toTypedArray()),
                    Modifier.tinggiBarisFigCraft(NotoTeks.Teks),
                    style = NotoTeks.Teks,
                    color = NotoWarna.TeksSekunder,
                    maxLines = 1,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(if (pertama) 2.dp else 0.dp)) {
            Text(
                formatDurasiRinci(app.menit),
                style = NotoTeks.JudulItem,
                color = if (pertama) NotoWarna.TeksSekunder else NotoWarna.TeksUtama,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
            val selisih = app.selisihMenit
            val stabil = selisih == null
            Text(
                when {
                    selisih == null -> stringResource(R.string.stabil)
                    selisih < 0 -> "-" + formatDurasiRinci(-selisih)
                    else -> "+" + formatDurasiRinci(selisih)
                },
                Modifier
                    .background(if (stabil) NotoWarna.BlurDekoratif else NotoWarna.Garis, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                style = NotoTeks.KecilTeks,
                color = if (stabil) NotoWarna.Primer else NotoWarna.TeksUtama,
                maxLines = 1,
            )
        }
    }
}

@Preview(name = "Isi Minggu Ini", widthDp = 393)
@Composable
private fun PreviewMingguIniIsi() {
    NOTOAPPTheme { Box(Modifier.background(NotoWarna.Putih)) { MingguIniIsi(contohMingguan) } }
}
