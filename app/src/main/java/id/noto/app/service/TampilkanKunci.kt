package id.noto.app.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import id.noto.app.data.AturanLengkap
import id.noto.app.data.KuotaDao
import id.noto.app.data.cariKunci
import id.noto.app.ui.permission.Izin
import id.noto.app.ui.permission.izinDiberikan
import java.time.LocalDateTime

// Dipakai bersama PemantauService (tiap 2 detik) dan PengunciAksesibilitas (seketika saat aplikasi dibuka).

/** Kalau kuota aturan yang memuat [paket] sudah habis pada [t], buka layar Terkunci di atasnya. */
suspend fun kunciKalauHabis(context: Context, aturan: List<AturanLengkap>, paket: String, t: LocalDateTime, kuotaDao: KuotaDao) {
    val kunci = cariKunci(aturan, paket, t) { id, mulai -> kuotaDao.terpakai(id, mulai) } ?: return
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
    context.startActivity(TerkunciActivity.intent(context, kunci, nama))
}
