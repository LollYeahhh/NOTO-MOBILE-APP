package id.noto.app

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import id.noto.app.ui.components.NotoBadge
import id.noto.app.ui.components.NotoBar24Jam
import id.noto.app.ui.components.NotoKartuBlokir
import id.noto.app.ui.components.StatusAturan
import id.noto.app.ui.components.NotoBarisForm
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoCheckbox
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoChipHari
import id.noto.app.ui.components.NotoInput
import id.noto.app.ui.components.NotoItemAplikasi
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoKolomWaktu
import id.noto.app.ui.components.NotoStepper
import id.noto.app.ui.components.NotoTabPeriode
import id.noto.app.ui.components.NotoToggle
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.NotoTombolTambahBlokir
import id.noto.app.ui.components.TipeBadge
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.blocks.BlokirScreen
import id.noto.app.ui.blocks.FormAturanScreen
import id.noto.app.ui.blocks.ModeForm
import id.noto.app.ui.blocks.PilihAplikasiScreen
import id.noto.app.ui.blocks.contohPilihanAwal
import id.noto.app.ui.blocks.contohFormTerisi
import id.noto.app.ui.home.BerandaScreen
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna

/**
 * Katalog komponen (build debug saja). Semua varian ditampilkan diam di posisi tetap
 * (margin kiri 16, mulai y80, jarak antarbaris 16) supaya screenshot bisa dibandingkan dengan FigCraft.
 * Ketuk area kosong di atas (y < 80) untuk pindah halaman.
 */
class KatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(SystemBarStyle.light(TRANSPARENT, TRANSPARENT), SystemBarStyle.light(TRANSPARENT, TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContent { NOTOAPPTheme { Katalog() } }
    }
}

private const val JUMLAH_HALAMAN = 9

@Composable
private fun Katalog() {
    var halaman by rememberSaveable { mutableIntStateOf(0) }
    // Kotak luar mengisi layar; kolom isi selebar 361 (lebar konten di desain) mulai di x16.
    Box(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        // Halaman 6 dan 7: Beranda - Kosong dan Blokir - Kosong layar penuh
        // (di aplikasi, kedua tab memakai data contoh yang terisi).
        // Halaman 8: Tambah Aturan Terisi (di aplikasi baru bisa tercapai setelah sheet Jadwal/Kuota, 3D).
        // Halaman 9: Pilih Aplikasi dengan pilihan seperti FigCraft (di aplikasi, pilihan awal = isi form).
        if (halaman == 5) BerandaScreen(aturan = emptyList())
        else if (halaman == 6) BlokirScreen(aturan = emptyList())
        else if (halaman == 7) FormAturanScreen(ModeForm.Tambah, contohFormTerisi, {}, {}, {})
        else if (halaman == 8) PilihAplikasiScreen(contohPilihanAwal, {}, {})
        else Column(
            Modifier
                .padding(start = 16.dp, top = 80.dp)
                .width(361.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (halaman) {
                0 -> HalamanAtom()
                1 -> HalamanBaris()
                2 -> HalamanTab()
                3 -> HalamanKartu(awal = true)
                else -> HalamanKartu(awal = false)
            }
        }
        // Strip ketuk di atas semua isi, supaya tetap bisa pindah halaman dari layar penuh.
        Box(Modifier.fillMaxWidth().height(80.dp).clickable { halaman = (halaman + 1) % JUMLAH_HALAMAN })
    }
}

@Composable
private fun HalamanAtom() {
    val baris = Arrangement.spacedBy(8.dp)
    Row(horizontalArrangement = baris) {
        NotoBadge(stringResource(R.string.katalog_badge_info), TipeBadge.Info)
        NotoBadge(stringResource(R.string.katalog_badge_netral), TipeBadge.Netral)
        NotoBadge(stringResource(R.string.katalog_badge_aktif), TipeBadge.Aktif)
    }
    NotoBadge(stringResource(R.string.katalog_badge_peringatan), TipeBadge.Peringatan)
    Row(horizontalArrangement = baris) {
        // Keadaan awal sama dengan desain; bisa diketuk untuk menguji perpindahan keadaan.
        var t1 by remember { mutableStateOf(true) }
        var t2 by remember { mutableStateOf(false) }
        var c1 by remember { mutableStateOf(true) }
        var c2 by remember { mutableStateOf(false) }
        NotoToggle(nyala = t1, onUbah = { t1 = it })
        NotoToggle(nyala = t2, onUbah = { t2 = it })
        NotoCheckbox(dipilih = c1, onUbah = { c1 = it })
        NotoCheckbox(dipilih = c2, onUbah = { c2 = it })
    }
    Row(horizontalArrangement = baris) {
        var k1 by remember { mutableStateOf(true) }
        var k2 by remember { mutableStateOf(false) }
        var h1 by remember { mutableStateOf(true) }
        var h2 by remember { mutableStateOf(false) }
        NotoChip(stringResource(R.string.katalog_chip), aktif = k1, onKlik = { k1 = !k1 })
        NotoChip(stringResource(R.string.katalog_chip), aktif = k2, onKlik = { k2 = !k2 })
        NotoChipHari(stringResource(R.string.katalog_hari_aktif), aktif = h1, onKlik = { h1 = !h1 })
        NotoChipHari(stringResource(R.string.katalog_hari_pasif), aktif = h2, onKlik = { h2 = !h2 })
    }
    NotoTombol(stringResource(R.string.katalog_tombol_utama), onKlik = {})
    NotoTombol(stringResource(R.string.katalog_tombol_teks), onKlik = {}, tipe = TipeTombol.Teks)
    NotoTombol(stringResource(R.string.katalog_tombol_permukaan), onKlik = {}, tipe = TipeTombol.Permukaan)
    NotoTombolTambahBlokir(onKlik = {})
    val placeholder = stringResource(R.string.katalog_input_placeholder)
    var isi by remember { mutableStateOf("") }
    NotoInput(isi, { isi = it }, placeholder)
    NotoInput(stringResource(R.string.katalog_input_isi), {}, placeholder)
}

// Baris Setelan, Item Aplikasi dan Stepper selebar 329 seperti komponennya di FigCraft.
@Composable
private fun HalamanBaris() {
    NotoBarisForm(
        painterResource(R.drawable.ic_aplikasi),
        stringResource(R.string.katalog_form_judul),
        stringResource(R.string.katalog_form_nilai),
        onKlik = {},
    )
    var toggleSetelan by remember { mutableStateOf(true) }
    var dipilih by remember { mutableStateOf(true) }
    val lonceng = painterResource(R.drawable.ic_lonceng)
    val judul = stringResource(R.string.katalog_setelan_judul)
    val ket = stringResource(R.string.katalog_setelan_ket)
    NotoBarisSetelan(lonceng, judul, Modifier.width(329.dp), keterangan = ket, nilai = stringResource(R.string.katalog_setelan_nilai))
    NotoBarisSetelan(lonceng, judul, Modifier.width(329.dp), keterangan = ket, toggle = toggleSetelan, onUbah = { toggleSetelan = it })
    val ig = painterResource(R.drawable.logo_instagram)
    val nama = stringResource(R.string.katalog_app_nama)
    val ketApp = stringResource(R.string.katalog_app_ket)
    NotoItemAplikasi(ig, nama, ketApp, Modifier.width(329.dp), durasi = stringResource(R.string.katalog_app_durasi))
    NotoItemAplikasi(ig, nama, ketApp, Modifier.width(329.dp), dipilih = dipilih, onUbah = { dipilih = it })
    NotoStepper(
        stringResource(R.string.katalog_stepper_angka),
        stringResource(R.string.katalog_stepper_satuan),
        onKurangi = {},
        onTambah = {},
        modifier = Modifier.width(329.dp),
    )
    NotoKolomWaktu(stringResource(R.string.katalog_waktu_label), stringResource(R.string.katalog_waktu), {}, Modifier.width(176.dp))
    NotoKepalaSheet(stringResource(R.string.katalog_sheet_judul), stringResource(R.string.katalog_sheet_sub), {})
}

@Composable
private fun HalamanTab() {
    // Tab pertama bisa diketuk untuk menguji animasi 300 ms; dua lainnya diam sebagai acuan.
    var tab by remember { mutableIntStateOf(0) }
    NotoTabPeriode(tab, { tab = it })
    NotoTabPeriode(1, {})
    NotoTabPeriode(2, {})
    // Bar 24 Jam selebar 329 dengan nilai bawaan FigCraft: 07.00–21.00, sekarang 14.00.
    NotoBar24Jam(7 * 60, 21 * 60, Modifier.width(329.dp), sekarangMenit = 14 * 60)
    // Uji jadwal melewati tengah malam (21.00–07.00, sekarang 23.00): harus jadi dua blok di kedua ujung.
    NotoBar24Jam(21 * 60, 7 * 60, Modifier.width(329.dp), sekarangMenit = 23 * 60, tampilkanLegenda = false)
}

// Halaman 4: Berlaku, Hampir Habis, Time Out. Halaman 5: Di Luar Jadwal, Nonaktif.
@Composable
private fun HalamanKartu(awal: Boolean) {
    val logo = listOf(R.drawable.logo_facebook, R.drawable.logo_instagram, R.drawable.logo_twitter, R.drawable.logo_tiktok, R.drawable.logo_whatsapp)
        .map { painterResource(it) }
    val judul = stringResource(R.string.katalog_kartu_judul)
    val jadwal = stringResource(R.string.katalog_kartu_jadwal)
    @Composable
    fun kartu(status: StatusAturan, label: Int, sisa: Int?, total: Int?, progres: Float) = NotoKartuBlokir(
        judul, jadwal, status, stringResource(label),
        sisa?.let { stringResource(it) } ?: "", total?.let { stringResource(it) } ?: "", progres, logo, {},
    )
    if (awal) {
        kartu(StatusAturan.Berlaku, R.string.katalog_berlaku_label, R.string.katalog_berlaku_sisa, R.string.katalog_berlaku_total, 164f / 329f)
        kartu(StatusAturan.HampirHabis, R.string.katalog_hampir_label, R.string.katalog_hampir_sisa, R.string.katalog_hampir_total, 309f / 329f)
        kartu(StatusAturan.TimeOut, R.string.katalog_timeout_label, R.string.katalog_timeout_sisa, R.string.katalog_timeout_total, 1f)
    } else {
        kartu(StatusAturan.DiLuarJadwal, R.string.katalog_luar_label, R.string.katalog_luar_sisa, R.string.katalog_luar_total, 0f)
        kartu(StatusAturan.Nonaktif, R.string.katalog_nonaktif_label, null, null, 0f)
    }
}
