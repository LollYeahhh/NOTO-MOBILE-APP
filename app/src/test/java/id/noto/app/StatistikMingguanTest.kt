package id.noto.app

import android.content.pm.ApplicationInfo
import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.JenisKejadian
import id.noto.app.data.Kejadian
import id.noto.app.data.Pemakaian
import id.noto.app.data.hariKeBitmask
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.stats.labelSkor
import id.noto.app.ui.stats.persenBulat
import id.noto.app.ui.stats.statistikMingguan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class StatistikMingguanTest {
    private val zona = ZoneOffset.ofHours(7)
    private val hari = LocalDate.of(2026, 11, 12) // Kamis; Senin = 9 Nov
    private val apps = mapOf(
        "ig" to AplikasiPilihan("ig", "IG", 0, kategoriSistem = ApplicationInfo.CATEGORY_SOCIAL),
        "yt" to AplikasiPilihan("yt", "YT", 0, kategoriSistem = ApplicationInfo.CATEGORY_VIDEO),
        "doc" to AplikasiPilihan("doc", "Doc", 0, kategoriSistem = ApplicationInfo.CATEGORY_PRODUCTIVITY),
        "map" to AplikasiPilihan("map", "Map", 0),
    )
    private fun p(tgl: LocalDate, paket: String, menit: Int, dibuka: Int = 0) = Pemakaian(tgl.toEpochDay(), 10, paket, menit * 60L, dibuka)
    private fun aturan(kuota: Int, vararg paket: String) = AturanLengkap(
        Aturan(id = 1, nama = "A", hariAktif = hariKeBitmask((0..6).toSet()), mulaiMenit = 0, selesaiMenit = 0, kuotaMenit = kuota, aktif = true),
        paket.toList(),
    )

    @Test
    fun tujuhHariTerakhir_rataRata_perubahan_skor_dan_ritme() {
        val data = mutableListOf<Pemakaian>()
        // 7 hari terakhir (Jum 6 Nov … Kam 12 Nov): Jum 90, Sab 190, Min 125, Sen 130, Sel 105, Rab 200, Kam 135.
        val menit = listOf(90, 190, 125, 130, 105, 200, 135)
        menit.forEachIndexed { i, m -> data += p(hari.minusDays(6L - i), "ig", m) }
        // 7 hari sebelumnya: rata 200 → rata 139 = turun 30%.
        for (i in 7..13) data += p(hari.minusDays(i.toLong()), "ig", 200)
        val s = statistikMingguan(data, emptyList(), hari, apps, listOf(aturan(180, "ig")), zona)
        assertEquals(975 / 7, s.rataRataMenit)
        assertEquals(-30, s.perubahanPersen)
        assertEquals(180, s.batasMenit)
        // Sab 190 dan Rab 200 lewat 90% batas: 5 dari 7 sesuai = 71, +10 karena turun = 81.
        assertEquals(81, s.skor)
        assertEquals(R.string.skor_tenang, s.labelSkor)
        // Urutan Sen–Min.
        assertEquals(listOf(130, 105, 200, 135, 90, 190, 125), s.ritme.map { it.menit })
        assertEquals(listOf(false, false, false, true, false, false, false), s.ritme.map { it.hariIni })
    }

    @Test
    fun tanpaAturan_tanpaBatas_skorPenuh_tanpaBadge() {
        val s = statistikMingguan(listOf(p(hari, "ig", 30)), emptyList(), hari, apps, emptyList(), zona)
        assertNull(s.batasMenit)
        assertEquals(0, s.perubahanPersen)
        assertEquals(100, s.skor)
    }

    @Test
    fun kategori_duaTerbesarDanLainnya_jumlah100() {
        val data = listOf(p(hari, "ig", 42), p(hari, "yt", 35), p(hari, "doc", 13), p(hari, "map", 10))
        val s = statistikMingguan(data, emptyList(), hari, apps, emptyList(), zona)
        assertEquals(listOf(R.string.kategori_sosmed, R.string.kategori_video, R.string.kategori_lainnya), s.kategori.map { it.nama })
        assertEquals(listOf(42, 35, 23), s.kategori.map { it.persen })
    }

    @Test
    fun keteranganDanSelisih() {
        val ms = hari.minusDays(6).atStartOfDay(zona).toInstant().toEpochMilli() + 1000
        fun k(jenis: String, paket: String) = Kejadian(waktu = ms, jenis = jenis, aturanId = 1, paket = paket, mulaiJendela = 0)
        val data = listOf(
            p(hari, "ig", 7 * 60), p(hari, "yt", 7 * 60), p(hari, "doc", 7 * 60), p(hari, "map", 14, dibuka = 63),
            p(hari.minusDays(8), "ig", 7 * 60), // 7 hari sebelumnya ig rata 60m; sekarang 60m → Stabil
        )
        val kejadian = listOf(k(JenisKejadian.NAPAS, "ig"), k(JenisKejadian.NAPAS, "ig"), k(JenisKejadian.NAPAS, "yt"), k(JenisKejadian.BATAL, "yt"))
        val s = statistikMingguan(data, kejadian, hari, apps, listOf(aturan(60, "ig", "yt", "doc")), zona)
        val per = s.aplikasi.associateBy { it.paket }
        assertEquals(R.string.jeda_napas_sadar to listOf(2), per.getValue("ig").let { it.keterangan to it.argumen })
        assertEquals(R.string.jeda_tertahan to listOf(1, 1), per.getValue("yt").let { it.keterangan to it.argumen })
        assertEquals(R.string.terkendali_terjadwal, per.getValue("doc").keterangan)
        assertEquals(R.string.dibuka_per_hari to listOf(9), per.getValue("map").let { it.keterangan to it.argumen })
        assertNull(per.getValue("ig").selisihMenit)
        assertEquals(60, per.getValue("yt").selisihMenit)
        assertEquals(listOf(60, 60, 60, 2), s.aplikasi.map { it.menit })
    }

    @Test
    fun persenBulat_danLabel() {
        assertEquals(listOf(34, 33, 33), persenBulat(listOf(1, 1, 1)))
        assertEquals(listOf(0, 0), persenBulat(listOf(0, 0)))
        assertEquals(R.string.skor_cukup_seimbang, labelSkor(62))
        assertEquals(R.string.skor_perlu_jeda, labelSkor(49))
    }
}
