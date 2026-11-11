package id.noto.app.ui.blocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.res.stringResource
import id.noto.app.R
import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.bitmaskKeHari
import id.noto.app.data.hariKeBitmask
import id.noto.app.ui.components.StatusAturan
import id.noto.app.ui.home.AturanBeranda
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam
import id.noto.app.util.ringkasHari
import kotlinx.coroutines.delay
import java.time.LocalDateTime

// Mengubah aturan dari Room menjadi data yang siap ditampilkan di Blokir dan Beranda.

/** Waktu sekarang: [hari] 0 = Senin … 6 = Minggu, [menit] sejak tengah malam. */
data class Waktu(val hari: Int, val menit: Int)

fun LocalDateTime.keWaktu() = Waktu(dayOfWeek.value - 1, hour * 60 + minute)

/** Waktu sekarang yang diperbarui setiap pergantian menit, supaya status aturan ikut berubah. */
@Composable
fun rememberWaktu(): Waktu {
    val waktu by produceState(LocalDateTime.now().keWaktu()) {
        while (true) {
            delay(60_000L - System.currentTimeMillis() % 60_000L)
            value = LocalDateTime.now().keWaktu()
        }
    }
    return waktu
}

/**
 * Apakah [w] berada di jam terbatas aturan. Jadwal yang melewati tengah malam (22.00–05.00) dimulai
 * di hari aktif, jadi bagian pagi (00.00–05.00) ikut hari sebelumnya. Mulai = selesai berarti sehari penuh.
 */
fun dalamJadwal(a: Aturan, w: Waktu): Boolean {
    val hari = bitmaskKeHari(a.hariAktif)
    val kemarin = (w.hari + 6) % 7
    return when {
        a.mulaiMenit == a.selesaiMenit -> w.hari in hari
        a.mulaiMenit < a.selesaiMenit -> w.hari in hari && w.menit >= a.mulaiMenit && w.menit < a.selesaiMenit
        else -> (w.hari in hari && w.menit >= a.mulaiMenit) || (kemarin in hari && w.menit < a.selesaiMenit)
    }
}

/**
 * Status aturan saat [w].
 * shortcut: pemakaian dianggap 0 sampai Tahap 6, jadi Hampir Habis dan Time Out belum pernah muncul.
 */
fun statusAturan(a: Aturan, w: Waktu): StatusAturan = when {
    !a.aktif -> StatusAturan.Nonaktif
    dalamJadwal(a, w) -> StatusAturan.Berlaku
    else -> StatusAturan.DiLuarJadwal
}

/** Isian form yang sudah [FormAturan.lengkap] → baris tabel aturan. */
fun FormAturan.keAturan(nama: String): Aturan {
    val j = checkNotNull(jadwal) { "Form belum lengkap" }
    return Aturan(
        nama = nama,
        hariAktif = hariKeBitmask(j.hari),
        mulaiMenit = j.mulaiMenit,
        selesaiMenit = j.selesaiMenit,
        kuotaMenit = checkNotNull(kuotaMenit) { "Form belum lengkap" },
        senyap = senyap,
    )
}

/**
 * Logo aplikasi dari namanya.
 * shortcut: dicari di data contoh Pilih Aplikasi; diganti ikon dari PackageManager di Tahap 5.
 */
private fun logoAplikasi(paket: List<String>): List<Int> {
    val semua = contohKategoriAplikasi.flatMap { it.aplikasi }
    return paket.mapNotNull { nama -> semua.firstOrNull { it.nama == nama }?.logo }
}

/** Kartu Blokir - Daftar. */
@Composable
fun AturanLengkap.keDaftar(w: Waktu): AturanDaftar {
    val a = aturan
    val status = statusAturan(a, w)
    val jam = stringResource(R.string.rentang_singkat, formatJam(a.mulaiMenit), formatJam(a.selesaiMenit))
    val hari = ringkasHari(bitmaskKeHari(a.hariAktif), namaHariSingkat(), stringResource(R.string.setiap_hari), stringResource(R.string.rentang_singkat))
    val jadwal = stringResource(R.string.jadwal_ringkas, jam, hari)
    val kuota = formatDurasi(a.kuotaMenit)
    return when (status) {
        StatusAturan.Nonaktif -> AturanDaftar(
            a.nama, stringResource(R.string.jadwal_kuota, jadwal, kuota), status,
            stringResource(R.string.seksi_nonaktif), "", "", 0f, logoAplikasi(paket),
        )
        StatusAturan.DiLuarJadwal -> AturanDaftar(
            a.nama, jadwal, status,
            stringResource(R.string.status_bebas_sampai, formatJam(a.mulaiMenit)),
            stringResource(R.string.kuota_siap, kuota),
            stringResource(R.string.mulai_lagi, formatJam(a.mulaiMenit)),
            0f, logoAplikasi(paket),
        )
        else -> AturanDaftar(
            a.nama, jadwal, status,
            stringResource(R.string.status_berlaku_sd, formatJam(a.selesaiMenit)),
            stringResource(R.string.sisa_menit, kuota),
            stringResource(R.string.terpakai_dari, formatDurasi(0), kuota),
            0f, logoAplikasi(paket),
        )
    }
}

/** Kartu Beranda. Tanda hubung biasa di jam, sama dengan FigCraft "Beranda - Terisi". */
fun AturanLengkap.keBeranda() = AturanBeranda(
    nama = aturan.nama,
    aktif = aturan.aktif,
    jam = "${formatJam(aturan.mulaiMenit)}-${formatJam(aturan.selesaiMenit)}",
    kuota = formatDurasi(aturan.kuotaMenit),
    logo = logoAplikasi(paket),
)
