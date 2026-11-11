package id.noto.app.ui.home

import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.blocks.contohAplikasi

/**
 * Satu aturan seperti yang tampil di kartu Beranda.
 * [jam] dan [kuota] null = aturan tanpa jadwal ("Blokir Manual").
 */
data class AturanBeranda(
    val nama: String,
    val aktif: Boolean,
    val jam: String?,
    val kuota: String?,
    val logo: List<AplikasiPilihan>, // aplikasi di aturan (untuk logonya)
    val id: Long = 0, // id aturan di Room; 0 = data contoh
)

// Data contoh sama dengan FigCraft "Beranda - Terisi". Diganti data dari Room di Tahap 4,
// dihapus sebelum rilis bersama logo pihak ketiga.
private val logoContoh = listOf("Facebook", "Instagram", "X", "TikTok", "WhatsApp")
    .map { nama -> contohAplikasi.first { it.nama == nama } }

val contohAturanBeranda = listOf(
    AturanBeranda("Sosmed", aktif = true, jam = "07.00-21.00", kuota = "1j 30m", logo = logoContoh),
    AturanBeranda("Sosmed", aktif = false, jam = null, kuota = null, logo = logoContoh),
)
