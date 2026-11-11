package id.noto.app

import android.content.pm.ApplicationInfo
import id.noto.app.data.kategoriDari
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.blocks.aplikasiDariPaket
import id.noto.app.ui.blocks.saringAplikasi
import org.junit.Assert.assertEquals
import org.junit.Test

class SaringAplikasiTest {
    private val sosmed = R.string.kategori_sosial_media
    private val lainnya = R.string.kategori_lainnya
    private val data = listOf(
        AplikasiPilihan("a", "X", sosmed),
        AplikasiPilihan("b", "WhatsApp", lainnya),
        AplikasiPilihan("c", "instagram", sosmed),
        AplikasiPilihan("d", "Facebook", sosmed),
    )

    @Test
    fun cariTanpaBedaHurufDanBuangKelompokKosong() {
        val hasil = saringAplikasi(data, " INSTA ", urutAZ = false)
        assertEquals(listOf(sosmed), hasil.map { it.judul })
        assertEquals(listOf("instagram"), hasil[0].aplikasi.map { it.nama })
        assertEquals(emptyList<Any>(), saringAplikasi(data, "zzz", urutAZ = true))
    }

    @Test
    fun kategoriUrutTetapIsiAZ() {
        val hasil = saringAplikasi(data, "", urutAZ = false)
        assertEquals(listOf(sosmed, lainnya), hasil.map { it.judul })
        assertEquals(listOf("Facebook", "instagram", "X"), hasil[0].aplikasi.map { it.nama })
    }

    @Test
    fun azSatuKelompok() {
        val hasil = saringAplikasi(data, "", urutAZ = true)
        assertEquals(listOf(R.string.semua_aplikasi), hasil.map { it.judul })
        assertEquals(listOf("Facebook", "instagram", "WhatsApp", "X"), hasil[0].aplikasi.map { it.nama })
    }

    @Test
    fun kategoriDariSistem() {
        assertEquals(sosmed, kategoriDari(ApplicationInfo.CATEGORY_SOCIAL))
        assertEquals(R.string.kategori_hiburan, kategoriDari(ApplicationInfo.CATEGORY_GAME))
        assertEquals(R.string.kategori_hiburan, kategoriDari(ApplicationInfo.CATEGORY_VIDEO))
        assertEquals(lainnya, kategoriDari(ApplicationInfo.CATEGORY_UNDEFINED))
        assertEquals(lainnya, kategoriDari(ApplicationInfo.CATEGORY_PRODUCTIVITY))
    }

    @Test
    fun aplikasiDicopotTetapTampil() {
        val hasil = aplikasiDariPaket(listOf("b", "com.dicopot"), data)
        assertEquals(listOf("WhatsApp", "com.dicopot"), hasil.map { it.nama })
        assertEquals(null, hasil[1].ikon)
    }
}
