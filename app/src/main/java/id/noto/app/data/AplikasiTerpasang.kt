package id.noto.app.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import id.noto.app.R
import id.noto.app.ui.blocks.AplikasiPilihan
import kotlin.math.roundToInt

/**
 * Semua aplikasi yang punya ikon di peluncur (launcher), kecuali NOTO sendiri.
 * Butuh <queries> untuk MAIN/LAUNCHER di AndroidManifest; sejak Android 11 aplikasi lain tidak terlihat tanpa itu.
 * Lambat (memuat ikon), jadi panggil di luar main thread.
 */
fun muatAplikasiTerpasang(context: Context): List<AplikasiPilihan> {
    val pm = context.packageManager
    val peluncur = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    // Ikon digambar sekali seukuran logo di Item Aplikasi (27 dp).
    val ukuran = (27 * context.resources.displayMetrics.density).roundToInt()
    return pm.queryIntentActivities(peluncur, 0)
        .map { it.activityInfo.applicationInfo }
        .distinctBy { it.packageName }
        .filter { it.packageName != context.packageName }
        .map { info ->
            AplikasiPilihan(
                paket = info.packageName,
                nama = info.loadLabel(pm).toString(),
                kategori = kategoriDari(info.category),
                // Gagal dimuat = null, nanti diganti ikon cadangan.
                ikon = runCatching { info.loadIcon(pm).toBitmap(ukuran, ukuran).asImageBitmap() }.getOrNull(),
            )
        }
}

/**
 * Kelompok di Pilih Aplikasi dari kategori yang diisi pembuat aplikasi (ApplicationInfo.category).
 * Android tidak punya kategori "komunikasi"; aplikasi tanpa kategori masuk "Lainnya".
 */
fun kategoriDari(kategoriSistem: Int): Int = when (kategoriSistem) {
    ApplicationInfo.CATEGORY_SOCIAL -> R.string.kategori_sosial_media
    ApplicationInfo.CATEGORY_VIDEO,
    ApplicationInfo.CATEGORY_AUDIO,
    ApplicationInfo.CATEGORY_GAME,
    ApplicationInfo.CATEGORY_IMAGE,
    -> R.string.kategori_hiburan
    else -> R.string.kategori_lainnya
}
