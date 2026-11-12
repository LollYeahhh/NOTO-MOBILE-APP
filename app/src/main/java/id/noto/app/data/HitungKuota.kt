package id.noto.app.data

import java.time.LocalDateTime
import java.time.ZoneId

// Jendela jadwal aturan: rentang waktu saat pemakaian dihitung terhadap kuota.
// Aturan jadwal sama dengan dalamJadwal() di TampilanAturan: mulai = selesai berarti sehari penuh,
// dan jadwal yang melewati tengah malam (22.00–05.00) dimulai di hari aktif.

/** Awal jendela jadwal yang sedang berjalan pada [t], atau null kalau [t] di luar jadwal. */
fun jendelaSekarang(a: Aturan, t: LocalDateTime): LocalDateTime? {
    val hari = bitmaskKeHari(a.hariAktif)
    val tanggal = t.toLocalDate()
    val hariIni = t.dayOfWeek.value - 1
    val kemarin = (hariIni + 6) % 7
    val menit = t.hour * 60 + t.minute
    return when {
        a.mulaiMenit == a.selesaiMenit -> if (hariIni in hari) tanggal.atStartOfDay() else null
        a.mulaiMenit < a.selesaiMenit ->
            if (hariIni in hari && menit >= a.mulaiMenit && menit < a.selesaiMenit) tanggal.atMenit(a.mulaiMenit) else null
        hariIni in hari && menit >= a.mulaiMenit -> tanggal.atMenit(a.mulaiMenit)
        kemarin in hari && menit < a.selesaiMenit -> tanggal.minusDays(1).atMenit(a.mulaiMenit)
        else -> null
    }
}

/** Awal jendela jadwal berikutnya setelah [t] (dipakai teks "kuota kembali penuh"), atau null kalau tidak ada hari aktif. */
fun jendelaBerikutnya(a: Aturan, t: LocalDateTime): LocalDateTime? {
    val hari = bitmaskKeHari(a.hariAktif)
    val sehariPenuh = a.mulaiMenit == a.selesaiMenit
    for (tambah in 0L..7L) {
        val tanggal = t.toLocalDate().plusDays(tambah)
        val mulai = if (sehariPenuh) tanggal.atStartOfDay() else tanggal.atMenit(a.mulaiMenit)
        if (tanggal.dayOfWeek.value - 1 in hari && mulai.isAfter(t)) return mulai
    }
    return null
}

/** Catatan kuota aturan [a] di jendela jadwal yang sedang berjalan pada [t]; kosong kalau di luar jadwal. */
fun pemakaianJendela(a: Aturan, kuota: List<Kuota>, t: LocalDateTime): List<Kuota> {
    val mulai = jendelaSekarang(a, t)?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: return emptyList()
    return kuota.filter { it.aturanId == a.id && it.mulaiJendela == mulai }
}

private fun java.time.LocalDate.atMenit(menit: Int): LocalDateTime = atTime(menit / 60, menit % 60)

/**
 * "Jeda sebelum mematikan" (Setelan > Aturan Pembatasan): hitung mundur [jedaDetik] dulu sebelum aturan [a]
 * dimatikan atau dihapus, tapi hanya saat aturannya sedang berlaku (aktif dan di dalam jadwal, termasuk time out).
 */
fun perluJeda(a: Aturan, t: LocalDateTime, jedaDetik: Int): Boolean =
    jedaDetik > 0 && a.aktif && jendelaSekarang(a, t) != null
