package id.noto.app.ui.settings

import id.noto.app.ui.blocks.JadwalForm

/**
 * Semua nilai di tab Setelan. Nilai contoh di bawah sama dengan FigCraft.
 * shortcut: disimpan di memori saja (hilang saat aplikasi ditutup) dan belum memengaruhi aturan;
 * dipindah ke DataStore di Tahap 4 dan disambungkan ke perilaku aplikasi di Tahap 8.
 */
data class SetelanNoto(
    val pengingatSisa: Boolean = true,
    val sisaMenit: Set<Int> = setOf(15, 5),
    val saatKuotaHabis: Boolean = true,
    val saatJadwal: Boolean = false,
    val ringkasanHarianMenit: Int = 21 * 60,
    val ringkasanMingguan: Boolean = true,
    val jamTenang: Boolean = true,
    val jadwalBawaan: JadwalForm = id.noto.app.ui.blocks.jadwalBawaan,
    val kuotaBawaanMenit: Int = 60,
    val jedaDetik: Int = 10,
    val kunciEdit: Boolean = false,
    val latihanNapas: Boolean = true,
    val sisaWaktu: Boolean = true,
)

val contohSetelan = SetelanNoto()

/** Pilihan chip "Sisa 15m / 10m / 5m" di Pengingat sisa kuota. */
val pilihanSisaMenit = listOf(15, 10, 5)

/** Jam tenang contoh (FigCraft: 22.00–06.00). */
const val JAM_TENANG_MULAI = 22 * 60
const val JAM_TENANG_SELESAI = 6 * 60

/** Sheet yang bisa dibuka dari tab Setelan. */
enum class SheetSetelan { Jadwal, JamMulai, JamSelesai, Kuota, Jeda, JamRingkasan }
