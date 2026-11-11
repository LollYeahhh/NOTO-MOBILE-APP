package id.noto.app.data

import kotlinx.coroutines.flow.Flow

/** Satu pintu ke data aturan untuk ViewModel; layar tidak menyentuh DAO langsung. */
class AturanRepository(private val dao: AturanDao) {
    val semua: Flow<List<AturanLengkap>> = dao.semua()

    fun satu(id: Long): Flow<AturanLengkap?> = dao.satu(id)

    fun yangMemuat(paket: String): Flow<List<Aturan>> = dao.yangMemuat(paket)

    suspend fun simpan(aturan: Aturan, paket: List<String>): Long = dao.simpan(aturan, paket)

    suspend fun hapus(id: Long) = dao.hapus(id)

    suspend fun ubahAktif(id: Long, aktif: Boolean) = dao.ubahAktif(id, aktif)

    suspend fun ubahSenyap(id: Long, senyap: Boolean) = dao.ubahSenyap(id, senyap)
}
