package id.noto.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.noto.app.R
import id.noto.app.data.NotoDatabase
import id.noto.app.data.SetelanRepository
import id.noto.app.data.muatAplikasiTerpasang
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.settings.JAM_TENANG_SELESAI
import id.noto.app.ui.settings.SetelanNoto
import id.noto.app.ui.stats.statistikHariIni
import id.noto.app.ui.stats.statistikMingguan
import id.noto.app.util.formatDurasi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

// Ringkasan harian dan mingguan (Tahap 8D-3). Satu alarm tidak tepat waktu (AlarmManager) ke jam ringkasan
// berikutnya; setiap alarm berbunyi, ringkasan dikirim lalu alarm berikutnya disetel.

/** Alarm ringkasan berikutnya: kapan berbunyi, dan tanggal yang diringkas. */
data class JadwalRingkasan(val waktu: LocalDateTime, val tanggal: LocalDate)

/**
 * Jam ringkasan [jamMenit] berikutnya setelah [sekarang]. Kalau jatuh di jam tenang dan [jamTenang] nyala,
 * dikirim saat jam tenang selesai (06.00), tapi tetap meringkas tanggal semula.
 */
fun ringkasanBerikutnya(sekarang: LocalDateTime, jamMenit: Int, jamTenang: Boolean): JadwalRingkasan {
    var jadwal = sekarang.toLocalDate().atStartOfDay().plusMinutes(jamMenit.toLong())
    if (!jadwal.isAfter(sekarang)) jadwal = jadwal.plusDays(1)
    if (!jamTenang || !diJamTenang(jadwal)) return JadwalRingkasan(jadwal, jadwal.toLocalDate())
    var kirim = jadwal.toLocalDate().atStartOfDay().plusMinutes(JAM_TENANG_SELESAI.toLong())
    if (kirim.isBefore(jadwal)) kirim = kirim.plusDays(1)
    return JadwalRingkasan(kirim, jadwal.toLocalDate())
}

/** Hari Minggu dengan ringkasan mingguan nyala: kirim mingguan (menggantikan harian). */
fun ringkasanMingguan(tanggal: LocalDate, setelan: SetelanNoto): Boolean =
    setelan.ringkasanMingguan && tanggal.dayOfWeek == DayOfWeek.SUNDAY

/** Setel (atau ganti) alarm ringkasan sesuai setelan terbaru. Aman dipanggil berkali-kali. */
suspend fun jadwalkanRingkasan(context: Context) {
    val setelan = SetelanRepository(context).setelan.first()
    val j = ringkasanBerikutnya(LocalDateTime.now(), setelan.ringkasanHarianMenit, setelan.jamTenang)
    val intent = Intent(context, RingkasanAlarm::class.java).putExtra(TANGGAL, j.tanggal.toEpochDay())
    val tunda = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    // Tidak tepat waktu (tanpa izin exact alarm); tetap berbunyi saat Doze, mungkin terlambat beberapa menit.
    context.getSystemService(AlarmManager::class.java)
        .setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, j.waktu.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), tunda)
}

/** Menerima alarm ringkasan, mengirim notifikasinya, lalu menyetel alarm berikutnya. */
class RingkasanAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tanggal = LocalDate.ofEpochDay(intent.getLongExtra(TANGGAL, LocalDate.now().toEpochDay()))
        // goAsync: pekerjaan database dan daftar aplikasi selesai di latar (batas ±10 detik).
        val selesai = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                kirimRingkasan(context, tanggal)
                jadwalkanRingkasan(context)
            } finally {
                selesai.finish()
            }
        }
    }
}

private suspend fun kirimRingkasan(context: Context, tanggal: LocalDate) {
    val setelan = SetelanRepository(context).setelan.first()
    val db = NotoDatabase.ambil(context)
    // Hanya aplikasi di launcher yang dihitung, sama seperti layar Statistik.
    val aplikasi: Map<String, AplikasiPilihan> = muatAplikasiTerpasang(context).associateBy { it.paket }
    val pemakaian = db.pemakaianDao().rentang(tanggal.minusDays(13).toEpochDay(), tanggal.toEpochDay()).first()
    val (judul, isi) = if (ringkasanMingguan(tanggal, setelan)) {
        val awal = tanggal.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val s = statistikMingguan(pemakaian, db.kejadianDao().sejak(awal).first(), tanggal, aplikasi, db.aturanDao().semua().first())
        if (s.rataRataMenit == 0) return
        context.getString(R.string.notif_ringkasan_mingguan_judul) to
            if (s.perubahanPersen < 0) {
                context.getString(R.string.notif_ringkasan_mingguan_isi_turun, formatDurasi(s.rataRataMenit), -s.perubahanPersen)
            } else {
                context.getString(R.string.notif_ringkasan_mingguan_isi, formatDurasi(s.rataRataMenit))
            }
    } else {
        val s = statistikHariIni(pemakaian, tanggal, aplikasi)
        if (s.totalMenit == 0) return
        val teratas = s.aplikasi.firstOrNull()?.nama
        context.getString(R.string.notif_ringkasan_harian_judul) to
            if (teratas != null) {
                context.getString(R.string.notif_ringkasan_harian_isi, formatDurasi(s.totalMenit), teratas)
            } else {
                context.getString(R.string.notif_ringkasan_harian_isi_singkat, formatDurasi(s.totalMenit))
            }
    }
    tampilkanNotifikasi(context, ID_RINGKASAN, judul, isi, keStatistik = true)
}

private const val TANGGAL = "tanggal"
