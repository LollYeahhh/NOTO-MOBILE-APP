package id.noto.app.ui.stats

import id.noto.app.R

// Data contoh layar Statistik, persis seperti FigCraft. Data asli menggantikannya di Tahap 8.
// Logo pihak ketiga hanya untuk data contoh (CLAUDE.md §8): hapus bersama file ini sebelum rilis.

/** Pemakaian satu aplikasi hari ini, untuk Daftar Aplikasi di Screen Time. */
data class PemakaianHariIni(val logo: Int, val nama: String, val menit: Int)

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

/** Warna batang Ritme 7 Hari. Aturan pemilihannya belum ada di desain, jadi data contoh menyebutnya langsung. */
enum class JenisBatang { Sesuai, Mendekati, Pasif, Redup }

/** Satu hari di Ritme 7 Hari. [tinggi] = tinggi batang (dp) apa adanya dari FigCraft. */
data class HariRitme(val hari: Int, val menit: Int, val tinggi: Float, val jenis: JenisBatang, val hariIni: Boolean = false)

/** Satu kategori di bar proporsi Distribusi Penggunaan. */
data class KategoriPersen(val nama: Int, val persen: Int)

/** Satu aplikasi di Distribusi Penggunaan. [selisihMenit] null = "Stabil". */
data class AplikasiMingguan(
    val logo: Int,
    val nama: String,
    val keterangan: Int,
    val argumen: List<Int>,
    val menit: Int,
    val selisihMenit: Int?,
)

/** Isi "Statistik - Minggu Ini". */
data class StatistikMingguan(
    val rataRataMenit: Int,
    val perubahanPersen: Int,
    val skor: Int,
    val labelSkor: Int,
    val batasMenit: Int,
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
    // shortcut: tinggi batang dan jenisnya disalin dari FigCraft (tidak sebanding menit); rumusnya diputuskan di Tahap 8.
    ritme = listOf(
        HariRitme(R.string.hari_sen, 130, 61.59f, JenisBatang.Sesuai),
        HariRitme(R.string.hari_sel, 105, 49.27f, JenisBatang.Pasif),
        HariRitme(R.string.hari_rab, 175, 56f, JenisBatang.Mendekati),
        HariRitme(R.string.hari_kam, 135, 64.95f, JenisBatang.Sesuai, hariIni = true),
        HariRitme(R.string.hari_jum, 90, 42.55f, JenisBatang.Redup),
        HariRitme(R.string.hari_sab, 190, 56f, JenisBatang.Mendekati),
        HariRitme(R.string.hari_min, 125, 58.23f, JenisBatang.Pasif),
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
