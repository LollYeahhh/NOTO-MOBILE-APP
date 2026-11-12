package id.noto.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** Query tabel kuota. Ditulis pemantau pemakaian, dibaca Blokir dan Detail Aturan. */
@Dao
interface KuotaDao {
    /** Catatan jendela yang dimulai sejak [batas] (epoch milidetik). Jendela terpanjang 24 jam. */
    @Query("SELECT * FROM kuota WHERE mulaiJendela >= :batas")
    fun sejak(batas: Long): Flow<List<Kuota>>

    /** Tambah [detik] dan [dibuka] ke catatan itu; dibuat baru kalau belum ada. */
    @Transaction
    suspend fun tambah(aturanId: Long, mulaiJendela: Long, paket: String, detik: Long, dibuka: Int) {
        if (tambahKeAda(aturanId, mulaiJendela, paket, detik, dibuka) == 0) {
            sisipkan(Kuota(aturanId, mulaiJendela, paket, detik, dibuka))
        }
    }

    @Query(
        "UPDATE kuota SET detik = detik + :detik, dibuka = dibuka + :dibuka " +
            "WHERE aturanId = :aturanId AND mulaiJendela = :mulaiJendela AND paket = :paket",
    )
    suspend fun tambahKeAda(aturanId: Long, mulaiJendela: Long, paket: String, detik: Long, dibuka: Int): Int

    /** Total detik terpakai aturan [aturanId] di jendela yang dimulai [mulaiJendela]. Dipakai pemantau untuk mengunci. */
    @Query("SELECT COALESCE(SUM(detik), 0) FROM kuota WHERE aturanId = :aturanId AND mulaiJendela = :mulaiJendela")
    suspend fun terpakai(aturanId: Long, mulaiJendela: Long): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun sisipkan(kuota: Kuota)
}
