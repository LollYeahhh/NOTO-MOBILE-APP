package id.noto.app.ui.permission

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
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
import id.noto.app.service.PengunciAksesibilitas

/**
 * Izin yang dibutuhkan pelacakan dan penguncian, urut seperti sheet-nya muncul.
 * TampilDiAtas = izin overlay ("Tampil di atas aplikasi lain"): Activity layar Terkunci boleh dibuka
 * dari latar belakang. Aksesibilitas = layanan PengunciAksesibilitas (opsional, mengunci seketika).
 * Perangkat = izin buatan pabrik HP (mulai otomatis, pop-up di latar belakang).
 */
enum class Izin { AksesPenggunaan, Notifikasi, Baterai, TampilDiAtas, Aksesibilitas, Perangkat }

// Merek yang punya izin tambahan mulai otomatis / pop-up latar (dontkillmyapp.com). Samsung tidak.
private val MEREK_IZIN_TAMBAHAN = setOf("xiaomi", "redmi", "poco", "huawei", "honor", "oppo", "realme", "vivo", "oneplus")

/** true kalau perangkat buatan [pabrik] (Build.MANUFACTURER) butuh sheet "Izin tambahan perangkat". */
fun perluIzinPerangkat(pabrik: String = Build.MANUFACTURER): Boolean = pabrik.lowercase() in MEREK_IZIN_TAMBAHAN

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
    Izin.TampilDiAtas -> Settings.canDrawOverlays(context)
    // Daftar layanan Aksesibilitas yang dinyalakan pengguna, dipisah ":" (contoh "id.noto.app/id.noto.app.service.PengunciAksesibilitas").
    Izin.Aksesibilitas -> {
        val milikNoto = ComponentName(context, PengunciAksesibilitas::class.java)
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
            .split(':').any { ComponentName.unflattenFromString(it) == milikNoto }
    }
    // Izin pabrik tidak bisa dicek aplikasi; dianggap beres hanya di perangkat yang tidak membutuhkannya.
    Izin.Perangkat -> !perluIzinPerangkat()
}

/** Izin yang belum diberikan, urut seperti [Izin]. */
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

/** Halaman Aksesibilitas di Setelan Android; pengguna memilih Noto lalu menyalakannya. */
fun bukaSetelanAksesibilitas(context: Context) {
    bukaSalahSatu(context, Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

/** Halaman Setelan Android untuk izin tampil di atas aplikasi lain, langsung ke Noto. */
fun bukaSetelanTampilDiAtas(context: Context) {
    val keNoto = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
    bukaSalahSatu(context, keNoto, Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
}

/**
 * Halaman izin Noto buatan pabrik. Xiaomi/Redmi/POCO: editor izin MIUI (ada "Mulai otomatis" dan
 * "Tampilkan jendela pop-up saat di latar belakang"); merek lain dan cadangannya: Info Aplikasi.
 */
fun bukaSetelanPerangkat(context: Context) {
    val infoAplikasi = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
    val miui = Intent("miui.intent.action.APP_PERM_EDITOR")
        .setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        .putExtra("extra_pkgname", context.packageName)
    if (Build.MANUFACTURER.lowercase() in setOf("xiaomi", "redmi", "poco")) {
        bukaSalahSatu(context, miui, infoAplikasi)
    } else {
        bukaSalahSatu(context, infoAplikasi)
    }
}

/** Buka intent pertama yang ada di HP ini; kalau tidak ada sama sekali, buka Setelan utama. */
fun bukaSalahSatu(context: Context, vararg intent: Intent) {
    for (i in intent) {
        try {
            context.startActivity(i)
            return
        } catch (_: ActivityNotFoundException) {
        } catch (_: SecurityException) {
            // Beberapa versi MIUI menolak membuka editor izinnya dari aplikasi lain.
        }
    }
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}
