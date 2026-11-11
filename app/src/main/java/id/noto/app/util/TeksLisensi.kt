package id.noto.app.util

/**
 * Teks lisensi asli dipotong per ±72 huruf. Di layar HP, potongan itu membuat baris patah di tengah,
 * jadi baris dalam satu paragraf disambung lagi. Paragraf (dipisah baris kosong) tetap terpisah,
 * judul pendek berhuruf kapital (misalnya "PREAMBLE") tetap di barisnya sendiri,
 * dan garis "-----" dibuang.
 */
fun rapikanTeksLisensi(teks: String): String =
    teks.replace("\r\n", "\n")
        .split(Regex("\n\\s*\n"))
        .map { paragraf ->
            paragraf.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.all { c -> c == '-' } }
                .fold("") { hasil, baris ->
                    when {
                        hasil.isEmpty() -> baris
                        adalahJudul(hasil.substringAfterLast('\n')) -> "$hasil\n$baris"
                        else -> "$hasil $baris"
                    }
                }
        }
        .filter { it.isNotEmpty() }
        .joinToString("\n\n")

/** Judul seksi: pendek dan tanpa huruf kecil, misalnya "DEFINITIONS" atau "PERMISSION & CONDITIONS". */
private fun adalahJudul(baris: String) = baris.length < 30 && baris.none { it.isLowerCase() }
