package id.noto.app.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Satu aturan pembatasan (tabel "aturan"). Kuota dipakai bersama oleh semua aplikasinya.
 * [hariAktif] berupa bitmask: bit 0 = Senin … bit 6 = Minggu (lihat [hariKeBitmask]).
 * Jam terbatas dalam menit sejak tengah malam; [selesaiMenit] boleh lebih kecil dari [mulaiMenit]
 * untuk jadwal yang melewati tengah malam (misalnya 22.00–05.00).
 */
@Entity(tableName = "aturan")
data class Aturan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val hariAktif: Int,
    val mulaiMenit: Int,
    val selesaiMenit: Int,
    val kuotaMenit: Int,
    val aktif: Boolean = true,
    val senyap: Boolean = false,
    val dibuatPada: Long = System.currentTimeMillis(),
)

/**
 * Aplikasi yang termasuk dalam satu aturan (tabel "aturan_aplikasi").
 * shortcut: [paket] sementara berisi nama aplikasi contoh ("Instagram"); diganti nama paket Android
 * (misalnya "com.instagram.android") di Tahap 5 saat pemilihan aplikasi memakai PackageManager.
 */
@Entity(
    tableName = "aturan_aplikasi",
    primaryKeys = ["aturanId", "paket"],
    foreignKeys = [ForeignKey(entity = Aturan::class, parentColumns = ["id"], childColumns = ["aturanId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("paket")],
)
data class AturanAplikasi(val aturanId: Long, val paket: String)

/** Aturan beserta daftar aplikasinya, hasil baca dari database. */
data class AturanLengkap(
    @Embedded val aturan: Aturan,
    @Relation(parentColumn = "id", entityColumn = "aturanId", entity = AturanAplikasi::class, projection = ["paket"])
    val paket: List<String>,
)

/** Hari (0 = Senin … 6 = Minggu) → bitmask untuk kolom [Aturan.hariAktif]. */
fun hariKeBitmask(hari: Set<Int>): Int = hari.fold(0) { mask, h -> mask or (1 shl h) }

/** Bitmask [Aturan.hariAktif] → kumpulan hari (0 = Senin … 6 = Minggu). */
fun bitmaskKeHari(mask: Int): Set<Int> = (0..6).filter { mask and (1 shl it) != 0 }.toSet()
