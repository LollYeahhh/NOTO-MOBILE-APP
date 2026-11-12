package id.noto.app.ui.stats

import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.painterAplikasi
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoTeksLama
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatDurasiRinci
import java.time.Month
import java.time.format.TextStyle

/**
 * Isi "Statistik - Bulan Ini" (node 460) di bawah Tab Periode: Ringkasan (y195), Penggunaan 4 Minggu (y412),
 * dan Top List Aplikasi Bulanan (y698). Semua kartu Permukaan, sudut 16, padding 16, jarak isi 12.
 */
@Composable
fun BulanIniIsi(data: StatistikBulanan, onAplikasi: (AplikasiBulanan) -> Unit = {}) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        KartuRingkasan(data)
        Spacer(Modifier.height(16.5.dp))
        KartuEmpatMinggu(data)
        Spacer(Modifier.height(16.dp))
        KartuTopList(data, onAplikasi)
    }
}

@Composable
private fun Kartu(isi: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) { isi() }
}

// ---------- Ringkasan Penggunaan Bulanan (node 489) ----------

@Composable
private fun KartuRingkasan(data: StatistikBulanan) = Kartu {
    val bahasa = LocalConfiguration.current.locales[0]
    val namaBulan = Month.of(data.bulan).getDisplayName(TextStyle.FULL_STANDALONE, bahasa)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(stringResource(R.string.ringkasan_bulan, namaBulan).uppercase(bahasa), style = NotoTeks.Eyebrow, color = NotoWarna.TeksSekunder)
        Text(
            stringResource(R.string.penggunaan_dipantau),
            Modifier.tinggiBarisFigCraft(NotoTeks.Teks),
            style = NotoTeks.Teks,
            color = NotoWarna.TeksSekunder,
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            formatDurasi(data.totalMenit),
            Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.AngkaHero),
            style = NotoTeks.AngkaHero,
            color = NotoWarna.Primer,
        )
        Text(
            stringResource(R.string.total_penggunaan),
            Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.Teks),
            style = NotoTeks.Teks,
            color = NotoWarna.TeksSekunder,
        )
    }
    // Panel putih (margin 12 atas-bawah): rata-rata harian di kiri, bulan lalu di kanan.
    Row(
        Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .background(NotoWarna.Putih, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).background(NotoWarna.Permukaan, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.ic_lama_bulan_ini), contentDescription = null, modifier = Modifier.size(15.dp))
            }
            Column {
                Text(stringResource(R.string.rata_rata_harian), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
                // Dua teks rata bawah (desain: "2j 15m " dengan spasi di belakang, lalu selisih dalam kurung).
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(formatDurasiRinci(data.rataRataMenit) + " ", style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama, maxLines = 1)
                    val selisih = data.rataRataMenit - data.bulanLaluMenit
                    val tanda = if (selisih < 0) "-" else "+"
                    Text(
                        stringResource(R.string.selisih_kurung, tanda + formatDurasiRinci(kotlin.math.abs(selisih))),
                        Modifier.tinggiBarisFigCraft(NotoTeks.Teks),
                        style = NotoTeks.Teks,
                        color = NotoWarna.Primer,
                        maxLines = 1,
                    )
                }
            }
        }
        // Pola lama: nilai bulan lalu berakhir 3 dp sebelum tepi kanan label "Bulan Lalu".
        Column(Modifier.padding(vertical = 4.5.dp), verticalArrangement = Arrangement.spacedBy(1.5.dp), horizontalAlignment = Alignment.End) {
            Text(stringResource(R.string.bulan_lalu), style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder, textAlign = TextAlign.End)
            Text(
                formatDurasiRinci(data.bulanLaluMenit),
                Modifier.padding(end = 3.dp).tinggiBarisFigCraft(NotoTeks.Label),
                style = NotoTeks.Label,
                color = NotoWarna.TeksSekunder,
                textAlign = TextAlign.End,
            )
        }
    }
}

// ---------- Penggunaan <bulan> (node 513) ----------

/** Tinggi batang pekan dengan pemakaian terbanyak; pekan lain sebanding (FigCraft: 170m = 75). */
private val TINGGI_BATANG_MAKS = 75f

@Composable
private fun KartuEmpatMinggu(data: StatistikBulanan) = Kartu {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        val bahasa = LocalConfiguration.current.locales[0]
        Text(stringResource(R.string.penggunaan_bulan, Month.of(data.bulan).getDisplayName(TextStyle.FULL_STANDALONE, bahasa)), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
        // Chip: Biru pucet, sudut penuh, padding 2/8, jarak 4. Disembunyikan kalau tidak turun (pilihan pemilik proyek).
        if (data.turunPersen > 0) Row(
            Modifier.background(NotoWarna.InfoLatar, CircleShape).padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Ikon chip (node 520) masih kosong di FigCraft, sedang dicek pemilik proyek: ruangnya 8×8 dibiarkan kosong.
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.persen_turun, data.turunPersen), style = NotoTeks.KecilLabel, color = NotoWarna.Info)
        }
    }
    // Kanvas setinggi 160 (padding 24/8); kolom pekan setinggi 96 mulai y40, jarak 8, isi rata bawah.
    Box(Modifier.fillMaxWidth().height(160.dp)) {
        Row(Modifier.padding(top = 40.dp).fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val maks = data.menitPerPekan.maxOrNull()?.coerceAtLeast(1) ?: 1
            data.menitPerPekan.forEachIndexed { i, menit ->
                KolomPekan(i + 1, menit, menit * TINGGI_BATANG_MAKS / maks, sorot = i + 1 == data.pekanIni)
            }
        }
    }
    // Legenda: garis atas 1 digambar di dalam padding 8 (tinggi 30), berbeda dengan Ritme 7 Hari.
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind { drawLine(NotoWarna.Garis, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx()) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendaPekan(NotoWarna.Pasif, stringResource(R.string.pekan_lain))
        LegendaPekan(NotoWarna.Primer, stringResource(R.string.pekan_ini, data.pekanIni))
    }
}

/** Nilai, batang lebar 26 sudut atas 8, nama pekan; jarak 6. Pekan berjalan: Primer dan SemiBold. */
@Composable
private fun RowScope.KolomPekan(pekan: Int, menit: Int, tinggi: Float, sorot: Boolean) {
    val (gaya, warnaTeks) = if (sorot) NotoTeks.KecilTebal to NotoWarna.Primer else NotoTeks.KecilLabel to NotoWarna.TeksSekunder
    Box(Modifier.weight(1f).fillMaxHeight()) {
        Column(
            Modifier.align(Alignment.BottomCenter).wrapContentHeight(Alignment.Bottom, unbounded = true),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.menit_singkat, menit),
                style = if (sorot) NotoTeks.KecilTebal else NotoTeks.KecilTeks,
                color = warnaTeks,
                maxLines = 1,
                softWrap = false,
            )
            Box(
                Modifier
                    .width(26.dp)
                    .height(tinggi.dp)
                    .background(if (sorot) NotoWarna.Primer else NotoWarna.Pasif, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            )
            Text(stringResource(R.string.pekan_n, pekan), style = gaya, color = warnaTeks, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
private fun LegendaPekan(warna: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(warna, CircleShape))
        Text(label, style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
    }
}

// ---------- Top List Aplikasi Bulanan (node 557) ----------

@Composable
private fun KartuTopList(data: StatistikBulanan, onAplikasi: (AplikasiBulanan) -> Unit) = Kartu {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.top_list_bulanan), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
        Text(stringResource(R.string.top_list_ket), Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
    }
    // Margin 12 atas-bawah, lalu daftar dengan padding 4 dan jarak 8.
    Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.aplikasi.forEach { BarisAplikasiBulanan(it) { onAplikasi(it) } }
    }
}

/** Baris (Permukaan sudut 12, padding 8, tinggi 52): kotak logo Garis 36 berisi logo 27, nama + jumlah buka, durasi. */
@Composable
private fun BarisAplikasiBulanan(app: AplikasiBulanan, onKlik: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
            .background(NotoWarna.Permukaan, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).background(NotoWarna.Garis, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Image(painterAplikasi(app.ikon, app.logo), contentDescription = null, modifier = Modifier.size(27.dp))
            }
            Column {
                Text(app.nama, Modifier.tinggiBarisFigCraft(NotoTeksLama.JudulBulanIni), style = NotoTeksLama.JudulBulanIni, color = NotoWarna.TeksUtama, maxLines = 1)
                Text(
                    stringResource(R.string.kali_dibuka, app.dibuka),
                    Modifier.tinggiBarisFigCraft(NotoTeksLama.KeteranganBulanIni),
                    style = NotoTeksLama.KeteranganBulanIni,
                    color = NotoWarna.TeksSekunder,
                    maxLines = 1,
                )
            }
        }
        Text(formatDurasiRinci(app.menit), style = NotoTeks.JudulItem, color = NotoWarna.TeksSekunder, textAlign = TextAlign.End, maxLines = 1)
    }
}

@Preview(name = "Isi Bulan Ini", widthDp = 393)
@Composable
private fun PreviewBulanIniIsi() {
    NOTOAPPTheme { Box(Modifier.background(NotoWarna.Putih)) { BulanIniIsi(contohBulanan) } }
}
