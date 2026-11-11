package id.noto.app

import id.noto.app.data.bitmaskKeHari
import id.noto.app.data.hariKeBitmask
import org.junit.Assert.assertEquals
import org.junit.Test

class HariBitmaskTest {
    @Test
    fun senJum() {
        assertEquals(0b0011111, hariKeBitmask(setOf(0, 1, 2, 3, 4)))
        assertEquals(setOf(0, 1, 2, 3, 4), bitmaskKeHari(0b0011111))
    }

    @Test
    fun bolakBalik() {
        for (mask in 0 until 128) assertEquals(mask, hariKeBitmask(bitmaskKeHari(mask)))
        assertEquals(emptySet<Int>(), bitmaskKeHari(0))
        assertEquals((0..6).toSet(), bitmaskKeHari(127))
    }
}
