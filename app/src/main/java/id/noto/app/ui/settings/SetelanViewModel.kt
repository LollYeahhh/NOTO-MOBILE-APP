package id.noto.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.noto.app.data.SetelanRepository
import id.noto.app.service.jadwalkanRingkasan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel tab Setelan: memegang nilai Setelan untuk layar dan menyimpan perubahannya ke DataStore.
 * AndroidViewModel = ViewModel yang membawa konteks aplikasi, jadi tidak perlu factory untuk membuat repository.
 */
class SetelanViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = SetelanRepository(app)

    /** Nilai Setelan terkini; dibaca sejak ViewModel dibuat, sebelum tab Setelan dibuka. */
    val setelan: StateFlow<SetelanNoto> = repository.setelan.stateIn(viewModelScope, SharingStarted.Eagerly, SetelanNoto())

    fun ubah(baru: SetelanNoto) {
        viewModelScope.launch {
            repository.simpan(baru)
            // Jam ringkasan, sakelar mingguan, atau jam tenang bisa berubah: setel ulang alarmnya.
            jadwalkanRingkasan(getApplication())
        }
    }
}
