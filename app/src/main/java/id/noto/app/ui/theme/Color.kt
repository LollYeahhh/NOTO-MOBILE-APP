package id.noto.app.ui.theme

import androidx.compose.ui.graphics.Color

// Warna NOTO, diambil dari koleksi variabel "Warna" di FigCraft.
// Pakai berdasarkan PERAN (lihat CLAUDE.md §8), bukan berdasarkan namanya.
object NotoWarna {
    val Putih = Color(0xFFFFFFFF)        // Latar layar, teks di atas Primer
    val Permukaan = Color(0xFFF2F4EE)    // "Color": kartu, header, nav, tab
    val Primer = Color(0xFF5B7B6A)       // "Hijau sage": chip aktif, tombol utama, batang grafik
    val PrimerMuda = Color(0xFF6E8F76)   // "sage lebih muda": pill nav aktif, progress kuota
    val TeksUtama = Color(0xFF1F2B25)    // "Sage tua banget": teks utama, ikon, scrim 40%
    val TeksSekunder = Color(0xFF424843) // "sagenya ga nyage": keterangan
    val Garis = Color(0xFFDFE5D7)        // "sage bocil": pemisah, track, garis input
    val Pasif = Color(0xFFABBAAE)        // "sage pucet": data pasif, toggle mati, placeholder
    val InfoLatar = Color(0xFFC8DFDB)    // "Biru pucet": latar badge info, panel info
    val Info = Color(0xFF3368A0)         // "Biru semu": teks dan ikon info
    val Peringatan = Color(0xFFE6C15A)   // "Mustard": HANYA untuk status "hampir habis"

    // Efek blur dekoratif (layar Terkunci, kartu hero Minggu Ini). Hex langsung diizinkan.
    val BlurDekoratif = Color(0xFFC4ECCF)
}

// Warna pola lama yang benar-benar dipakai di layar Beranda, Blokir - Kosong, dan Statistik.
// Direplikasi apa adanya (aturan mutlak §0), jangan diganti dengan warna NOTO di atas.
object NotoWarnaLama {
    val LatarLayar = Color(0xFFF2F4F0)   // isi frame layar lama (sedikit beda dari Permukaan)
    val IkonNav = Color(0xFF212121)      // garis ikon setelan di nav lama
    val IkonNavGaris = Color(0xFF292929) // garis ikon statistik di nav lama
    val Hitam = Color(0xFF000000)        // ikon blokir & titik tiga di kartu Beranda - Terisi
    val IkonBulanIni = Color(0xFF5A6056) // ikon jam di kartu Ringkasan Statistik - Bulan Ini
}
