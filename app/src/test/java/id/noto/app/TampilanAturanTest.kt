package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.bitmaskKeHari
import id.noto.app.data.hariKeBitmask
import id.noto.app.ui.blocks.FormAturan
import id.noto.app.ui.blocks.JadwalForm
import id.noto.app.ui.blocks.Waktu
import id.noto.app.ui.blocks.dalamJadwal
import id.noto.app.ui.blocks.keAturan
import id.noto.app.ui.blocks.statusAturan
import id.noto.app.ui.components.StatusAturan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TampilanAturanTest {
    private fun aturan(hari: Set<Int>, mulai: Int, selesai: Int, aktif: Boolean = true) =
        Aturan(nama = "Uji", hariAktif = hariKeBitmask(hari), mulaiMenit = mulai, selesaiMenit = selesai, kuotaMenit = 60, aktif = aktif)

    private val senJum = setOf(0, 1, 2, 3, 4)

    @Test
    fun jadwalSiang() {
        val a = aturan(senJum, 7 * 60, 21 * 60)
        assertTrue(dalamJadwal(a, Waktu(0, 7 * 60)))       // Senin 07.00: mulai
        assertTrue(dalamJadwal(a, Waktu(4, 20 * 60 + 59))) // Jumat 20.59
        assertFalse(dalamJadwal(a, Waktu(0, 21 * 60)))     // Senin 21.00: selesai, sudah bebas
        assertFalse(dalamJadwal(a, Waktu(0, 6 * 60)))      // Senin 06.00
        assertFalse(dalamJadwal(a, Waktu(5, 12 * 60)))     // Sabtu bukan hari aktif
    }

    @Test
    fun jadwalLewatTengahMalam() {
        val a = aturan(setOf(4), 22 * 60, 5 * 60) // Jumat 22.00–05.00
        assertTrue(dalamJadwal(a, Waktu(4, 23 * 60)))  // Jumat 23.00
        assertTrue(dalamJadwal(a, Waktu(5, 4 * 60)))   // Sabtu 04.00: lanjutan malam Jumat
        assertFalse(dalamJadwal(a, Waktu(5, 5 * 60)))  // Sabtu 05.00: selesai
        assertFalse(dalamJadwal(a, Waktu(4, 4 * 60)))  // Jumat 04.00: lanjutan Kamis, bukan hari aktif
        assertFalse(dalamJadwal(a, Waktu(5, 23 * 60))) // Sabtu 23.00
        // Minggu (6) → Senin (0) melewati batas minggu.
        val minggu = aturan(setOf(6), 22 * 60, 5 * 60)
        assertTrue(dalamJadwal(minggu, Waktu(0, 1 * 60)))
    }

    @Test
    fun sehariPenuh() {
        val a = aturan(setOf(2), 0, 0)
        assertTrue(dalamJadwal(a, Waktu(2, 0)))
        assertTrue(dalamJadwal(a, Waktu(2, 23 * 60 + 59)))
        assertFalse(dalamJadwal(a, Waktu(3, 0)))
    }

    @Test
    fun status() {
        val a = aturan(senJum, 7 * 60, 21 * 60)
        assertEquals(StatusAturan.Berlaku, statusAturan(a, Waktu(0, 12 * 60)))
        assertEquals(StatusAturan.DiLuarJadwal, statusAturan(a, Waktu(0, 22 * 60)))
        assertEquals(StatusAturan.Nonaktif, statusAturan(a.copy(aktif = false), Waktu(0, 12 * 60)))
    }

    @Test
    fun formKeAturan() {
        val form = FormAturan(
            aplikasi = listOf("Instagram"),
            jadwal = JadwalForm(setOf(5, 6), 22 * 60, 5 * 60),
            kuotaMenit = 45,
            senyap = true,
        )
        val a = form.keAturan("Malam")
        assertEquals("Malam", a.nama)
        assertEquals(setOf(5, 6), bitmaskKeHari(a.hariAktif))
        assertEquals(22 * 60, a.mulaiMenit)
        assertEquals(5 * 60, a.selesaiMenit)
        assertEquals(45, a.kuotaMenit)
        assertTrue(a.senyap)
        assertTrue(a.aktif)
    }
}
