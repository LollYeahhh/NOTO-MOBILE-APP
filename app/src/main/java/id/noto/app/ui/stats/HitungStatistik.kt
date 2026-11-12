package id.noto.app.ui.stats

import id.noto.app.data.Pemakaian
import id.noto.app.ui.blocks.AplikasiPilihan
import java.time.LocalDate

// Hitungan Statistik dari tabel pemakaian (per jam per aplikasi). Fungsi murni supaya mudah diuji.
// Hanya aplikasi di [aplikasi] (punya ikon di launcher, tanpa Noto) yang dihitung; komponen sistem diabaikan.

/** Detik → menit penuh (dibulatkan ke bawah, seperti angka screen time di Android). */
private fun menit(detik: Long): Int = (detik / 60).toInt()

private fun List<Pemakaian>.milik(aplikasi: Map<String, AplikasiPilihan>) = filter { it.paket in aplikasi }

private fun List<Pemakaian>.antara(dari: LocalDate, sampai: LocalDate) =
    filter { it.tanggal in dari.toEpochDay()..sampai.toEpochDay() }

/** "Statistik - Screen Time": total hari ini, menit per jam (24 batang), aplikasi yang dipakai ≥ 1 menit. */
fun statistikHariIni(data: List<Pemakaian>, hari: LocalDate, aplikasi: Map<String, AplikasiPilihan>): StatistikHariIni {
    val hariIni = data.milik(aplikasi).antara(hari, hari)
    return StatistikHariIni(
        totalMenit = menit(hariIni.sumOf { it.detik }),
        menitPerJam = List(24) { jam -> hariIni.filter { it.jam == jam }.sumOf { it.detik } / 60f },
        aplikasi = hariIni.groupBy { it.paket }
            .map { (paket, baris) ->
                val a = aplikasi.getValue(paket)
                PemakaianHariIni(a.logoContoh, a.nama, menit(baris.sumOf { it.detik }), a.ikon, paket)
            }
            .filter { it.menit >= 1 }
            .sortedByDescending { it.menit },
    )
}

/**
 * "Statistik - Bulan Ini". Rata-rata harian = total ÷ hari yang sudah lewat (termasuk hari ini);
 * bulan lalu = total bulan lalu ÷ jumlah harinya. Empat pekan = 4 × 7 hari yang berakhir hari ini (pekan 4 =
 * 7 hari terakhir), nilainya rata-rata harian. [StatistikBulanan.turunPersen] 0 = tidak turun / belum ada pembanding.
 */
fun statistikBulanan(data: List<Pemakaian>, hari: LocalDate, aplikasi: Map<String, AplikasiPilihan>): StatistikBulanan {
    val milik = data.milik(aplikasi)
    val awal = hari.withDayOfMonth(1)
    val bulanIni = milik.antara(awal, hari)
    val awalLalu = awal.minusMonths(1)
    val total = menit(bulanIni.sumOf { it.detik })
    val pekan = (3 downTo 0).map { k ->
        val akhir = hari.minusDays(7L * k)
        menit(milik.antara(akhir.minusDays(6), akhir).sumOf { it.detik }) / 7
    }
    val (p1, p4) = pekan.first() to pekan.last()
    return StatistikBulanan(
        bulan = hari.monthValue,
        totalMenit = total,
        rataRataMenit = total / hari.dayOfMonth,
        bulanLaluMenit = menit(milik.antara(awalLalu, awal.minusDays(1)).sumOf { it.detik }) / awalLalu.lengthOfMonth(),
        turunPersen = if (p1 > 0 && p4 < p1) (p1 - p4) * 100 / p1 else 0,
        menitPerPekan = pekan,
        aplikasi = bulanIni.groupBy { it.paket }
            .map { (paket, baris) ->
                val a = aplikasi.getValue(paket)
                AplikasiBulanan(a.logoContoh, a.nama, baris.sumOf { it.dibuka }, menit(baris.sumOf { it.detik }), a.ikon, paket)
            }
            .filter { it.menit >= 1 }
            .sortedByDescending { it.menit }
            .take(3),
    )
}

/** Isi Detail Aplikasi dari data asli: menit per jam hari ini, total, dibuka hari ini dan kemarin. */
data class HarianAplikasi(val menitPerJam: List<Float>, val menit: Int, val dibuka: Int, val dibukaKemarin: Int)

fun harianAplikasi(data: List<Pemakaian>, paket: String, hari: LocalDate): HarianAplikasi {
    val milik = data.filter { it.paket == paket }
    val hariIni = milik.antara(hari, hari)
    return HarianAplikasi(
        menitPerJam = List(24) { jam -> hariIni.filter { it.jam == jam }.sumOf { it.detik } / 60f },
        menit = menit(hariIni.sumOf { it.detik }),
        dibuka = hariIni.sumOf { it.dibuka },
        dibukaKemarin = milik.antara(hari.minusDays(1), hari.minusDays(1)).sumOf { it.dibuka },
    )
}
