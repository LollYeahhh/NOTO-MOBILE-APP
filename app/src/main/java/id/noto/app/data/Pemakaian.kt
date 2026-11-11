package id.noto.app.data

import androidx.room.Entity

/**
 * Pemakaian satu aplikasi pada satu jam di satu hari (tabel "pemakaian").
 * [tanggal] = hari sejak 1 Januari 1970 (LocalDate.toEpochDay), [jam] = 0–23.
 * Disimpan per jam supaya grafik per jam (Screen Time, Detail Aplikasi) bisa dibentuk;
 * total harian = jumlah semua jamnya. Diisi oleh pemantau pemakaian di Tahap 6.
 */
@Entity(tableName = "pemakaian", primaryKeys = ["tanggal", "jam", "paket"])
data class Pemakaian(
    val tanggal: Long,
    val jam: Int,
    val paket: String,
    val detik: Long,
    val dibuka: Int,
)
