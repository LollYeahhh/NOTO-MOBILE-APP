package id.noto.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Menyalakan pemantau dan alarm ringkasan lagi setelah perangkat dinyalakan ulang (butuh izin RECEIVE_BOOT_COMPLETED). */
class MulaiSaatBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        PemantauService.mulai(context)
        // Alarm hilang saat perangkat dimatikan: setel ulang ringkasan.
        val selesai = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try { jadwalkanRingkasan(context) } finally { selesai.finish() }
        }
    }
}
