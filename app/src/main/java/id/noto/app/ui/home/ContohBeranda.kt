package id.noto.app.ui.home

import id.noto.app.R

/**
 * Satu aturan seperti yang tampil di kartu Beranda.
 * [jam] dan [kuota] null = aturan tanpa jadwal ("Blokir Manual").
 */
data class AturanBeranda(
    val nama: String,
    val aktif: Boolean,
    val jam: String?,
    val kuota: String?,
    val logo: List<Int>, // id drawable logo aplikasi
)

// Data contoh sama dengan FigCraft "Beranda - Terisi". Diganti data dari Room di Tahap 4,
// dihapus sebelum rilis bersama logo pihak ketiga.
private val logoContoh = listOf(
    R.drawable.logo_facebook,
    R.drawable.logo_instagram,
    R.drawable.logo_twitter,
    R.drawable.logo_tiktok,
    R.drawable.logo_whatsapp,
)

val contohAturanBeranda = listOf(
    AturanBeranda("Sosmed", aktif = true, jam = "07.00-21.00", kuota = "1j 30m", logo = logoContoh),
    AturanBeranda("Sosmed", aktif = false, jam = null, kuota = null, logo = logoContoh),
)
