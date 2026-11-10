package id.noto.app.util

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
