package id.noto.app.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.painterAplikasi
import id.noto.app.data.Aturan
import id.noto.app.data.hariKeBitmask
import id.noto.app.ui.blocks.contohAplikasi
import id.noto.app.ui.blocks.jadwalRingkas
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.settings.IsiSetelan
import id.noto.app.ui.settings.SeksiSetelan
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatDurasiRinci
import id.noto.app.util.formatJam
import kotlin.math.ceil

/**
 * "Statistik - Detail Aplikasi" (node 5725): kartu pemakaian hari ini dengan grafik per jam,
 * seksi "Hari ini", lalu aturan yang memuat aplikasi ini.
 * [aturan] null = aplikasi tidak ada di aturan mana pun: seksi aturan disembunyikan (disepakati b.1),
 * batang grafik tidak dibedakan dalam/luar jadwal, dan baris yang menyangkut aturan tidak ditampilkan.
 */
@Composable
fun DetailAplikasiScreen(
    app: AplikasiStatistik,
    aturan: Aturan?,
    onKembali: () -> Unit,
    onAturan: () -> Unit,
    // Kategori dari daftar Pilih Aplikasi; aplikasi di luar daftar itu hanya bertuliskan "Hari ini".
    kategori: Int? = contohAplikasi.firstOrNull { it.nama == app.nama }?.kategori,
    // Data asli hari ini; null = data contoh (Preview, aplikasi contoh Minggu Ini sampai Tahap 8C).
    harian: HarianAplikasi? = null,
    // Kejadian asli hari ini; null = data contoh.
    kejadian: KejadianAplikasi? = null,
) {
    val hariIni = contohHariIniAplikasi
    val napas = kejadian?.latihanNapas ?: hariIni.latihanNapas
    val batal = kejadian?.batalMembuka ?: hariIni.batalMembuka
    val timeOut = kejadian?.timeOut ?: hariIni.timeOut
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(app.nama, onIkonKiri = onKembali)
        IsiSetelan {
            KartuPemakaian(app, kategori, aturan, harian)
            SeksiSetelan(R.string.hari_ini) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_aplikasi),
                    stringResource(R.string.dibuka),
                    keterangan = stringResource(R.string.kemarin_kali, harian?.dibukaKemarin ?: hariIni.dibukaKemarin),
                    nilai = stringResource(R.string.jumlah_kali, harian?.dibuka ?: hariIni.dibuka),
                    onKlik = null,
                )
                if (aturan != null) {
                    NotoBarisSetelan(
                        painterResource(R.drawable.ic_jeda),
                        stringResource(R.string.latihan_napas_judul),
                        keterangan = stringResource(R.string.batal_membuka, batal),
                        nilai = stringResource(R.string.jumlah_kali, napas),
                        onKlik = null,
                    )
                    NotoBarisSetelan(
                        painterResource(R.drawable.ic_jam_pasir),
                        stringResource(R.string.kena_time_out),
                        // Belum pernah habis hari ini: tanpa keterangan (tidak ada jam untuk ditulis).
                        keterangan = when {
                            kejadian == null -> stringResource(R.string.kuota_habis_pukul, aturan.nama, hariIni.jamTimeOut)
                            kejadian.jamTimeOut == null -> null
                            else -> stringResource(R.string.kuota_habis_pukul, kejadian.aturanTimeOut.orEmpty(), kejadian.jamTimeOut)
                        },
                        nilai = stringResource(R.string.jumlah_kali, timeOut),
                        onKlik = null,
                    )
                }
            }
            if (aturan != null) {
                SeksiSetelan(R.string.aturan_memuat_aplikasi) {
                    NotoBarisSetelan(
                        painterResource(R.drawable.ic_blokir),
                        aturan.nama,
                        keterangan = aturan.jadwalRingkas(),
                        nilai = formatDurasi(aturan.kuotaMenit),
                        onKlik = onAturan,
                    )
                }
            }
        }
    }
}

/** "Sosial Media" → "Sosial media", seperti teks di FigCraft. */
private fun String.hurufKalimat(): String = lowercase().replaceFirstChar { it.uppercase() }

/** Kartu (node 5728): Permukaan sudut 16, padding 16, jarak 12. */
@Composable
private fun KartuPemakaian(app: AplikasiStatistik, kategori: Int?, jadwal: Aturan?, harian: HarianAplikasi?) {
    Column(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Daftar (padding atas-bawah 4) berisi baris aplikasi (padding 4): kotak logo Garis 36 berisi logo 27,
        // nama + keterangan, total di kanan.
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).background(NotoWarna.Garis, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Image(painterAplikasi(app.ikon, app.logo), contentDescription = null, modifier = Modifier.size(27.dp))
                }
                Column {
                    Text(app.nama, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama, maxLines = 1)
                    Text(
                        if (kategori == null) stringResource(R.string.hari_ini) else stringResource(R.string.aplikasi_hari_ini, stringResource(kategori).hurufKalimat()),
                        Modifier.tinggiBarisFigCraft(NotoTeks.Teks),
                        style = NotoTeks.Teks,
                        color = NotoWarna.TeksSekunder,
                        maxLines = 1,
                    )
                }
            }
            Text(formatDurasiRinci(harian?.menit ?: app.menit), style = NotoTeks.AngkaHero, color = NotoWarna.TeksUtama, maxLines = 1)
        }
        Text(stringResource(R.string.pemakaian_per_jam), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
        GrafikJam(harian?.menitPerJam ?: contohMenitPerJam(app.menit), jadwal?.let { it.mulaiMenit to it.selesaiMenit })
        if (jadwal != null) {
            // Legenda (node 5918): garis atas 1, padding 8 atas-bawah, dua item rata kiri-kanan.
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(NotoWarna.Garis)
                    .padding(top = 1.dp)
                    .background(NotoWarna.Permukaan)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LegendaJam(NotoWarna.Primer, stringResource(R.string.dalam_jadwal, stringResource(R.string.rentang_singkat, formatJam(jadwal.mulaiMenit), formatJam(jadwal.selesaiMenit))))
                LegendaJam(NotoWarna.Pasif, stringResource(R.string.di_luar_jadwal))
            }
        }
    }
}

// Grafik per jam (node 5883): kotak 329 x 116. Garis 20m/10m/0m di y 8/48/88 mulai x28 selebar 301,
// 24 batang selebar 8 di tengah kolomnya (301/24), dasar di y88, tinggi penuh 80.
private val KIRI_GRAFIK = 28.dp
private val LEBAR_GRAFIK = 301.dp
private val ATAS_GRAFIK = 8.dp
private val DASAR_GRAFIK = 88.dp
private val LEBAR_BATANG = 8.dp
private val TINGGI_MIN = 2.dp
// Posisi kiri label jam 00.00, 06.00, 12.00, 18.00 di FigCraft.
private val KIRI_LABEL_JAM = listOf(25.dp, 96.5.dp, 171.8.dp, 247.dp)

/**
 * Batas atas sumbu: kelipatan 10 menit terdekat di atas batang tertinggi, paling kecil 10
 * (contoh di desain: batang tertinggi 12m → 20m).
 */
internal fun batasSumbu(menitPerJam: List<Float>): Int =
    (ceil((menitPerJam.maxOrNull() ?: 0f) / 10f).toInt() * 10).coerceAtLeast(10)

/** Jam ke-[jam] (0–23) ada di dalam jadwal [mulaiMenit]–[selesaiMenit] bila awal jam itu masuk rentangnya. */
internal fun dalamJadwal(jam: Int, mulaiMenit: Int, selesaiMenit: Int): Boolean = jam * 60 in mulaiMenit until selesaiMenit

/**
 * Batang Primer = dalam jadwal, Pasif = di luar jadwal, Garis setinggi 2 = tidak dipakai.
 * [jadwal] null = aplikasi tanpa aturan: semua batang Primer.
 */
@Composable
private fun GrafikJam(menitPerJam: List<Float>, jadwal: Pair<Int, Int>?) {
    val batas = batasSumbu(menitPerJam)
    Box(Modifier.fillMaxWidth().height(116.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val kiri = KIRI_GRAFIK.toPx()
            val lebar = LEBAR_GRAFIK.toPx()
            val dasar = DASAR_GRAFIK.toPx()
            val penuh = dasar - ATAS_GRAFIK.toPx()
            val tebal = 1.dp.toPx()
            listOf(0f, 0.5f, 1f).forEach { f ->
                drawRect(NotoWarna.Garis, Offset(kiri, ATAS_GRAFIK.toPx() + penuh * f), Size(lebar, tebal))
            }
            val kolom = lebar / 24
            val lebarBatang = LEBAR_BATANG.toPx()
            val sudut = CornerRadius(2.dp.toPx())
            menitPerJam.forEachIndexed { jam, menit ->
                val warna = when {
                    menit <= 0f -> NotoWarna.Garis
                    jadwal == null || dalamJadwal(jam, jadwal.first, jadwal.second) -> NotoWarna.Primer
                    else -> NotoWarna.Pasif
                }
                val tinggi = maxOf(TINGGI_MIN.toPx(), menit / batas * penuh)
                val x = kiri + jam * kolom + (kolom - lebarBatang) / 2
                val batang = Path().apply {
                    addRoundRect(RoundRect(x, dasar - tinggi, x + lebarBatang, dasar, topLeftCornerRadius = sudut, topRightCornerRadius = sudut))
                }
                drawPath(batang, warna)
            }
        }
        // Label sumbu kiri, 7 di atas garisnya.
        listOf(batas, batas / 2, 0).forEachIndexed { i, menit ->
            Text(
                formatDurasi(menit),
                Modifier.offset(y = ATAS_GRAFIK - 7.dp + (DASAR_GRAFIK - ATAS_GRAFIK) / 2 * i),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
                maxLines = 1,
            )
        }
        KIRI_LABEL_JAM.forEachIndexed { i, x ->
            Text(
                String.format(java.util.Locale.ROOT, "%02d.00", i * 6),
                Modifier.offset(x = x, y = DASAR_GRAFIK + 8.dp).width(40.dp),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
                maxLines = 1,
            )
        }
    }
}

/** Titik 10 + label (NOTO/Kecil Label), jarak 6. */
@Composable
private fun LegendaJam(warna: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(warna, CircleShape))
        Text(label, style = NotoTeks.KecilLabel, color = NotoWarna.TeksSekunder)
    }
}

@Preview(name = "Statistik - Detail Aplikasi", widthDp = 393, heightDp = 1000)
@Composable
private fun PreviewDetailAplikasi() {
    NOTOAPPTheme { DetailAplikasiScreen(
            AplikasiStatistik(R.drawable.logo_instagram, "Instagram", 70),
            Aturan(nama = "Sosmed", hariAktif = hariKeBitmask(setOf(0, 1, 2, 3, 4)), mulaiMenit = 7 * 60, selesaiMenit = 21 * 60, kuotaMenit = 90),
            {}, {},
        ) }
}
