package id.noto.app.ui.stats

import android.content.pm.ApplicationInfo
import id.noto.app.R
import id.noto.app.data.AturanLengkap
import id.noto.app.data.JenisKejadian
import id.noto.app.data.Kejadian
import id.noto.app.data.Pemakaian
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.util.formatJam
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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

/**
 * Seksi "Hari ini" Detail Aplikasi dari tabel kejadian. Time out = aturan berisi aplikasi ini yang kuotanya habis
 * hari ini; [aturanTimeOut]/[jamTimeOut] dari yang terakhir habis (null kalau belum pernah).
 */
data class KejadianAplikasi(
    val latihanNapas: Int,
    val batalMembuka: Int,
    val timeOut: Int,
    val aturanTimeOut: String?,
    val jamTimeOut: String?,
)

fun kejadianAplikasi(
    kejadian: List<Kejadian>,
    paket: String,
    aturan: List<AturanLengkap>,
    hari: LocalDate,
    zona: ZoneId = ZoneId.systemDefault(),
): KejadianAplikasi {
    val hariIni = kejadian.filter { Instant.ofEpochMilli(it.waktu).atZone(zona).toLocalDate() == hari }
    val aturanApp = aturan.filter { paket in it.paket }.associateBy { it.aturan.id }
    val timeOut = hariIni.filter { it.jenis == JenisKejadian.TIME_OUT && it.aturanId in aturanApp }
    val terakhir = timeOut.maxByOrNull { it.waktu }
    return KejadianAplikasi(
        latihanNapas = hariIni.count { it.jenis == JenisKejadian.NAPAS && it.paket == paket },
        batalMembuka = hariIni.count { it.jenis == JenisKejadian.BATAL && it.paket == paket },
        timeOut = timeOut.size,
        aturanTimeOut = terakhir?.let { aturanApp.getValue(it.aturanId).aturan.nama },
        jamTimeOut = terakhir?.let {
            val w = Instant.ofEpochMilli(it.waktu).atZone(zona)
            formatJam(w.hour * 60 + w.minute)
        },
    )
}

// ---------- Minggu Ini (Tahap 8C) ----------

private val NAMA_HARI = listOf(
    R.string.hari_sen, R.string.hari_sel, R.string.hari_rab, R.string.hari_kam, R.string.hari_jum, R.string.hari_sab, R.string.hari_min,
)

/** Kategori Distribusi dari ApplicationInfo.category: Sosial → Sosmed, Video → Video, Produktivitas → Karya. */
private fun kategoriDistribusi(kategoriSistem: Int): Int = when (kategoriSistem) {
    ApplicationInfo.CATEGORY_SOCIAL -> R.string.kategori_sosmed
    ApplicationInfo.CATEGORY_VIDEO -> R.string.kategori_video
    ApplicationInfo.CATEGORY_PRODUCTIVITY -> R.string.kategori_karya
    else -> R.string.kategori_lainnya
}

/** Persen bulat yang jumlahnya tepat 100 (sisa pembulatan diberikan ke pecahan terbesar). */
internal fun persenBulat(nilai: List<Long>): List<Int> {
    val total = nilai.sum()
    if (total <= 0) return nilai.map { 0 }
    val dasar = nilai.map { (it * 100 / total).toInt() }
    val urut = nilai.indices.sortedByDescending { (nilai[it] * 100) % total }
    val hasil = dasar.toMutableList()
    repeat(100 - dasar.sum()) { hasil[urut[it]]++ }
    return hasil
}

/** Label skor: ≥ 80 Tenang, 50–79 Cukup Seimbang, di bawahnya Perlu Jeda (frame "Statistik - Label Skor"). */
internal fun labelSkor(skor: Int): Int = when {
    skor >= 80 -> R.string.skor_tenang
    skor >= 50 -> R.string.skor_cukup_seimbang
    else -> R.string.skor_perlu_jeda
}

/**
 * "Statistik - Minggu Ini" untuk 7 hari terakhir s.d. [hari] (seperti "Ritme 7 Hari Terakhir" di desain);
 * pembanding = 7 hari sebelumnya. Batang Ritme tetap berurutan Sen–Min, hari ini disorot.
 * Skor = 100 × hari yang di bawah 90% batas ÷ 7, +10 kalau rata-rata turun, maksimal 100.
 * Batas harian = jumlah kuota aturan aktif (null kalau belum ada). [StatistikMingguan.aplikasi] berisi semua
 * aplikasi dengan rata-rata ≥ 1 menit; Minggu Ini menampilkan 3 teratas.
 */
fun statistikMingguan(
    pemakaian: List<Pemakaian>,
    kejadian: List<Kejadian>,
    hari: LocalDate,
    aplikasi: Map<String, AplikasiPilihan>,
    aturan: List<AturanLengkap>,
    zona: ZoneId = ZoneId.systemDefault(),
): StatistikMingguan {
    val awal = hari.minusDays(6)
    val milik = pemakaian.milik(aplikasi)
    val mingguIni = milik.antara(awal, hari)
    val mingguLalu = milik.antara(awal.minusDays(7), awal.minusDays(1))
    val rata = menit(mingguIni.sumOf { it.detik }) / 7
    val rataLalu = menit(mingguLalu.sumOf { it.detik }) / 7
    val perubahan = if (rataLalu > 0 && rata < rataLalu) -((rataLalu - rata) * 100 / rataLalu) else 0
    val batas = aturan.filter { it.aturan.aktif }.sumOf { it.aturan.kuotaMenit }.takeIf { it > 0 }

    // Indeks 0 = Senin … 6 = Minggu; tiap hari diambil dari tanggalnya di 7 hari terakhir.
    val indeksHariIni = hari.dayOfWeek.value - 1
    val menitHari = (0..6).map { i ->
        val d = hari.minusDays(((indeksHariIni - i + 7) % 7).toLong())
        menit(milik.antara(d, d).sumOf { it.detik })
    }
    val sesuai = menitHari.count { batas == null || !mendekatiBatas(it, batas) }
    val skor = (100 * sesuai / 7 + if (perubahan < 0) 10 else 0).coerceAtMost(100)

    // Kategori: 2 terbesar (Sosmed/Video/Karya), sisanya digabung "Lainnya", supaya cocok dengan 3 warna bar.
    val perKategori = mingguIni.groupBy { kategoriDistribusi(aplikasi.getValue(it.paket).kategoriSistem) }
        .mapValues { (_, b) -> b.sumOf { it.detik } }
    val utama = perKategori.filterKeys { it != R.string.kategori_lainnya }.entries.sortedByDescending { it.value }.take(2)
    val lainnya = perKategori.values.sum() - utama.sumOf { it.value }
    val kelompok = utama.map { it.key to it.value } + listOfNotNull((R.string.kategori_lainnya to lainnya).takeIf { lainnya > 0 })
    val persen = persenBulat(kelompok.map { it.second })

    // Kejadian minggu ini per aplikasi, untuk keterangan baris.
    val awalMinggu = awal.atStartOfDay(zona).toInstant().toEpochMilli()
    val kejadianMinggu = kejadian.filter { it.waktu >= awalMinggu }
    val diAturan = aturan.flatMap { it.paket }.toSet()
    val apps = mingguIni.groupBy { it.paket }.map { (paket, baris) ->
        val a = aplikasi.getValue(paket)
        val avg = menit(baris.sumOf { it.detik }) / 7
        val avgLalu = menit(mingguLalu.filter { it.paket == paket }.sumOf { it.detik }) / 7
        val napas = kejadianMinggu.count { it.paket == paket && it.jenis == JenisKejadian.NAPAS }
        val batal = kejadianMinggu.count { it.paket == paket && it.jenis == JenisKejadian.BATAL }
        val (ket, arg) = when {
            batal > 0 -> R.string.jeda_tertahan to listOf(napas, batal)
            napas > 0 -> R.string.jeda_napas_sadar to listOf(napas)
            paket in diAturan -> R.string.terkendali_terjadwal to emptyList()
            else -> R.string.dibuka_per_hari to listOf(baris.sumOf { it.dibuka } / 7)
        }
        // Beda kurang dari 5 menit dari minggu lalu = "Stabil".
        val selisih = (avg - avgLalu).takeIf { kotlin.math.abs(it) >= 5 }
        AplikasiMingguan(a.logoContoh, a.nama, ket, arg, avg, selisih, a.ikon, paket)
    }.filter { it.menit >= 1 }.sortedByDescending { it.menit }

    return StatistikMingguan(
        rataRataMenit = rata,
        perubahanPersen = perubahan,
        skor = skor,
        labelSkor = labelSkor(skor),
        batasMenit = batas,
        ritme = menitHari.mapIndexed { i, m -> HariRitme(NAMA_HARI[i], m, hariIni = i == indeksHariIni) },
        kategori = kelompok.mapIndexed { i, (nama, _) -> KategoriPersen(nama, persen[i]) },
        aplikasi = apps,
    )
}
