package id.noto.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import id.noto.app.data.AturanLengkap
import id.noto.app.data.NotoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Layanan Aksesibilitas (AccessibilityService) Noto: layanan yang diberi tahu Android setiap kali jendela
 * aplikasi berganti. Dipakai HANYA untuk mengambil nama paket aplikasi yang baru tampil, supaya layar Terkunci
 * muncul seketika (tanpa jeda putaran 2 detik PemantauService). Isi layar tidak dibaca
 * (canRetrieveWindowContent=false di res/xml/aksesibilitas.xml) dan tidak ada data yang keluar dari perangkat.
 * Penghitungan kuota tetap di PemantauService, yang juga tetap menjadi cadangan pemicu kunci.
 */
class PengunciAksesibilitas : AccessibilityService() {
    private val cakupan = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var aturan: List<AturanLengkap> = emptyList()

    override fun onServiceConnected() {
        cakupan.launch { NotoDatabase.ambil(this@PengunciAksesibilitas).aturanDao().semua().collect { aturan = it } }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val paket = event.packageName?.toString() ?: return
        if (paket == packageName) return
        val kuotaDao = NotoDatabase.ambil(this).kuotaDao()
        cakupan.launch { kunciKalauHabis(this@PengunciAksesibilitas, aturan, paket, LocalDateTime.now(), kuotaDao) }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        cakupan.cancel()
        super.onDestroy()
    }
}
