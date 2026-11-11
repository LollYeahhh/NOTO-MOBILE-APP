package id.noto.app.util

import java.util.Locale

/** Durasi dalam menit ke format NOTO: "3j 10m", "45m", "1j" (tanpa "h", lihat CLAUDE.md §9). */
fun formatDurasi(menit: Int): String {
    val jam = menit / 60
    val sisa = menit % 60
    return when {
        jam == 0 -> "${sisa}m"
        sisa == 0 -> "${jam}j"
        else -> "${jam}j ${sisa}m"
    }
}

/** Durasi di layar Statistik: menit selalu dua digit bila ada jam ("2j 00m", "1j 05m"), selain itu "45m". */
fun formatDurasiRinci(menit: Int): String =
    if (menit < 60) "${menit}m" else String.format(Locale.ROOT, "%dj %02dm", menit / 60, menit % 60)

/** Menit sejak tengah malam ke jam NOTO: 420 → "07.00", 1440 → "24.00". */
fun formatJam(menit: Int): String = String.format(Locale.ROOT, "%02d.%02d", menit / 60, menit % 60)

/**
 * Daftar hari (0 = Senin … 6 = Minggu) ke teks: semua hari → [setiapHari], hari berurutan (≥ 2) →
 * [polaRentang] dengan hari pertama dan terakhir ("Sen–Jum"), selain itu dipisah koma ("Sen, Rab").
 * [nama] berisi 7 nama hari, singkat atau panjang.
 */
fun ringkasHari(hari: Set<Int>, nama: List<String>, setiapHari: String, polaRentang: String): String {
    val urut = hari.sorted()
    return when {
        urut.size == 7 -> setiapHari
        urut.size >= 2 && urut.last() - urut.first() == urut.size - 1 -> polaRentang.format(nama[urut.first()], nama[urut.last()])
        else -> urut.joinToString(", ") { nama[it] }
    }
}
