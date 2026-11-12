package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.hariKeBitmask
import id.noto.app.data.perluJeda
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class JedaTest {
    // 07.00–21.00 setiap hari.
    private val a = Aturan(id = 1, nama = "Sosmed", hariAktif = hariKeBitmask((0..6).toSet()), mulaiMenit = 7 * 60, selesaiMenit = 21 * 60, kuotaMenit = 60, aktif = true)
    private val siang = LocalDateTime.of(2026, 11, 12, 9, 0)

    @Test
    fun hanyaSaatAturanBerlaku() {
        assertTrue(perluJeda(a, siang, 10))
        assertFalse(perluJeda(a, siang.withHour(22), 10)) // di luar jadwal
        assertFalse(perluJeda(a.copy(aktif = false), siang, 10)) // sudah nonaktif
        assertFalse(perluJeda(a, siang, 0)) // "Tanpa jeda"
    }
}
