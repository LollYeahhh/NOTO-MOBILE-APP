package id.noto.app

import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam
import id.noto.app.util.ringkasHari
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatWaktuTest {
    @Test fun menitSaja() = assertEquals("45m", formatDurasi(45))
    @Test fun jamSaja() = assertEquals("1j", formatDurasi(60))
    @Test fun jamDanMenit() = assertEquals("3j 10m", formatDurasi(190))
    @Test fun nol() = assertEquals("0m", formatDurasi(0))

    @Test fun jamPagi() = assertEquals("07.00", formatJam(420))
    @Test fun jamMalam() = assertEquals("21.30", formatJam(1290))

    private val singkat = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    private fun ringkas(vararg h: Int) = ringkasHari(h.toSet(), singkat, "Setiap hari", "%1\$s–%2\$s")

    @Test fun hariKerja() = assertEquals("Sen–Jum", ringkas(0, 1, 2, 3, 4))
    @Test fun akhirPekan() = assertEquals("Sab–Min", ringkas(5, 6))
    @Test fun semuaHari() = assertEquals("Setiap hari", ringkas(0, 1, 2, 3, 4, 5, 6))
    @Test fun tidakBerurutan() = assertEquals("Sen, Rab, Jum", ringkas(4, 0, 2))
    @Test fun satuHari() = assertEquals("Rab", ringkas(2))
}
