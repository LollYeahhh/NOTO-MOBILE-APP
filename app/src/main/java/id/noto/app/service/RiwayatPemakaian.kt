package id.noto.app.service

import id.noto.app.data.Pemakaian
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Mengubah riwayat kejadian Android (waktu + [Peristiwa]) menjadi pemakaian per jam, untuk mengisi Statistik
 * dengan pemakaian sebelum Noto dipasang. Aturan "dibuka" sama dengan [PelacakLatar].
 * Logika murni (tanpa Android) supaya bisa diuji di unit test.
 *
 * [sampai]: batas akhir riwayat; aplikasi yang masih di layar dihitung sampai titik ini.
 * [abaikan]: paket yang tidak dicatat (Noto sendiri dan launcher).
 */
fun ringkasRiwayat(
    kejadian: List<Pair<Long, Peristiwa>>,
    sampai: Long,
    abaikan: Set<String>,
    zona: ZoneId,
): List<Pemakaian> {
    // Kunci: (epoch day, jam, paket) → [milidetik, dibuka]. Milidetik baru dibulatkan ke detik di akhir.
    val wadah = linkedMapOf<Triple<Long, Int, String>, LongArray>()
    fun kunci(waktu: Long, paket: String): Triple<Long, Int, String> {
        val t = Instant.ofEpochMilli(waktu).atZone(zona)
        return Triple(t.toLocalDate().toEpochDay(), t.hour, paket)
    }

    var diLayar: String? = null
    var mulai = 0L
    var terakhir: String? = null

    // Tambahkan lama [diLayar] dari [mulai] sampai [akhir], dipecah per jam.
    fun tutup(akhir: Long) {
        val paket = diLayar ?: return
        if (paket in abaikan) return
        var kursor = mulai
        while (kursor < akhir) {
            val jamBerikut = Instant.ofEpochMilli(kursor).atZone(zona).truncatedTo(ChronoUnit.HOURS).plusHours(1).toInstant().toEpochMilli()
            val ujung = minOf(akhir, jamBerikut)
            wadah.getOrPut(kunci(kursor, paket)) { LongArray(2) }[0] += ujung - kursor
            kursor = ujung
        }
    }

    for ((waktu, p) in kejadian) {
        if (waktu > sampai) break
        when (p) {
            is Peristiwa.Masuk -> {
                tutup(waktu)
                if (p.paket != terakhir && p.paket !in abaikan) wadah.getOrPut(kunci(waktu, p.paket)) { LongArray(2) }[1] += 1
                diLayar = p.paket
                mulai = waktu
                terakhir = p.paket
            }
            is Peristiwa.Keluar -> if (p.paket == diLayar) {
                tutup(waktu)
                diLayar = null
            }
            Peristiwa.LayarMati -> {
                tutup(waktu)
                diLayar = null
                terakhir = null
            }
        }
    }
    tutup(sampai)

    return wadah.map { (k, v) -> Pemakaian(k.first, k.second, k.third, v[0] / 1000, v[1].toInt()) }
        .filter { it.detik > 0 || it.dibuka > 0 }
}
