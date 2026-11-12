package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.cariKunci
import id.noto.app.data.hariKeBitmask
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class HitungKunciTest {
    // 2026-11-13 = Jumat.
    private fun t(hari: Int, jam: Int, menit: Int = 0) = LocalDateTime.of(2026, 11, hari, jam, menit)
    private fun aturan(id: Long, mulai: Int, selesai: Int, aktif: Boolean = true) = AturanLengkap(
        Aturan(id = id, nama = "A$id", hariAktif = hariKeBitmask((0..6).toSet()), mulaiMenit = mulai, selesaiMenit = selesai, kuotaMenit = 60, aktif = aktif),
        listOf("yt"),
    )

    private fun kunci(daftar: List<AturanLengkap>, waktu: LocalDateTime, terpakai: Map<Long, Long>, paket: String = "yt") =
        runBlocking { cariKunci(daftar, paket, waktu) { id, _ -> terpakai[id] ?: 0 } }

    @Test
    fun kuotaHabis_terkunciSampaiJadwalSelesai() {
        val a = aturan(1, 7 * 60, 21 * 60)
        assertNull(kunci(listOf(a), t(13, 12), mapOf(1L to 3599)))
        assertEquals(t(13, 21), kunci(listOf(a), t(13, 12), mapOf(1L to 3600))?.selesai)
        // Aplikasi lain, aturan nonaktif, dan di luar jadwal: tidak terkunci.
        assertNull(kunci(listOf(a), t(13, 12), mapOf(1L to 3600), paket = "ig"))
        assertNull(kunci(listOf(aturan(1, 7 * 60, 21 * 60, aktif = false)), t(13, 12), mapOf(1L to 3600)))
        assertNull(kunci(listOf(a), t(13, 22), mapOf(1L to 3600)))
    }

    @Test
    fun lewatTengahMalamDanSehariPenuh() {
        // 22.00–05.00 dari Jumat malam selesai Sabtu 05.00.
        assertEquals(t(14, 5), kunci(listOf(aturan(1, 22 * 60, 5 * 60)), t(13, 23), mapOf(1L to 3600))?.selesai)
        // Mulai = selesai: sehari penuh, selesai tengah malam berikutnya.
        assertEquals(t(14, 0), kunci(listOf(aturan(1, 7 * 60, 7 * 60)), t(13, 3), mapOf(1L to 3600))?.selesai)
    }

    @Test
    fun beberapaAturanHabis_pakaiYangSelesaiPalingAkhir() {
        val pagi = aturan(1, 7 * 60, 13 * 60)
        val siang = aturan(2, 9 * 60, 18 * 60)
        assertEquals(2L, kunci(listOf(pagi, siang), t(13, 12), mapOf(1L to 3600, 2L to 3600))?.aturan?.id)
        assertEquals(1L, kunci(listOf(pagi, siang), t(13, 12), mapOf(1L to 3600))?.aturan?.id)
    }
}
