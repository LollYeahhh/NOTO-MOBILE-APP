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
import id.noto.app.ui.components.TipeBadge
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
 * Aplikasi di aturan dari nama paketnya. Aplikasi yang sudah dicopot tetap tampil:
 * namanya nama paket dan logonya ikon cadangan (ikon = null).
 */
fun aplikasiDariPaket(paket: List<String>, terpasang: List<AplikasiPilihan>): List<AplikasiPilihan> =
    paket.map { p -> terpasang.firstOrNull { it.paket == p } ?: AplikasiPilihan(p, p, R.string.kategori_lainnya) }

/** "07.00–21.00 · Sen–Jum". */
@Composable
fun Aturan.jadwalRingkas(): String {
    val jam = stringResource(R.string.rentang_singkat, formatJam(mulaiMenit), formatJam(selesaiMenit))
    val hari = ringkasHari(bitmaskKeHari(hariAktif), namaHariSingkat(), stringResource(R.string.setiap_hari), stringResource(R.string.rentang_singkat))
    return stringResource(R.string.jadwal_ringkas, jam, hari)
}

/** Kartu Blokir - Daftar. */
@Composable
fun AturanLengkap.keDaftar(w: Waktu, terpasang: List<AplikasiPilihan>): AturanDaftar {
    val a = aturan
    val logo = aplikasiDariPaket(paket, terpasang)
    val status = statusAturan(a, w)
    val jadwal = a.jadwalRingkas()
    val kuota = formatDurasi(a.kuotaMenit)
    return when (status) {
        StatusAturan.Nonaktif -> AturanDaftar(
            a.nama, stringResource(R.string.jadwal_kuota, jadwal, kuota), status,
            stringResource(R.string.seksi_nonaktif), "", "", 0f, logo, a.id,
        )
        StatusAturan.DiLuarJadwal -> AturanDaftar(
            a.nama, jadwal, status,
            stringResource(R.string.status_bebas_sampai, formatJam(a.mulaiMenit)),
            stringResource(R.string.kuota_siap, kuota),
            stringResource(R.string.mulai_lagi, formatJam(a.mulaiMenit)),
            0f, logo, a.id,
        )
        else -> AturanDaftar(
            a.nama, jadwal, status,
            stringResource(R.string.status_berlaku_sd, formatJam(a.selesaiMenit)),
            stringResource(R.string.sisa_menit, kuota),
            stringResource(R.string.terpakai_dari, formatDurasi(0), kuota),
            0f, logo, a.id,
        )
    }
}

/** Kartu Beranda. Tanda hubung biasa di jam, sama dengan FigCraft "Beranda - Terisi". */
fun AturanLengkap.keBeranda(terpasang: List<AplikasiPilihan>) = AturanBeranda(
    nama = aturan.nama,
    aktif = aturan.aktif,
    jam = "${formatJam(aturan.mulaiMenit)}-${formatJam(aturan.selesaiMenit)}",
    kuota = formatDurasi(aturan.kuotaMenit),
    logo = aplikasiDariPaket(paket, terpasang),
    id = aturan.id,
)

/** Menit dari [w] sampai jam selesai jadwal (jadwal yang melewati tengah malam ikut dihitung). */
fun menitSampaiSelesai(a: Aturan, w: Waktu): Int = (a.selesaiMenit - w.menit + 24 * 60) % (24 * 60)

/**
 * Isi layar Detail Aturan.
 * shortcut: pemakaian dianggap 0 sampai Tahap 6 (sisa = kuota penuh, dibuka 0 kali).
 * Di Luar Jadwal dan Nonaktif mengikuti frame "Blokir - Detail Aturan (Di Luar Jadwal)" dan "(Nonaktif)".
 */
@Composable
fun AturanLengkap.keDetail(w: Waktu, terpasang: List<AplikasiPilihan>): DetailAturan {
    val a = aturan
    val daftar = keDaftar(w, terpasang)
    val status = statusAturan(a, w)
    return DetailAturan(
        nama = a.nama,
        labelStatus = daftar.labelStatus,
        sisa = formatDurasi(a.kuotaMenit),
        kuota = formatDurasi(a.kuotaMenit),
        progres = 0f,
        terpakai = formatDurasi(0),
        dibuka = 0,
        bebasLagi = formatDurasi(menitSampaiSelesai(a, w)),
        jamReset = formatJam(a.mulaiMenit),
        jam = stringResource(R.string.rentang_singkat, formatJam(a.mulaiMenit), formatJam(a.selesaiMenit)),
        hariAktif = bitmaskKeHari(a.hariAktif),
        mulaiMenit = a.mulaiMenit,
        selesaiMenit = a.selesaiMenit,
        sekarangMenit = w.menit,
        aplikasi = daftar.logo.map { PemakaianAplikasi(it, 0, formatDurasi(0)) },
        badge = status.badge,
        keteranganKanan = when (status) {
            StatusAturan.Berlaku -> null
            StatusAturan.Nonaktif -> ""
            else -> stringResource(R.string.mulai_lagi, formatJam(a.mulaiMenit))
        },
        teksInfo = when (status) {
            StatusAturan.Nonaktif -> stringResource(R.string.info_nonaktif)
            StatusAturan.DiLuarJadwal -> stringResource(R.string.info_di_luar_jadwal, formatJam(a.mulaiMenit))
            else -> null
        },
    )
}

/** Aturan yang sudah ada → isian form Edit. */
fun AturanLengkap.keForm() = FormAturan(
    nama = aturan.nama,
    aplikasi = paket,
    jadwal = JadwalForm(bitmaskKeHari(aturan.hariAktif), aturan.mulaiMenit, aturan.selesaiMenit),
    kuotaMenit = aturan.kuotaMenit,
    senyap = aturan.senyap,
)
