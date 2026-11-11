package id.noto.app.ui.permission

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Izin yang dibutuhkan pelacakan pemakaian (Tahap 6), urut seperti sheet-nya muncul.
 * Overlay, alarm tepat waktu, pop-up Xiaomi, dan Aksesibilitas menyusul di Tahap 7.
 */
enum class Izin { AksesPenggunaan, Notifikasi, Baterai }

/** true kalau [izin] sudah diberikan pengguna. */
fun izinDiberikan(context: Context, izin: Izin): Boolean = when (izin) {
    // Akses penggunaan bukan izin biasa (tidak ada dialog): dicek lewat AppOps.
    Izin.AksesPenggunaan -> {
        val appOps = context.getSystemService(AppOpsManager::class.java)
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        mode == AppOpsManager.MODE_ALLOWED
    }
    // Mencakup izin POST_NOTIFICATIONS (Android 13+) dan sakelar notifikasi aplikasi di Setelan.
    Izin.Notifikasi -> NotificationManagerCompat.from(context).areNotificationsEnabled()
    Izin.Baterai -> context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)
}

/** Izin yang belum diberikan, urut Akses penggunaan → Notifikasi → Baterai. */
fun izinBelum(context: Context): List<Izin> = Izin.entries.filterNot { izinDiberikan(context, it) }

/**
 * Izin yang belum diberikan, dibaca ulang setiap layar kembali tampil (misalnya setelah pengguna
 * kembali dari halaman Setelan Android).
 */
@Composable
fun rememberIzinBelum(): List<Izin> {
    val context = LocalContext.current
    var belum by remember { mutableStateOf(izinBelum(context)) }
    LifecycleResumeEffect(Unit) {
        belum = izinBelum(context)
        onPauseOrDispose { }
    }
    return belum
}

/** Halaman Setelan Android untuk Akses penggunaan; sebagian HP bisa langsung ke Noto lewat "package:". */
fun bukaSetelanAksesPenggunaan(context: Context) {
    val keNoto = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:${context.packageName}"))
    bukaSalahSatu(context, keNoto, Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
}

/** Halaman notifikasi Noto di Setelan Android (dipakai kalau dialog izin tidak bisa muncul lagi). */
fun bukaSetelanNotifikasi(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    bukaSalahSatu(context, intent)
}

/**
 * Daftar optimasi baterai di Setelan Android. Sengaja tanpa izin REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
 * (dibatasi Google Play): pengguna memilih Noto sendiri di daftar.
 */
fun bukaSetelanBaterai(context: Context) {
    bukaSalahSatu(context, Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
}

/** Buka intent pertama yang ada di HP ini; kalau tidak ada sama sekali, buka Setelan utama. */
fun bukaSalahSatu(context: Context, vararg intent: Intent) {
    for (i in intent) {
        try {
            context.startActivity(i)
            return
        } catch (_: ActivityNotFoundException) {
        }
    }
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}
