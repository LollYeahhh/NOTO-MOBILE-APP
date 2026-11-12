package id.noto.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Satu kejadian penguncian (tabel "kejadian"), untuk Statistik:
 * - [JenisKejadian.TIME_OUT]: kuota aturan [aturanId] habis; dicatat sekali per jendela [mulaiJendela].
 * - [JenisKejadian.NAPAS]: layar Terkunci muncul dengan panel latihan napas.
 * - [JenisKejadian.BATAL]: pengguna menutup layar Terkunci lewat "Kembali ke layar utama" atau tombol back.
 * - notif_*: penanda pengingat yang sudah ditangani (Tahap 8D), supaya terkirim sekali per jendela; tidak dihitung Statistik.
 * [waktu] dan [mulaiJendela] dalam epoch milidetik; [paket] = aplikasi yang sedang dikunci.
 * Tanpa foreign key: riwayat tetap ada walau aturannya dihapus.
 */
@Entity(tableName = "kejadian", indices = [Index("waktu")])
data class Kejadian(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val waktu: Long,
    val jenis: String,
    val aturanId: Long,
    val paket: String,
    val mulaiJendela: Long,
)

object JenisKejadian {
    const val TIME_OUT = "time_out"
    const val NAPAS = "napas"
    const val BATAL = "batal"
    const val NOTIF_MULAI = "notif_mulai"
    const val NOTIF_SELESAI = "notif_selesai"
    const val NOTIF_HABIS = "notif_habis"
    fun notifSisa(menit: Int) = "notif_sisa_$menit"
}
