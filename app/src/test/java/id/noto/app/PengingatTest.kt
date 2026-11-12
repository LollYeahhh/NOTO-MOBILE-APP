package id.noto.app

import id.noto.app.data.Aturan
import id.noto.app.data.JenisKejadian
import id.noto.app.data.hariKeBitmask
import id.noto.app.service.diJamTenang
import id.noto.app.service.pengingatAturan
import id.noto.app.service.yangDikirim
import id.noto.app.ui.settings.SetelanNoto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class PengingatTest {
    // 07.00–21.00 setiap hari, kuota 60 menit.
    private val a = Aturan(id = 1, nama = "Sosmed", hariAktif = hariKeBitmask((0..6).toSet()), mulaiMenit = 7 * 60, selesaiMenit = 21 * 60, kuotaMenit = 60, aktif = true)
    private val setelan = SetelanNoto(pengingatSisa = true, sisaMenit = setOf(15, 5), saatKuotaHabis = true, saatJadwal = true)
    private val hari = LocalDateTime.of(2026, 11, 12, 0, 0)

    private fun cek(t: LocalDateTime, terpakaiMenit: Int, sudah: Set<String> = emptySet(), s: SetelanNoto = setelan) = runBlocking {
        pengingatAturan(a, t, s, { terpakaiMenit * 60L }, { jenis, _ -> jenis in sudah })
    }.map { it.jenis }

    @Test
    fun jadwalMulai_sekaliPerJendela() {
        assertEquals(listOf(JenisKejadian.NOTIF_MULAI), cek(hari.withHour(9), 0))
        assertEquals(emptyList<String>(), cek(hari.withHour(9), 0, setOf(JenisKejadian.NOTIF_MULAI)))
    }

    @Test
    fun sisaKuota_ambangTerkecilYangDikirim() {
        val sudah = setOf(JenisKejadian.NOTIF_MULAI)
        assertEquals(emptyList<String>(), cek(hari.withHour(9), 40, sudah))
        assertEquals(listOf("notif_sisa_15"), cek(hari.withHour(9), 46, sudah))
        // Langsung lewat dua ambang: keduanya ditandai, yang dikirim hanya "Sisa 5m".
        val dua = runBlocking { pengingatAturan(a, hari.withHour(9), setelan, { 56 * 60L }, { j, _ -> j in sudah }) }
        assertEquals(listOf("notif_sisa_5", "notif_sisa_15"), dua.map { it.jenis })
        assertEquals(listOf(5), yangDikirim(dua).map { it.menit })
    }

    @Test
    fun ambangTidakKurangDariKuota_dilewati() {
        val kecil = a.copy(kuotaMenit = 15)
        val hasil = runBlocking { pengingatAturan(kecil, hari.withHour(9), setelan, { 0L }, { j, _ -> j == JenisKejadian.NOTIF_MULAI }) }
        assertEquals(emptyList<String>(), hasil.map { it.jenis })
    }

    @Test
    fun kuotaHabis_tanpaSisa() {
        assertEquals(listOf(JenisKejadian.NOTIF_HABIS), cek(hari.withHour(9), 60, setOf(JenisKejadian.NOTIF_MULAI)))
    }

    @Test
    fun jadwalSelesai_hanya1JamSetelahnya() {
        assertEquals(listOf(JenisKejadian.NOTIF_SELESAI), cek(hari.withHour(21).withMinute(30), 0))
        assertEquals(emptyList<String>(), cek(hari.withHour(23), 0))
    }

    @Test
    fun sakelarMati_tidakAdaPengingat() {
        val mati = SetelanNoto(pengingatSisa = false, saatKuotaHabis = false, saatJadwal = false)
        assertEquals(emptyList<String>(), cek(hari.withHour(9), 60, s = mati))
        assertEquals(emptyList<String>(), cek(hari.withHour(9), 50, s = mati))
    }

    @Test
    fun jamTenang() {
        assertTrue(diJamTenang(hari.withHour(22)))
        assertTrue(diJamTenang(hari.withHour(5).withMinute(59)))
        assertFalse(diJamTenang(hari.withHour(6)))
        assertFalse(diJamTenang(hari.withHour(21).withMinute(59)))
    }
}
