package id.noto.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Pemakaian kuota satu aplikasi dalam satu jendela jadwal aturan (tabel "kuota").
 * [mulaiJendela] = awal jendela jadwal (epoch milidetik). Jendela baru punya nilai baru, jadi kuota
 * otomatis "kembali penuh" tanpa perlu menghapus apa pun. Diisi pemantau pemakaian.
 */
@Entity(
    tableName = "kuota",
    primaryKeys = ["aturanId", "mulaiJendela", "paket"],
    foreignKeys = [ForeignKey(entity = Aturan::class, parentColumns = ["id"], childColumns = ["aturanId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("mulaiJendela")],
)
data class Kuota(
    val aturanId: Long,
    val mulaiJendela: Long,
    val paket: String,
    val detik: Long,
    val dibuka: Int,
)
