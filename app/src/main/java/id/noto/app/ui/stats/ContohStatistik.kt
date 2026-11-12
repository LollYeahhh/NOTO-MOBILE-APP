package id.noto.app.ui.stats

import androidx.compose.ui.graphics.ImageBitmap
import id.noto.app.R

// Data contoh layar Statistik, persis seperti FigCraft. Data asli menggantikannya di Tahap 8.
// Logo pihak ketiga hanya untuk data contoh (CLAUDE.md §8): hapus bersama file ini sebelum rilis.

/**
 * Pemakaian satu aplikasi hari ini, untuk Daftar Aplikasi di Screen Time.
 * [logo] = drawable data contoh (Preview); data asli memakai [ikon] dari sistem dan [paket].
 */
data class PemakaianHariIni(val logo: Int?, val nama: String, val menit: Int, val ikon: ImageBitmap? = null, val paket: String = nama)

/** Isi "Statistik - Screen Time": total hari ini, pemakaian per jam (menit), dan daftar aplikasi. */
data class StatistikHariIni(val totalMenit: Int, val menitPerJam: List<Float>, val aplikasi: List<PemakaianHariIni>)

val contohHariIni = StatistikHariIni(
    totalMenit = 4 * 60 + 13,
    // Tinggi batang di FigCraft (2, 29, … px) diubah ke menit dengan skala grafik 1j = 78.
    menitPerJam = listOf(2, 29, 51, 79, 74, 42, 63).map { it * 60f / 78f },
    aplikasi = listOf(
        PemakaianHariIni(R.drawable.logo_whatsapp_kotak, "WhatsApp", 120),
        PemakaianHariIni(R.drawable.logo_youtube_kotak, "YouTube", 105),
        PemakaianHariIni(R.drawable.logo_discord_kotak, "Discord", 90),
        PemakaianHariIni(R.drawable.logo_pinterest_kotak, "Pinterest", 75),
        PemakaianHariIni(R.drawable.logo_gmail_kotak, "Gmail", 45),
        PemakaianHariIni(R.drawable.logo_twitter_kotak, "Twitter", 30),
        PemakaianHariIni(R.drawable.logo_instagram_kotak, "Instagram", 15),
        PemakaianHariIni(R.drawable.logo_facebook_kotak, "Facebook", 4),
    ),
)

/** Satu hari di Ritme 7 Hari. Tinggi dan warna batang dihitung dari [menit] dan batas harian (MingguIniIsi). */
data class HariRitme(val hari: Int, val menit: Int, val hariIni: Boolean = false)

/** Satu kategori di bar proporsi Distribusi Penggunaan. */
data class KategoriPersen(val nama: Int, val persen: Int)

/** Satu aplikasi di Distribusi Penggunaan. [selisihMenit] null = "Stabil". [logo] = contoh; data asli memakai [ikon]. */
data class AplikasiMingguan(
    val logo: Int?,
    val nama: String,
    val keterangan: Int,
    val argumen: List<Int>,
    val menit: Int,
    val selisihMenit: Int?,
    val ikon: ImageBitmap? = null,
    val paket: String = nama,
)

/**
 * Isi "Statistik - Minggu Ini". [perubahanPersen] negatif = turun (badge "Lebih Sadar"); ≥ 0 = badge disembunyikan.
 * [batasMenit] null = belum ada aturan aktif: garis dan teks batas disembunyikan.
 */
data class StatistikMingguan(
    val rataRataMenit: Int,
    val perubahanPersen: Int,
    val skor: Int,
    val labelSkor: Int,
    val batasMenit: Int?,
    val ritme: List<HariRitme>,
    val kategori: List<KategoriPersen>,
    val aplikasi: List<AplikasiMingguan>,
)

val contohMingguan = StatistikMingguan(
    rataRataMenit = 2 * 60 + 45,
    perubahanPersen = -34,
    skor = 88,
    labelSkor = R.string.skor_tenang,
    batasMenit = 3 * 60,
    ritme = listOf(
        HariRitme(R.string.hari_sen, 130),
        HariRitme(R.string.hari_sel, 105),
        HariRitme(R.string.hari_rab, 175),
        HariRitme(R.string.hari_kam, 135, hariIni = true),
        HariRitme(R.string.hari_jum, 90),
        HariRitme(R.string.hari_sab, 190),
        HariRitme(R.string.hari_min, 125),
    ),
    kategori = listOf(
        KategoriPersen(R.string.kategori_sosmed, 42),
        KategoriPersen(R.string.kategori_video, 35),
        KategoriPersen(R.string.kategori_karya, 23),
    ),
    aplikasi = listOf(
        AplikasiMingguan(R.drawable.logo_instagram, "Instagram", R.string.jeda_napas_sadar, listOf(3), 70, -18),
        AplikasiMingguan(R.drawable.logo_tiktok, "TikTok", R.string.jeda_tertahan, listOf(5, 2), 55, -45),
        AplikasiMingguan(R.drawable.logo_youtube_kecil, "YouTube", R.string.terkendali_terjadwal, emptyList(), 40, null),
    ),
)

/** Satu aplikasi di Top List Aplikasi Bulanan. */
data class AplikasiBulanan(
    val logo: Int?,
    val nama: String,
    val dibuka: Int,
    val menit: Int,
    val ikon: ImageBitmap? = null,
    val paket: String = nama,
)

/** Isi "Statistik - Bulan Ini". [bulan] 1–12; [menitPerPekan] berisi 4 pekan, pekan terakhir disorot. */
data class StatistikBulanan(
    val bulan: Int,
    val totalMenit: Int,
    val rataRataMenit: Int,
    val bulanLaluMenit: Int,
    val turunPersen: Int,
    val menitPerPekan: List<Int>,
    val aplikasi: List<AplikasiBulanan>,
)

val contohBulanan = StatistikBulanan(
    bulan = 10,
    totalMenit = 62 * 60,
    rataRataMenit = 2 * 60 + 15,
    bulanLaluMenit = 3 * 60 + 20,
    turunPersen = 32,
    menitPerPekan = listOf(170, 150, 130, 115),
    aplikasi = listOf(
        AplikasiBulanan(R.drawable.logo_instagram, "Instagram", 486, 18 * 60 + 20),
        AplikasiBulanan(R.drawable.logo_whatsapp, "WhatsApp", 372, 14 * 60),
        AplikasiBulanan(R.drawable.logo_tiktok, "TikTok", 294, 12 * 60 + 10),
    ),
)

/** Daftar lengkap "Statistik - Detail Distribusi" (node 5558): aplikasi Minggu Ini, ditambah X dan Facebook. */
val contohDistribusiLengkap = contohMingguan.aplikasi + listOf(
    AplikasiMingguan(R.drawable.logo_twitter, "X", R.string.dibuka_per_hari, listOf(9), 25, null),
    AplikasiMingguan(R.drawable.logo_facebook, "Facebook", R.string.dibuka_per_hari, listOf(4), 15, null),
)

/** Aplikasi yang dibuka di Detail Aplikasi: logo, nama, dan total menit dari baris yang diketuk. */
data class AplikasiStatistik(val logo: Int?, val nama: String, val menit: Int, val ikon: ImageBitmap? = null, val paket: String = nama)

/** Angka seksi "Hari ini" di Detail Aplikasi. */
data class HariIniAplikasi(
    val dibuka: Int,
    val dibukaKemarin: Int,
    val latihanNapas: Int,
    val batalMembuka: Int,
    val timeOut: Int,
    val jamTimeOut: String,
)

/** Pemakaian per jam Instagram di FigCraft (node 5883), jumlahnya 70 menit. */
private val POLA_PER_JAM = listOf(0, 0, 0, 0, 0, 0, 0, 5, 8, 2, 0, 3, 10, 6, 0, 0, 4, 2, 0, 7, 12, 8, 3, 0)

// shortcut: semua aplikasi memakai pola Instagram yang disesuaikan dengan totalnya, dan angka "Hari ini" yang sama
// (disepakati a.1). Diganti data asli dari UsageStatsManager di Tahap 8.
fun contohMenitPerJam(totalMenit: Int): List<Float> = POLA_PER_JAM.map { it * totalMenit / 70f }

val contohHariIniAplikasi = HariIniAplikasi(
    dibuka = 23,
    dibukaKemarin = 27,
    latihanNapas = 3,
    batalMembuka = 2,
    timeOut = 1,
    jamTimeOut = "20.40",
)
