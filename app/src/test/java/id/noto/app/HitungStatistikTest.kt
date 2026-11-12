package id.noto.app

import id.noto.app.data.Pemakaian
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.stats.harianAplikasi
import id.noto.app.ui.stats.statistikBulanan
import id.noto.app.ui.stats.statistikHariIni
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HitungStatistikTest {
    private val hari = LocalDate.of(2026, 11, 12)
    private val apps = listOf("yt", "wa").associateWith { AplikasiPilihan(it, it.uppercase(), 0) }
    private fun p(tgl: LocalDate, jam: Int, paket: String, detik: Long, dibuka: Int = 0) =
        Pemakaian(tgl.toEpochDay(), jam, paket, detik, dibuka)

    @Test
    fun hariIni_perJamDanDaftar_abaikanSistemDanDiBawahSemenit() {
        val data = listOf(
            p(hari, 7, "yt", 30 * 60),
            p(hari, 7, "wa", 59), // < 1 menit: tidak di daftar
            p(hari, 8, "yt", 15 * 60),
            p(hari, 8, "android", 3600), // bukan aplikasi launcher
            p(hari.minusDays(1), 8, "yt", 3600), // kemarin
        )
        val s = statistikHariIni(data, hari, apps)
        assertEquals(45, s.totalMenit) // 2759 detik → 45m
        assertEquals(24, s.menitPerJam.size)
        assertEquals(30f + 59 / 60f, s.menitPerJam[7], 0.001f)
        assertEquals(listOf("yt"), s.aplikasi.map { it.paket })
        assertEquals(45, s.aplikasi.single().menit)
    }

    @Test
    fun bulanan_rataRataPekanDanTurun() {
        val data = mutableListOf<Pemakaian>()
        // Pekan 1 (21–27 hari lalu): 140m/hari; pekan 4 (7 hari terakhir): 70m/hari.
        for (d in 21L..27L) data += p(hari.minusDays(d), 10, "yt", 140 * 60L, 2)
        for (d in 0L..6L) data += p(hari.minusDays(d), 10, "wa", 70 * 60L, 1)
        // Bulan lalu: 30 hari Oktober × 31 → 31 hari, total 310m → rata-rata 10m.
        data += p(LocalDate.of(2026, 10, 3), 9, "wa", 310 * 60L)
        val b = statistikBulanan(data, hari, apps)
        assertEquals(11, b.bulan)
        assertEquals(listOf(140, 0, 0, 70), b.menitPerPekan)
        assertEquals(50, b.turunPersen)
        // November 1–12: wa 7 hari (6–12) × 70 = 490; yt 21–27 hari lalu = 16–22 Okt, bukan bulan ini.
        assertEquals(490, b.totalMenit)
        assertEquals(490 / 12, b.rataRataMenit)
        // Oktober: 310m + yt 7 × 140 = 1290m ÷ 31.
        assertEquals((310 + 980) / 31, b.bulanLaluMenit)
        assertEquals(listOf("wa"), b.aplikasi.map { it.paket })
        assertEquals(7, b.aplikasi.single().dibuka)
    }

    @Test
    fun bulanan_tidakTurun_atauTanpaPembanding_nol() {
        val naik = listOf(p(hari.minusDays(25), 9, "yt", 600), p(hari, 9, "yt", 6000))
        assertEquals(0, statistikBulanan(naik, hari, apps).turunPersen)
        assertEquals(0, statistikBulanan(emptyList(), hari, apps).turunPersen)
    }

    @Test
    fun harianAplikasi_dibukaHariIniDanKemarin() {
        val data = listOf(
            p(hari, 20, "yt", 12 * 60, 3),
            p(hari, 21, "yt", 60, 1),
            p(hari.minusDays(1), 20, "yt", 60, 5),
            p(hari, 20, "wa", 600, 9),
        )
        val h = harianAplikasi(data, "yt", hari)
        assertEquals(13, h.menit)
        assertEquals(4, h.dibuka)
        assertEquals(5, h.dibukaKemarin)
        assertEquals(12f, h.menitPerJam[20], 0.001f)
    }
}
