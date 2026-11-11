package id.noto.app

import id.noto.app.ui.stats.mendekatiBatas
import id.noto.app.ui.stats.tinggiBatang
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RitmeTest {
    @Test
    fun tinggiSebandingMenit() {
        assertEquals(68f, tinggiBatang(180, 180), 0.001f)
        assertEquals(34f, tinggiBatang(90, 180), 0.001f)
        assertEquals(71.778f, tinggiBatang(190, 180), 0.001f)
    }

    @Test
    fun mendekatiMulai90Persen() {
        assertFalse(mendekatiBatas(161, 180))
        assertTrue(mendekatiBatas(162, 180))
        assertTrue(mendekatiBatas(190, 180))
    }
}
