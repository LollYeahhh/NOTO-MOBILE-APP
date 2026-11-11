package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.hariKeBitmask
import id.noto.app.data.jendelaBerikutnya
import id.noto.app.data.jendelaSekarang
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class HitungKuotaTest {
    // 2026-11-09 = Senin, 2026-11-13 = Jumat, 2026-11-14 = Sabtu.
    private fun aturan(hari: Set<Int>, mulai: Int, selesai: Int) =
        Aturan(nama = "Uji", hariAktif = hariKeBitmask(hari), mulaiMenit = mulai, selesaiMenit = selesai, kuotaMenit = 60)

    private val senJum = aturan((0..4).toSet(), 7 * 60, 21 * 60)
    private fun t(hari: Int, jam: Int, menit: Int = 0) = LocalDateTime.of(2026, 11, hari, jam, menit)

    @Test
    fun jadwalBiasa_dalamDanLuarJendela() {
        assertEquals(t(9, 7), jendelaSekarang(senJum, t(9, 12)))
        assertNull(jendelaSekarang(senJum, t(9, 6, 59)))
        // Jam selesai tidak termasuk.
        assertNull(jendelaSekarang(senJum, t(9, 21)))
        // Sabtu bukan hari aktif.
        assertNull(jendelaSekarang(senJum, t(14, 12)))
    }

    @Test
    fun jadwalLewatTengahMalam_bagianPagiIkutHariSebelumnya() {
        // 22.00–05.00, hanya Jumat.
        val malam = aturan(setOf(4), 22 * 60, 5 * 60)
        assertEquals(t(13, 22), jendelaSekarang(malam, t(13, 23)))
        assertEquals(t(13, 22), jendelaSekarang(malam, t(14, 4, 30)))
        // Jumat pagi ikut Kamis yang tidak aktif.
        assertNull(jendelaSekarang(malam, t(13, 4)))
    }

    @Test
    fun sehariPenuh_mulaiTengahMalam() {
        val penuh = aturan(setOf(0), 7 * 60, 7 * 60)
        assertEquals(t(9, 0), jendelaSekarang(penuh, t(9, 3)))
        assertNull(jendelaSekarang(penuh, t(10, 3)))
    }

    @Test
    fun jendelaBerikutnya_lewatAkhirPekan() {
        // Jumat malam → Senin 07.00.
        assertEquals(t(16, 7), jendelaBerikutnya(senJum, t(13, 22)))
        // Senin pagi sebelum mulai → hari yang sama.
        assertEquals(t(9, 7), jendelaBerikutnya(senJum, t(9, 6)))
        // Senin siang (sedang berjalan) → Selasa.
        assertEquals(t(10, 7), jendelaBerikutnya(senJum, t(9, 12)))
    }
}
