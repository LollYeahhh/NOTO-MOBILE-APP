package id.noto.app.ui.blocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.noto.app.data.AturanLengkap
import id.noto.app.data.AturanRepository
import id.noto.app.data.NotoDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel aturan: daftar aturan dari Room untuk Beranda dan Blokir, serta penyimpanannya. */
class AturanViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = AturanRepository(NotoDatabase.ambil(app).aturanDao())

    /** Semua aturan; kosong sampai Room selesai dibaca (tertutup splash). */
    val semua: StateFlow<List<AturanLengkap>> = repository.semua.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Simpan aturan baru dari form yang sudah lengkap. */
    fun tambah(form: FormAturan, nama: String) {
        viewModelScope.launch { repository.simpan(form.keAturan(nama), form.aplikasi) }
    }
}
