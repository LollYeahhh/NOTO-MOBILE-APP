package id.noto.app.ui.blocks

import androidx.compose.ui.graphics.ImageBitmap
import id.noto.app.R

// Isian form Tambah/Edit Aturan. Teks di layar dibentuk dari data ini (lihat util/FormatWaktu.kt).

/** Jadwal: [hari] 0 = Senin … 6 = Minggu, jam terbatas dalam menit sejak tengah malam. */
data class JadwalForm(val hari: Set<Int>, val mulaiMenit: Int, val selesaiMenit: Int)

data class FormAturan(
    val nama: String = "",
    val aplikasi: List<String> = emptyList(),
    val jadwal: JadwalForm? = null,
    val kuotaMenit: Int? = null,
    val senyap: Boolean = false,
) {
    /**
     * Aplikasi, jadwal, dan kuota sudah diisi: tombol Simpan aktif dan pratinjau muncul.
     * Nama tidak wajib; kalau kosong dipakai [namaOtomatis].
     */
    val lengkap: Boolean
        get() = aplikasi.isNotEmpty() && jadwal != null && kuotaMenit != null
}

/**
 * Nama untuk aturan tanpa nama: [dasar] ("Blokir"), lalu [polaNomor] dengan 1, 2, … ("Blokir 1"),
 * yang pertama belum dipakai di [terpakai]. Perbandingan tidak membedakan huruf besar-kecil.
 */
fun namaOtomatis(dasar: String, polaNomor: String, terpakai: Collection<String>): String {
    val dipakai = terpakai.map { it.trim().lowercase() }.toSet()
    if (dasar.lowercase() !in dipakai) return dasar
    var nomor = 1
    while (polaNomor.format(nomor).lowercase() in dipakai) nomor++
    return polaNomor.format(nomor)
}

/** Jadwal awal Sheet Atur Jadwal saat form belum punya jadwal (sama dengan FigCraft). */
val jadwalBawaan = JadwalForm(hari = setOf(0, 1, 2, 3, 4), mulaiMenit = 7 * 60, selesaiMenit = 21 * 60)

/** Kuota awal Sheet Atur Kuota saat form belum punya kuota (sama dengan FigCraft: 1j 30m). */
const val KUOTA_BAWAAN_MENIT = 90

/** Sama dengan FigCraft "Blokir - Tambah Aturan Terisi". */
val contohFormTerisi = FormAturan(
    nama = "Sosmed",
    aplikasi = listOf("Instagram", "X", "TikTok", "WhatsApp"),
    jadwal = jadwalBawaan,
    kuotaMenit = KUOTA_BAWAAN_MENIT,
    senyap = false,
)

/** Sama dengan FigCraft "Blokir - Edit Aturan" (aturan Sosmed yang sudah ada). */
val contohFormEdit = contohFormTerisi.copy(
    aplikasi = listOf("Instagram", "X", "TikTok", "WhatsApp", "Facebook"),
    senyap = true,
)

/**
 * Satu aplikasi di layar Pilih Aplikasi. [paket] = nama paket (disimpan di aturan), [kategori] = id string kelompok.
 * [ikon] dari sistem (null = ikon cadangan); [logoContoh] hanya untuk data contoh.
 */
data class AplikasiPilihan(
    val paket: String,
    val nama: String,
    val kategori: Int,
    val ikon: ImageBitmap? = null,
    val logoContoh: Int? = null,
)

/** Kelompok di Pilih Aplikasi; [judul] = id string nama kategori. */
data class KategoriAplikasi(val judul: Int, val aplikasi: List<AplikasiPilihan>)

/** Data contoh untuk Preview dan Statistik; aplikasi asli dari muatAplikasiTerpasang(). */
val contohAplikasi = listOf(
    AplikasiPilihan("com.instagram.android", "Instagram", R.string.kategori_sosial_media, logoContoh = R.drawable.logo_instagram),
    AplikasiPilihan("com.twitter.android", "X", R.string.kategori_sosial_media, logoContoh = R.drawable.logo_twitter),
    AplikasiPilihan("com.facebook.katana", "Facebook", R.string.kategori_sosial_media, logoContoh = R.drawable.logo_facebook),
    AplikasiPilihan("com.zhiliaoapp.musically", "TikTok", R.string.kategori_hiburan, logoContoh = R.drawable.logo_tiktok),
    AplikasiPilihan("com.whatsapp", "WhatsApp", R.string.kategori_komunikasi, logoContoh = R.drawable.logo_whatsapp),
)

/** Template cepat di Sheet Atur Jadwal: id string nama, jam mulai dan selesai (menit). */
data class TemplateJadwal(val nama: Int, val mulaiMenit: Int, val selesaiMenit: Int)

val templateJadwal = listOf(
    TemplateJadwal(R.string.template_siang, 7 * 60, 21 * 60),
    TemplateJadwal(R.string.template_jam_kerja, 9 * 60, 17 * 60),
    TemplateJadwal(R.string.template_jam_sekolah, 7 * 60, 15 * 60),
    TemplateJadwal(R.string.template_malam, 18 * 60, 23 * 60),
)

/** Pilihan cepat di Sheet Atur Kuota (menit). */
val pilihanKuotaCepat = listOf(30, 60, 90, 120)

/**
 * shortcut: rata-rata pemakaian di jam jadwal masih angka contoh dari FigCraft (2j 40m);
 * dihitung dari UsageStatsManager di Tahap 6.
 */
const val CONTOH_PEMAKAIAN_BIASA_MENIT = 160
