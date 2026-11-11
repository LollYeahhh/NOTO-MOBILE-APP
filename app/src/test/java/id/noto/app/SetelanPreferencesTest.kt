package id.noto.app

import androidx.datastore.preferences.core.mutablePreferencesOf
import id.noto.app.data.keSetelan
import id.noto.app.data.tulis
import id.noto.app.ui.blocks.JadwalForm
import id.noto.app.ui.settings.SetelanNoto
import org.junit.Assert.assertEquals
import org.junit.Test

class SetelanPreferencesTest {
    @Test
    fun kosongMemakaiBawaan() {
        assertEquals(SetelanNoto(), mutablePreferencesOf().keSetelan())
    }

    @Test
    fun tulisLaluBacaSama() {
        val diubah = SetelanNoto(
            pengingatSisa = false,
            sisaMenit = setOf(10),
            saatKuotaHabis = false,
            saatJadwal = true,
            ringkasanHarianMenit = 20 * 60 + 30,
            ringkasanMingguan = false,
            jamTenang = false,
            jadwalBawaan = JadwalForm(hari = setOf(5, 6), mulaiMenit = 22 * 60, selesaiMenit = 5 * 60),
            kuotaBawaanMenit = 45,
            jedaDetik = 30,
            kunciEdit = true,
            latihanNapas = false,
            sisaWaktu = false,
        )
        val prefs = mutablePreferencesOf()
        prefs.tulis(diubah)
        assertEquals(diubah, prefs.keSetelan())
    }

    @Test
    fun sisaMenitKosongTetapKosong() {
        val prefs = mutablePreferencesOf()
        prefs.tulis(SetelanNoto(sisaMenit = emptySet()))
        assertEquals(emptySet<Int>(), prefs.keSetelan().sisaMenit)
    }
}
