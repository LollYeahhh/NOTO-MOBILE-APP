package id.noto.app

import id.noto.app.ui.stats.batasSumbu
import id.noto.app.ui.stats.contohMenitPerJam
import id.noto.app.ui.stats.dalamJadwal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GrafikJamTest {
    @Test
    fun batasSumbuKelipatanSepuluh() {
        assertEquals(20, batasSumbu(contohMenitPerJam(70))) // batang tertinggi 12m, seperti desain
        assertEquals(10, batasSumbu(listOf(0f, 0f)))
        assertEquals(40, batasSumbu(listOf(30.5f)))
    }

    @Test
    fun polaContohMengikutiTotal() {
        assertEquals(120f, contohMenitPerJam(120).sum(), 0.01f)
        assertEquals(24, contohMenitPerJam(70).size)
    }

    @Test
    fun jamDalamJadwal() {
        assertFalse(dalamJadwal(6, 7 * 60, 21 * 60))
        assertTrue(dalamJadwal(7, 7 * 60, 21 * 60))
        assertTrue(dalamJadwal(20, 7 * 60, 21 * 60))
        assertFalse(dalamJadwal(21, 7 * 60, 21 * 60))
    }
}
