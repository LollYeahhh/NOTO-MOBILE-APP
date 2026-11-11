package id.noto.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Query tabel aturan. Bacaan berupa Flow: layar ikut diperbarui otomatis saat isi tabel berubah. */
@Dao
interface AturanDao {
    @Transaction
    @Query("SELECT * FROM aturan ORDER BY dibuatPada")
    fun semua(): Flow<List<AturanLengkap>>

    @Transaction
    @Query("SELECT * FROM aturan WHERE id = :id")
    fun satu(id: Long): Flow<AturanLengkap?>

    /** Aturan yang memuat aplikasi [paket] (seksi "Aturan yang memuat aplikasi ini" di Detail Aplikasi). */
    @Query("SELECT * FROM aturan WHERE id IN (SELECT aturanId FROM aturan_aplikasi WHERE paket = :paket) ORDER BY dibuatPada")
    fun yangMemuat(paket: String): Flow<List<Aturan>>

    @Query("UPDATE aturan SET aktif = :aktif WHERE id = :id")
    suspend fun ubahAktif(id: Long, aktif: Boolean)

    @Query("UPDATE aturan SET senyap = :senyap WHERE id = :id")
    suspend fun ubahSenyap(id: Long, senyap: Boolean)

    /** Simpan aturan baru ([Aturan.id] = 0) atau perubahan, sekaligus mengganti daftar aplikasinya. */
    @Transaction
    suspend fun simpan(aturan: Aturan, paket: List<String>): Long {
        val id = if (aturan.id == 0L) tambahAturan(aturan) else aturan.id.also { ubahAturan(aturan) }
        hapusAplikasi(id)
        tambahAplikasi(paket.map { AturanAplikasi(id, it) })
        return id
    }

    /** Hapus aturan beserta daftar aplikasinya. */
    @Transaction
    suspend fun hapus(id: Long) {
        hapusAplikasi(id)
        hapusAturan(id)
    }

    @Insert
    suspend fun tambahAturan(aturan: Aturan): Long

    @Update
    suspend fun ubahAturan(aturan: Aturan)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun tambahAplikasi(aplikasi: List<AturanAplikasi>)

    @Query("DELETE FROM aturan_aplikasi WHERE aturanId = :aturanId")
    suspend fun hapusAplikasi(aturanId: Long)

    @Query("DELETE FROM aturan WHERE id = :id")
    suspend fun hapusAturan(id: Long)
}
