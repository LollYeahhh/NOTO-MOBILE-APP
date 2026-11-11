package id.noto.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Menyalakan pemantau lagi setelah HP dinyalakan ulang (butuh izin RECEIVE_BOOT_COMPLETED). */
class MulaiSaatBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) PemantauService.mulai(context)
    }
}
