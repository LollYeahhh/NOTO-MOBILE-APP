package id.noto.app

import id.noto.app.ui.permission.perluIzinPerangkat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IzinPerangkatTest {
    @Test
    fun hanyaMerekDenganIzinTambahan() {
        assertTrue(perluIzinPerangkat("Xiaomi"))
        assertTrue(perluIzinPerangkat("OnePlus"))
        assertFalse(perluIzinPerangkat("samsung"))
        assertFalse(perluIzinPerangkat("Google"))
    }
}
