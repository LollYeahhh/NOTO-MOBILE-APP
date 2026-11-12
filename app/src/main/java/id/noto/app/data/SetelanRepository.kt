package id.noto.app.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import id.noto.app.ui.blocks.JadwalForm
import id.noto.app.ui.settings.SetelanNoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// File DataStore "setelan" (pasangan kunci–nilai). Satu objek untuk seluruh aplikasi.
private val Context.dataStoreSetelan by preferencesDataStore(name = "setelan")

/** Membaca dan menyimpan tab Setelan. Nilai yang belum pernah disimpan memakai bawaan [SetelanNoto] (sama dengan FigCraft). */
class SetelanRepository(context: Context) {
    private val dataStore = context.applicationContext.dataStoreSetelan

    val setelan: Flow<SetelanNoto> = dataStore.data.map { it.keSetelan() }

    suspend fun simpan(setelan: SetelanNoto) {
        dataStore.edit { it.tulis(setelan) }
    }
}

// Nama kunci jangan diubah setelah rilis: nilai lama di HP pengguna tersimpan dengan nama ini.
private object Kunci {
    val pengingatSisa = booleanPreferencesKey("pengingat_sisa")
    val sisaMenit = stringSetPreferencesKey("sisa_menit")
    val saatKuotaHabis = booleanPreferencesKey("saat_kuota_habis")
    val saatJadwal = booleanPreferencesKey("saat_jadwal")
    val ringkasanHarianMenit = intPreferencesKey("ringkasan_harian_menit")
    val ringkasanMingguan = booleanPreferencesKey("ringkasan_mingguan")
    val jamTenang = booleanPreferencesKey("jam_tenang")
    val jadwalHari = intPreferencesKey("jadwal_bawaan_hari") // bitmask, lihat hariKeBitmask
    val jadwalMulai = intPreferencesKey("jadwal_bawaan_mulai")
    val jadwalSelesai = intPreferencesKey("jadwal_bawaan_selesai")
    val kuotaBawaanMenit = intPreferencesKey("kuota_bawaan_menit")
    val jedaDetik = intPreferencesKey("jeda_detik")
    val kunciEdit = booleanPreferencesKey("kunci_edit")
    val latihanNapas = booleanPreferencesKey("latihan_napas")
    val sisaWaktu = booleanPreferencesKey("sisa_waktu")
    val izinPerangkatDijawab = booleanPreferencesKey("izin_perangkat_dijawab")
    val izinAksesibilitasDijawab = booleanPreferencesKey("izin_aksesibilitas_dijawab")
}

/** Isi DataStore → [SetelanNoto]; kunci yang belum ada memakai nilai bawaan. */
internal fun Preferences.keSetelan(): SetelanNoto {
    val b = SetelanNoto()
    return SetelanNoto(
        pengingatSisa = this[Kunci.pengingatSisa] ?: b.pengingatSisa,
        sisaMenit = this[Kunci.sisaMenit]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: b.sisaMenit,
        saatKuotaHabis = this[Kunci.saatKuotaHabis] ?: b.saatKuotaHabis,
        saatJadwal = this[Kunci.saatJadwal] ?: b.saatJadwal,
        ringkasanHarianMenit = this[Kunci.ringkasanHarianMenit] ?: b.ringkasanHarianMenit,
        ringkasanMingguan = this[Kunci.ringkasanMingguan] ?: b.ringkasanMingguan,
        jamTenang = this[Kunci.jamTenang] ?: b.jamTenang,
        jadwalBawaan = JadwalForm(
            hari = this[Kunci.jadwalHari]?.let(::bitmaskKeHari) ?: b.jadwalBawaan.hari,
            mulaiMenit = this[Kunci.jadwalMulai] ?: b.jadwalBawaan.mulaiMenit,
            selesaiMenit = this[Kunci.jadwalSelesai] ?: b.jadwalBawaan.selesaiMenit,
        ),
        kuotaBawaanMenit = this[Kunci.kuotaBawaanMenit] ?: b.kuotaBawaanMenit,
        jedaDetik = this[Kunci.jedaDetik] ?: b.jedaDetik,
        kunciEdit = this[Kunci.kunciEdit] ?: b.kunciEdit,
        latihanNapas = this[Kunci.latihanNapas] ?: b.latihanNapas,
        sisaWaktu = this[Kunci.sisaWaktu] ?: b.sisaWaktu,
        izinPerangkatDijawab = this[Kunci.izinPerangkatDijawab] ?: b.izinPerangkatDijawab,
        izinAksesibilitasDijawab = this[Kunci.izinAksesibilitasDijawab] ?: b.izinAksesibilitasDijawab,
    )
}

/** [SetelanNoto] → isi DataStore (semua kunci ditulis sekaligus). */
internal fun MutablePreferences.tulis(s: SetelanNoto) {
    this[Kunci.pengingatSisa] = s.pengingatSisa
    this[Kunci.sisaMenit] = s.sisaMenit.map { it.toString() }.toSet()
    this[Kunci.saatKuotaHabis] = s.saatKuotaHabis
    this[Kunci.saatJadwal] = s.saatJadwal
    this[Kunci.ringkasanHarianMenit] = s.ringkasanHarianMenit
    this[Kunci.ringkasanMingguan] = s.ringkasanMingguan
    this[Kunci.jamTenang] = s.jamTenang
    this[Kunci.jadwalHari] = hariKeBitmask(s.jadwalBawaan.hari)
    this[Kunci.jadwalMulai] = s.jadwalBawaan.mulaiMenit
    this[Kunci.jadwalSelesai] = s.jadwalBawaan.selesaiMenit
    this[Kunci.kuotaBawaanMenit] = s.kuotaBawaanMenit
    this[Kunci.jedaDetik] = s.jedaDetik
    this[Kunci.kunciEdit] = s.kunciEdit
    this[Kunci.latihanNapas] = s.latihanNapas
    this[Kunci.sisaWaktu] = s.sisaWaktu
    this[Kunci.izinPerangkatDijawab] = s.izinPerangkatDijawab
    this[Kunci.izinAksesibilitasDijawab] = s.izinAksesibilitasDijawab
}
