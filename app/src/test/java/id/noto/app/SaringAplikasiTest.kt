package id.noto.app

import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.blocks.KategoriAplikasi
import id.noto.app.ui.blocks.saringAplikasi
import org.junit.Assert.assertEquals
import org.junit.Test

class SaringAplikasiTest {
    private val data = listOf(
        KategoriAplikasi(1, listOf(AplikasiPilihan(0, "Instagram", 70), AplikasiPilihan(0, "X", 25), AplikasiPilihan(0, "Facebook", 15))),
        KategoriAplikasi(2, listOf(AplikasiPilihan(0, "TikTok", 55))),
    )

    private fun nama(hasil: List<KategoriAplikasi>) = hasil.map { k -> k.aplikasi.map { it.nama } }

    @Test
    fun cariTanpaBedaHurufDanBuangKategoriKosong() {
        assertEquals(listOf(listOf("Instagram")), nama(saringAplikasi(data, " insta ", urutAZ = false)))
        assertEquals(emptyList<List<String>>(), nama(saringAplikasi(data, "zzz", urutAZ = false)))
    }

    @Test
    fun urutan() {
        assertEquals(listOf("Facebook", "Instagram", "X"), nama(saringAplikasi(data, "", urutAZ = true))[0])
        assertEquals(listOf("Instagram", "X", "Facebook"), nama(saringAplikasi(data, "", urutAZ = false))[0])
    }
}
