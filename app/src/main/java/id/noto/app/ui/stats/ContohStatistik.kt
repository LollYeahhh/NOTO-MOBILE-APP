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
