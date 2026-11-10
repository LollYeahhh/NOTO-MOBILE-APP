package id.noto.app

import id.noto.app.util.formatDurasi
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatWaktuTest {
    @Test fun menitSaja() = assertEquals("45m", formatDurasi(45))
    @Test fun jamSaja() = assertEquals("1j", formatDurasi(60))
    @Test fun jamDanMenit() = assertEquals("3j 10m", formatDurasi(190))
    @Test fun nol() = assertEquals("0m", formatDurasi(0))
}
