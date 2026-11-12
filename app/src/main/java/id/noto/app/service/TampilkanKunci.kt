package id.noto.app.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import id.noto.app.data.AturanLengkap
import id.noto.app.data.Kejadian
import id.noto.app.data.KuotaDao
import id.noto.app.data.NotoDatabase
import id.noto.app.data.cariKunci
import id.noto.app.data.jendelaSekarang
import id.noto.app.ui.permission.Izin
import id.noto.app.ui.permission.izinDiberikan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

// Dipakai bersama PemantauService (tiap 2 detik) dan PengunciAksesibilitas (seketika saat aplikasi dibuka).

/** Kalau kuota aturan yang memuat [paket] sudah habis pada [t], buka layar Terkunci di atasnya. */
suspend fun kunciKalauHabis(context: Context, aturan: List<AturanLengkap>, paket: String, t: LocalDateTime, kuotaDao: KuotaDao) {
    val kunci = cariKunci(aturan, paket, t) { id, mulai -> kuotaDao.terpakai(id, mulai) } ?: return
    // Time out dicatat saat kunci pertama muncul di jendela ini (paling lambat ±2 detik setelah kuota habis).
    jendelaSekarang(kunci.aturan, t)?.let { mulai ->
        NotoDatabase.ambil(context).kejadianDao().catatTimeOut(milidetik(t), kunci.aturan.id, paket, milidetik(mulai))
    }
    // Android 10+ hanya mengizinkan Activity dibuka dari latar belakang kalau izin "Tampil di atas aplikasi lain"
    // diberikan, atau layanan Aksesibilitas Noto sedang aktif. Tanpa keduanya tidak dicoba: sistem pasti menolak,
    // dan setiap penolakan (tiap 2 detik) membuang kerja dan memenuhi log sistem.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        !izinDiberikan(context, Izin.TampilDiAtas) && !izinDiberikan(context, Izin.Aksesibilitas)
    ) {
        return
    }
    val nama = try {
        context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(paket, 0)).toString()
    } catch (_: PackageManager.NameNotFoundException) {
        paket
    }
    context.startActivity(TerkunciActivity.intent(context, kunci, nama, paket))
}

// Penulisan kejadian dari layar Terkunci tetap selesai walau Activity-nya sudah ditutup.
private val cakupanCatat = CoroutineScope(SupervisorJob() + Dispatchers.IO)

/** Simpan kejadian [jenis] (napas/batal) untuk aplikasi [paket] yang dikunci aturan [aturanId]. */
fun catatKejadian(context: Context, jenis: String, aturanId: Long, paket: String, mulaiJendela: Long) {
    val dao = NotoDatabase.ambil(context).kejadianDao()
    cakupanCatat.launch { dao.simpan(Kejadian(waktu = System.currentTimeMillis(), jenis = jenis, aturanId = aturanId, paket = paket, mulaiJendela = mulaiJendela)) }
}

private fun milidetik(t: LocalDateTime): Long = t.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
