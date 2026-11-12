package id.noto.app

import id.noto.app.data.Pemakaian
import id.noto.app.service.Peristiwa
import id.noto.app.service.ringkasRiwayat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class RiwayatPemakaianTest {
    private val zona = ZoneOffset.ofHours(7)
    private val hari = LocalDate.of(2026, 11, 12).toEpochDay()
    private fun t(jam: Int, menit: Int, detik: Int = 0) =
        LocalDateTime.of(2026, 11, 12, jam, menit, detik).toInstant(zona).toEpochMilli()

    @Test
    fun pemakaianDipecahPerJamDanDibukaDihitungSekali() {
        val hasil = ringkasRiwayat(
            listOf(
                t(9, 50) to Peristiwa.Masuk("yt"),
                t(10, 5) to Peristiwa.Masuk("yt"), // pindah layar di dalam aplikasi yang sama: bukan dibuka lagi
                t(10, 20) to Peristiwa.Keluar("yt"),
            ),
            sampai = t(12, 0), abaikan = emptySet(), zona = zona,
        )
        assertEquals(
            listOf(Pemakaian(hari, 9, "yt", 600, 1), Pemakaian(hari, 10, "yt", 1200, 0)),
            hasil,
        )
    }

    @Test
    fun launcherDiabaikanLayarMatiMenutupDanAplikasiTerakhirDihitungSampaiBatas() {
        val hasil = ringkasRiwayat(
            listOf(
                t(8, 0) to Peristiwa.Masuk("launcher"),
                t(8, 1) to Peristiwa.Masuk("ig"),
                t(8, 2) to Peristiwa.Keluar("launcher"), // datang setelah Masuk ig: tidak menutup ig
                t(8, 11) to Peristiwa.LayarMati,
                t(8, 20) to Peristiwa.Masuk("ig"), // setelah layar mati: dibuka lagi
            ),
            sampai = t(8, 30), abaikan = setOf("launcher"), zona = zona,
        )
        assertEquals(listOf(Pemakaian(hari, 8, "ig", 1200, 2)), hasil)
    }

    @Test
    fun kejadianSetelahBatasTidakDihitung() {
        val hasil = ringkasRiwayat(
            listOf(t(7, 0) to Peristiwa.Masuk("yt"), t(7, 30) to Peristiwa.Masuk("ig")),
            sampai = t(7, 10), abaikan = emptySet(), zona = zona,
        )
        assertEquals(listOf(Pemakaian(hari, 7, "yt", 600, 1)), hasil)
    }
}
