package id.noto.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Query tabel kejadian. Ditulis layar Terkunci dan pemicu kunci, dibaca Statistik. */
@Dao
interface KejadianDao {
    @Insert
    suspend fun simpan(kejadian: Kejadian)

    /** Catat time out aturan [aturanId] di jendela [mulaiJendela], kecuali sudah tercatat (satu per jendela). */
    @Query(
        "INSERT INTO kejadian (waktu, jenis, aturanId, paket, mulaiJendela) " +
            "SELECT :waktu, '${JenisKejadian.TIME_OUT}', :aturanId, :paket, :mulaiJendela " +
            "WHERE NOT EXISTS (SELECT 1 FROM kejadian WHERE jenis = '${JenisKejadian.TIME_OUT}' " +
            "AND aturanId = :aturanId AND mulaiJendela = :mulaiJendela)",
    )
    suspend fun catatTimeOut(waktu: Long, aturanId: Long, paket: String, mulaiJendela: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM kejadian WHERE jenis = :jenis AND aturanId = :aturanId AND mulaiJendela = :mulaiJendela)")
    suspend fun ada(jenis: String, aturanId: Long, mulaiJendela: Long): Boolean

    /** Simpan penanda [jenis] untuk aturan [aturanId] di jendela [mulaiJendela], kecuali sudah ada. */
    @Query(
        "INSERT INTO kejadian (waktu, jenis, aturanId, paket, mulaiJendela) " +
            "SELECT :waktu, :jenis, :aturanId, :paket, :mulaiJendela " +
            "WHERE NOT EXISTS (SELECT 1 FROM kejadian WHERE jenis = :jenis AND aturanId = :aturanId AND mulaiJendela = :mulaiJendela)",
    )
    suspend fun catatSekali(waktu: Long, jenis: String, aturanId: Long, paket: String, mulaiJendela: Long)

    @Query("SELECT * FROM kejadian WHERE waktu >= :batas")
    fun sejak(batas: Long): Flow<List<Kejadian>>

    @Query("DELETE FROM kejadian WHERE waktu < :batas")
    suspend fun hapusSebelum(batas: Long)
}
