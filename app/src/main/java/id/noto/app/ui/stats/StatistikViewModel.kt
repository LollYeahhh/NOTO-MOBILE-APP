package id.noto.app.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.noto.app.data.NotoDatabase
import id.noto.app.data.Pemakaian
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/** ViewModel Statistik: catatan pemakaian yang dibutuhkan Screen Time, Bulan Ini, dan Detail Aplikasi. */
class StatistikViewModel(app: Application) : AndroidViewModel(app) {
    // Mulai dari yang lebih awal: awal bulan lalu atau 27 hari lalu (4 pekan). Tanpa batas akhir supaya
    // hari baru (lewat tengah malam) tetap terbaca. Diperbarui otomatis saat pemantau menulis.
    // shortcut: batas awal dihitung sekali; kalau aplikasi terbuka berhari-hari, catatan lama ikut terbaca (tetap benar).
    private val hari = LocalDate.now()
    private val dari = minOf(hari.withDayOfMonth(1).minusMonths(1), hari.minusDays(27)).toEpochDay()

    val pemakaian: StateFlow<List<Pemakaian>> = NotoDatabase.ambil(app).pemakaianDao()
        .rentang(dari, Long.MAX_VALUE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
