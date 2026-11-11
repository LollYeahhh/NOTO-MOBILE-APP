package id.noto.app

import id.noto.app.service.PelacakLatar
import id.noto.app.service.Peristiwa.Keluar
import id.noto.app.service.Peristiwa.LayarMati
import id.noto.app.service.Peristiwa.Masuk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PelacakLatarTest {
    @Test
    fun pindahAplikasi_menghitungDibukaDanDiLayar() {
        val p = PelacakLatar()
        assertEquals(listOf("ig"), p.proses(listOf(Masuk("ig"))))
        // "Keluar" ig datang setelah "Masuk" yt: yt tetap di layar.
        assertEquals(listOf("yt"), p.proses(listOf(Masuk("yt"), Keluar("ig"))))
        assertEquals("yt", p.diLayar)
    }

    @Test
    fun kembaliKeAplikasiSama_tidakDihitungLagi() {
        val p = PelacakLatar()
        p.proses(listOf(Masuk("ig")))
        // Misalnya rotasi layar: keluar lalu masuk lagi ke ig.
        assertEquals(emptyList<String>(), p.proses(listOf(Keluar("ig"), Masuk("ig"))))
    }

    @Test
    fun layarMati_tidakAdaDiLayarDanBukaLagiDihitung() {
        val p = PelacakLatar()
        p.proses(listOf(Masuk("ig"), LayarMati))
        assertNull(p.diLayar)
        assertEquals(listOf("ig"), p.proses(listOf(Masuk("ig"))))
    }

    @Test
    fun awalPemantau_tidakMenghitungDibuka() {
        val p = PelacakLatar()
        assertEquals(emptyList<String>(), p.proses(listOf(Masuk("ig"), Masuk("yt")), hitungBuka = false))
        assertEquals("yt", p.diLayar)
        assertEquals(emptyList<String>(), p.proses(listOf(Masuk("yt"))))
    }
}
