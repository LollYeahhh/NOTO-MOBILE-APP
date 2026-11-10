package id.noto.app.ui.blocks

// Isian form Tambah/Edit Aturan. Teks jadwal dan kuota masih teks jadi (data contoh);
// di Tahap 4–5 dihitung dari hari, jam mulai-selesai, dan menit kuota.

/** Jadwal dalam dua bentuk: singkat untuk baris form, panjang untuk kalimat pratinjau. */
data class JadwalForm(val hariSingkat: String, val hariPanjang: String, val jam: String)

data class FormAturan(
    val nama: String = "",
    val aplikasi: List<String> = emptyList(),
    val jadwal: JadwalForm? = null,
    val kuota: String? = null,
    val senyap: Boolean = false,
) {
    /**
     * Aplikasi, jadwal, dan kuota sudah diisi: tombol Simpan aktif dan pratinjau muncul.
     * Nama tidak wajib; kalau kosong dipakai [namaOtomatis].
     */
    val lengkap: Boolean
        get() = aplikasi.isNotEmpty() && jadwal != null && kuota != null
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

private val jadwalSosmed = JadwalForm("Sen–Jum", "Senin sampai Jumat", "07.00–21.00")

/** Sama dengan FigCraft "Blokir - Tambah Aturan Terisi". */
val contohFormTerisi = FormAturan(
    nama = "Sosmed",
    aplikasi = listOf("Instagram", "X", "TikTok", "WhatsApp"),
    jadwal = jadwalSosmed,
    kuota = "1j 30m",
    senyap = false,
)

/** Sama dengan FigCraft "Blokir - Edit Aturan" (aturan Sosmed yang sudah ada). */
val contohFormEdit = contohFormTerisi.copy(
    aplikasi = listOf("Instagram", "X", "TikTok", "WhatsApp", "Facebook"),
    senyap = true,
)
