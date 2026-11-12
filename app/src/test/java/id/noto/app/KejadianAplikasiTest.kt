package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.JenisKejadian
import id.noto.app.data.Kejadian
import id.noto.app.data.hariKeBitmask
import id.noto.app.ui.stats.kejadianAplikasi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class KejadianAplikasiTest {
    private val zona = ZoneOffset.ofHours(7)
    private val hari = LocalDate.of(2026, 11, 12)
    private fun ms(jam: Int, menit: Int, tgl: LocalDate = hari) = LocalDateTime.of(tgl, java.time.LocalTime.of(jam, menit)).toInstant(zona).toEpochMilli()
    private fun aturan(id: Long, nama: String, vararg paket: String) = AturanLengkap(
        Aturan(id = id, nama = nama, hariAktif = hariKeBitmask((0..6).toSet()), mulaiMenit = 0, selesaiMenit = 0, kuotaMenit = 60, aktif = true),
        paket.toList(),
    )
    private fun k(waktu: Long, jenis: String, aturanId: Long, paket: String) = Kejadian(waktu = waktu, jenis = jenis, aturanId = aturanId, paket = paket, mulaiJendela = 0)

    private val aturan = listOf(aturan(1, "Sosmed", "ig", "yt"), aturan(2, "Video", "yt"), aturan(3, "Chat", "wa"))

    @Test
    fun hitungPerAplikasi_timeOutDariAturanYangMemuatnya() {
        val data = listOf(
            k(ms(9, 0), JenisKejadian.NAPAS, 1, "yt"),
            k(ms(9, 1), JenisKejadian.BATAL, 1, "yt"),
            k(ms(10, 0), JenisKejadian.NAPAS, 1, "yt"),
            k(ms(10, 0), JenisKejadian.NAPAS, 1, "ig"), // aplikasi lain
            k(ms(8, 15), JenisKejadian.TIME_OUT, 1, "ig"), // aturan Sosmed habis saat Instagram dibuka: yt ikut terkunci
            k(ms(20, 40), JenisKejadian.TIME_OUT, 2, "yt"),
            k(ms(21, 0), JenisKejadian.TIME_OUT, 3, "wa"), // aturan yang tidak memuat yt
            k(ms(23, 0, hari.minusDays(1)), JenisKejadian.NAPAS, 1, "yt"), // kemarin
        )
        val h = kejadianAplikasi(data, "yt", aturan, hari, zona)
        assertEquals(2, h.latihanNapas)
        assertEquals(1, h.batalMembuka)
        assertEquals(2, h.timeOut)
        assertEquals("Video", h.aturanTimeOut)
        assertEquals("20.40", h.jamTimeOut)
    }

    @Test
    fun belumPernahHabis_tanpaJam() {
        val h = kejadianAplikasi(listOf(k(ms(9, 0), JenisKejadian.NAPAS, 1, "yt")), "yt", aturan, hari, zona)
        assertEquals(0, h.timeOut)
        assertNull(h.jamTimeOut)
        assertNull(h.aturanTimeOut)
    }
}
