package id.noto.app.ui.blocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.noto.app.data.Aturan
import id.noto.app.data.AturanLengkap
import id.noto.app.data.AturanRepository
import id.noto.app.data.Kuota
import id.noto.app.data.NotoDatabase
import id.noto.app.data.muatAplikasiTerpasang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** ViewModel aturan: daftar aturan dari Room untuk Beranda dan Blokir, serta penyimpanannya. */
class AturanViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = AturanRepository(NotoDatabase.ambil(app).aturanDao())

    /** Semua aturan; kosong sampai Room selesai dibaca (tertutup splash). */
    val semua: StateFlow<List<AturanLengkap>> = repository.semua.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Catatan kuota 2 hari terakhir (jendela terpanjang 24 jam), diperbarui pemantau tiap 2 detik.
     * shortcut: batasnya dihitung sekali saat ViewModel dibuat; kalau aplikasi terbuka berhari-hari,
     * catatan lama ikut terbaca. Hitungan tetap benar karena disaring per jendela.
     */
    val kuota: StateFlow<List<Kuota>> = NotoDatabase.ambil(app).kuotaDao()
        .sejak(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _terpasang = MutableStateFlow<List<AplikasiPilihan>>(emptyList())

    /** Aplikasi terpasang di HP untuk Pilih Aplikasi; dimuat saat aplikasi dibuka (tertutup splash). */
    val terpasang: StateFlow<List<AplikasiPilihan>> = _terpasang

    init {
        muatAplikasi()
    }

    /** Baca ulang aplikasi terpasang (di latar belakang); daftar lama tetap tampil sampai yang baru siap. */
    fun muatAplikasi() {
        viewModelScope.launch {
            _terpasang.value = withContext(Dispatchers.IO) { muatAplikasiTerpasang(getApplication()) }
        }
    }

    /** Simpan aturan baru dari form yang sudah lengkap. */
    fun tambah(form: FormAturan, nama: String) {
        viewModelScope.launch { repository.simpan(form.keAturan(nama), form.aplikasi) }
    }

    /** Simpan perubahan dari form Edit; status aktif dan waktu dibuat tetap dari aturan [lama]. */
    fun ubah(lama: Aturan, form: FormAturan, nama: String) {
        val baru = form.keAturan(nama).copy(id = lama.id, aktif = lama.aktif, dibuatPada = lama.dibuatPada)
        viewModelScope.launch { repository.simpan(baru, form.aplikasi) }
    }

    fun hapus(id: Long) {
        viewModelScope.launch { repository.hapus(id) }
    }

    fun ubahAktif(id: Long, aktif: Boolean) {
        viewModelScope.launch { repository.ubahAktif(id, aktif) }
    }

    fun ubahSenyap(id: Long, senyap: Boolean) {
        viewModelScope.launch { repository.ubahSenyap(id, senyap) }
    }
}
