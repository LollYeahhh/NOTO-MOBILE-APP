package id.noto.app.ui.blocks

import id.noto.app.R
import id.noto.app.ui.components.StatusAturan
import id.noto.app.ui.components.TipeBadge

// Data contoh sama dengan FigCraft "Blokir - Daftar" dan "Blokir - Detail Aturan".
// Diganti data dari Room di Tahap 4, dihapus sebelum rilis bersama logo pihak ketiga.

/** Satu kartu di Daftar. Teks sudah jadi (nanti dihitung dari data aturan). */
data class AturanDaftar(
    val judul: String,
    val jadwal: String,
    val status: StatusAturan,
    val labelStatus: String,
    val sisa: String,
    val total: String,
    val progres: Float,
    val logo: List<Int>, // id drawable logo aplikasi
    val id: Long = 0, // id aturan di Room; 0 = data contoh
)

private val fb = R.drawable.logo_facebook
private val ig = R.drawable.logo_instagram
private val tw = R.drawable.logo_twitter
private val tt = R.drawable.logo_tiktok
private val wa = R.drawable.logo_whatsapp

val contohAturanDaftar = listOf(
    AturanDaftar("Sosmed", "07.00–21.00 · Sen–Jum", StatusAturan.Berlaku, "Berlaku s.d. 21.00",
        "Sisa 45m", "45m dari 1j 30m terpakai", 164f / 329f, listOf(fb, ig, tw, tt, wa)),
    AturanDaftar("Hiburan", "08.00–20.00 · Setiap hari", StatusAturan.HampirHabis, "Hampir habis",
        "Sisa 5m", "1j 55m dari 2j terpakai", 309f / 329f, listOf(tw, tt)),
    AturanDaftar("Chat Kerja", "09.00–21.00 · Sen–Jum", StatusAturan.TimeOut, "Bebas lagi 21.00",
        "Kuota habis, waktunya time out", "1j terpakai", 1f, listOf(wa)),
    AturanDaftar("Malam Tenang", "22.00–05.00 · Setiap hari", StatusAturan.DiLuarJadwal, "Bebas sampai 22.00",
        "Kuota siap 30m", "Mulai lagi 22.00", 0f, listOf(ig, tt)),
    AturanDaftar("Akhir Pekan", "10.00–18.00 · Sab–Min · Kuota 3j", StatusAturan.Nonaktif, "Nonaktif",
        "", "", 0f, listOf(fb, ig, tw, tt)),
)

/** Isi layar Detail Aturan. */
data class DetailAturan(
    val nama: String,
    val labelStatus: String,
    val sisa: String,
    val kuota: String,
    val progres: Float,
    val terpakai: String,
    val dibuka: Int,
    val bebasLagi: String,
    val jamReset: String,
    val jam: String,
    val hariAktif: Set<Int>, // 0 = Senin … 6 = Minggu
    val mulaiMenit: Int,
    val selesaiMenit: Int,
    val sekarangMenit: Int,
    val aplikasi: List<PemakaianAplikasi>,
    val badge: TipeBadge = TipeBadge.Aktif,
    /** Teks kanan bawah kartu Sisa Kuota; null = "Bebas lagi dalam [bebasLagi]". */
    val keteranganKanan: String? = null,
    /** Teks panel info; null = "Waktu hanya dihitung … [jamReset] …". */
    val teksInfo: String? = null,
)

data class PemakaianAplikasi(val logo: Int, val nama: String, val dibuka: Int, val durasi: String)

val contohDetailSosmed = DetailAturan(
    nama = "Sosmed",
    labelStatus = "Berlaku s.d. 21.00",
    sisa = "45m",
    kuota = "1j 30m",
    progres = 164f / 329f,
    terpakai = "45m",
    dibuka = 12,
    bebasLagi = "6j 15m",
    jamReset = "07.00",
    jam = "07.00–21.00",
    hariAktif = setOf(0, 1, 2, 3, 4),
    mulaiMenit = 7 * 60,
    selesaiMenit = 21 * 60,
    sekarangMenit = 14 * 60,
    aplikasi = listOf(
        PemakaianAplikasi(ig, "Instagram", 5, "20m"),
        PemakaianAplikasi(tt, "TikTok", 3, "12m"),
        PemakaianAplikasi(tw, "X", 2, "8m"),
        PemakaianAplikasi(wa, "WhatsApp", 1, "3m"),
        PemakaianAplikasi(fb, "Facebook", 1, "2m"),
    ),
)
