package id.noto.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** Query tabel pemakaian. Penulisan dipakai pemantau pemakaian (Tahap 6), bacaan dipakai statistik (Tahap 8). */
@Dao
interface PemakaianDao {
    /** Semua catatan pada [dari]..[sampai] (epoch day, inklusif). */
    @Query("SELECT * FROM pemakaian WHERE tanggal BETWEEN :dari AND :sampai")
    fun rentang(dari: Long, sampai: Long): Flow<List<Pemakaian>>

    /** Tambah [detik] dan [dibuka] ke catatan jam itu; dibuat baru kalau belum ada. */
    @Transaction
    suspend fun tambah(tanggal: Long, jam: Int, paket: String, detik: Long, dibuka: Int) {
        if (tambahKeAda(tanggal, jam, paket, detik, dibuka) == 0) {
            sisipkan(Pemakaian(tanggal, jam, paket, detik, dibuka))
        }
    }

    @Query(
        "UPDATE pemakaian SET detik = detik + :detik, dibuka = dibuka + :dibuka " +
            "WHERE tanggal = :tanggal AND jam = :jam AND paket = :paket",
    )
    suspend fun tambahKeAda(tanggal: Long, jam: Int, paket: String, detik: Long, dibuka: Int): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun sisipkan(pemakaian: Pemakaian)
}
