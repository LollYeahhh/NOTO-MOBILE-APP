package id.noto.app

import id.noto.app.service.JadwalRingkasan
import id.noto.app.service.ringkasanBerikutnya
import id.noto.app.service.ringkasanMingguan
import id.noto.app.ui.settings.SetelanNoto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RingkasanTest {
    private val kamis = LocalDate.of(2026, 11, 12)

    @Test
    fun sebelumDanSesudahJamPilihan() {
        // 10.00 → hari ini 21.00; 21.00 tepat atau lewat → besok 21.00.
        assertEquals(JadwalRingkasan(kamis.atTime(21, 0), kamis), ringkasanBerikutnya(kamis.atTime(10, 0), 21 * 60, true))
        val jumat = kamis.plusDays(1)
        assertEquals(JadwalRingkasan(jumat.atTime(21, 0), jumat), ringkasanBerikutnya(kamis.atTime(21, 0), 21 * 60, true))
    }

    @Test
    fun jamTenang_digeserKe0600_tetapMeringkasTanggalSemula() {
        // 23.00 di jam tenang → dikirim besok 06.00, isinya ringkasan hari ini.
        assertEquals(JadwalRingkasan(kamis.plusDays(1).atTime(6, 0), kamis), ringkasanBerikutnya(kamis.atTime(10, 0), 23 * 60, true))
        // 05.00 (besok, karena sudah lewat) → 06.00 di hari yang sama.
        assertEquals(JadwalRingkasan(kamis.plusDays(1).atTime(6, 0), kamis.plusDays(1)), ringkasanBerikutnya(kamis.atTime(10, 0), 5 * 60, true))
        // Jam tenang mati: tetap 23.00.
        assertEquals(JadwalRingkasan(kamis.atTime(23, 0), kamis), ringkasanBerikutnya(kamis.atTime(10, 0), 23 * 60, false))
    }

    @Test
    fun mingguanHanyaHariMinggu() {
        val minggu = LocalDate.of(2026, 11, 15)
        assertTrue(ringkasanMingguan(minggu, SetelanNoto(ringkasanMingguan = true)))
        assertFalse(ringkasanMingguan(minggu, SetelanNoto(ringkasanMingguan = false)))
        assertFalse(ringkasanMingguan(kamis, SetelanNoto(ringkasanMingguan = true)))
        // Ringkasan Minggu 23.00 yang tertunda ke Senin 06.00 tetap mingguan (tanggal semula = Minggu).
        assertTrue(ringkasanMingguan(ringkasanBerikutnya(minggu.atTime(10, 0), 23 * 60, true).tanggal, SetelanNoto()))
    }
}
