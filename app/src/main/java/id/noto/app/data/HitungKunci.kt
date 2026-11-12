package id.noto.app.data

import java.time.LocalDateTime
import java.time.ZoneId

/** Aturan yang mengunci sebuah aplikasi, dan kapan jendela jadwalnya selesai (aplikasi bebas lagi). */
data class KunciAplikasi(val aturan: Aturan, val selesai: LocalDateTime)

/** Akhir jendela jadwal [a] yang dimulai pada [mulai]. Mulai = selesai berarti sehari penuh (00.00–24.00). */
fun selesaiJendela(a: Aturan, mulai: LocalDateTime): LocalDateTime {
    val sehari = 24 * 60
    val durasi = if (a.mulaiMenit == a.selesaiMenit) sehari else (a.selesaiMenit - a.mulaiMenit + sehari) % sehari
    return mulai.plusMinutes(durasi.toLong())
}

/**
 * Kunci untuk [paket] pada [t]: aturan aktif yang memuat [paket], sedang dalam jadwal, dan kuotanya habis.
 * [terpakaiDetik] (aturanId, awal jendela epoch ms) = pemakaian aturan itu di jendela tersebut.
 * Kalau beberapa aturan habis sekaligus, dipakai yang selesainya paling akhir; null = tidak terkunci.
 */
suspend fun cariKunci(
    aturan: List<AturanLengkap>,
    paket: String,
    t: LocalDateTime,
    terpakaiDetik: suspend (Long, Long) -> Long,
): KunciAplikasi? {
    var hasil: KunciAplikasi? = null
    for (al in aturan) {
        val a = al.aturan
        if (!a.aktif || paket !in al.paket) continue
        val mulai = jendelaSekarang(a, t) ?: continue
        val mulaiMs = mulai.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (terpakaiDetik(a.id, mulaiMs) < a.kuotaMenit * 60L) continue
        val kunci = KunciAplikasi(a, selesaiJendela(a, mulai))
        if (hasil == null || kunci.selesai.isAfter(hasil.selesai)) hasil = kunci
    }
    return hasil
}
