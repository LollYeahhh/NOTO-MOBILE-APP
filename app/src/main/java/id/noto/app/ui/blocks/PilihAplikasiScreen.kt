package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.components.NotoInput
import id.noto.app.ui.components.NotoItemAplikasi
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.painterAplikasi
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/**
 * "Blokir - Pilih Aplikasi (Kategori)" dan "(A–Z)": Header Halaman, pencarian dan chip tampilan,
 * daftar per kategori atau satu daftar "Semua aplikasi", lalu Bilah Aksi dengan jumlah pilihan.
 * [awal] dan hasil [onPilih] berisi nama paket. Tidak ada Bottom Nav.
 */
@Composable
fun PilihAplikasiScreen(
    awal: List<String>,
    onKembali: () -> Unit,
    onPilih: (List<String>) -> Unit,
    aplikasi: List<AplikasiPilihan> = contohAplikasi,
) {
    var dipilih by rememberSaveable { mutableStateOf(awal.toSet()) }
    var cari by remember { mutableStateOf("") }
    var urutAZ by remember { mutableStateOf(false) }
    // Paket yang sudah tidak terpasang tidak ikut dihitung dan hilang dari aturan saat "Pilih" ditekan.
    val terpilih = aplikasi.filter { it.paket in dipilih }

    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.pilih_aplikasi_judul), onIkonKiri = onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Pencarian dan Urutan: jarak 12.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NotoInput(cari, { cari = it }, placeholder = stringResource(R.string.cari_aplikasi), tampilkanIkon = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NotoChip(stringResource(R.string.urut_kategori), aktif = !urutAZ, onKlik = { urutAZ = false })
                    NotoChip(stringResource(R.string.urut_a_z), aktif = urutAZ, onKlik = { urutAZ = true })
                }
            }
            val hasil = saringAplikasi(aplikasi, cari, urutAZ)
            if (hasil.isEmpty()) TidakDitemukan()
            hasil.forEach { k ->
                Kategori(
                    k,
                    dipilih = dipilih,
                    onUbah = { paket, pilih -> dipilih = if (pilih) dipilih + paket else dipilih - paket },
                    onPilihSemua = { dipilih = dipilih + k.aplikasi.map { it.paket } },
                )
            }
        }
        // Bilah Aksi: padding atas 12 di bawah garis 1 px, bawah 34, ringkasan lalu tombol (jarak 8).
        BilahAksi(atas = 13.dp, bawah = 34.dp) {
            Text(
                pluralStringResource(R.plurals.ringkasan_pilihan, terpilih.size, terpilih.size),
                Modifier.tinggiBarisFigCraft(NotoTeks.KecilTeks),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
            )
            NotoTombol(
                pluralStringResource(R.plurals.pilih_n_aplikasi, terpilih.size, terpilih.size),
                onKlik = { onPilih(terpilih.map { it.paket }) },
                aktif = terpilih.isNotEmpty(),
            )
        }
    }
}

/** "Blokir - Pilih Aplikasi Tidak Ditemukan" (node 5550): padding atas 56, kiri-kanan 40, jarak 6. */
@Composable
private fun TidakDitemukan() {
    Column(
        Modifier.fillMaxWidth().padding(start = 40.dp, end = 40.dp, top = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Ikon 64 dari vector 76 yang punya ruang tepi untuk tebal garis.
        Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.ic_cari_kosong), null, Modifier.requiredSize(76.dp))
        }
        Text(stringResource(R.string.cari_kosong_judul), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama, textAlign = TextAlign.Center)
        Text(
            stringResource(R.string.cari_kosong_ket),
            // Poppins di Android sedikit lebih rapat dari FigCraft; dikurangi 1 dp tiap sisi agar baris terpotong
            // di tempat yang sama ("… kata lain / atau periksa …").
            Modifier.fillMaxWidth().padding(horizontal = 1.dp),
            style = NotoTeks.KecilTeks,
            color = NotoWarna.TeksSekunder,
            textAlign = TextAlign.Center,
        )
    }
}

/** Kategori: kepala (label seksi kiri, "Pilih semua" kanan), lalu Kartu Aplikasi; jarak 8. */
@Composable
private fun Kategori(
    k: KategoriAplikasi,
    dipilih: Set<String>,
    onUbah: (String, Boolean) -> Unit,
    onPilihSemua: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(k.judul), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
            Text(
                stringResource(R.string.pilih_semua),
                Modifier
                    .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onPilihSemua)
                    .tinggiBarisFigCraft(NotoTeks.Label),
                style = NotoTeks.Label,
                color = NotoWarna.Primer,
            )
        }
        // Kartu Aplikasi: Permukaan sudut 16, padding 4/16.
        Column(
            Modifier
                .fillMaxWidth()
                .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            k.aplikasi.forEach { a ->
                val pilih = a.paket in dipilih
                NotoItemAplikasi(
                    painterAplikasi(a.ikon, a.logoContoh),
                    a.nama,
                    keterangan = null, // "Rata-rata … per hari" disembunyikan sampai Tahap 6
                    modifier = Modifier.clickable(interactionSource = null, indication = null, role = Role.Checkbox) { onUbah(a.paket, !pilih) },
                    dipilih = pilih,
                    onUbah = { onUbah(a.paket, it) },
                )
            }
        }
    }
}

@Preview(name = "Blokir - Pilih Aplikasi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewPilihAplikasi() {
    NOTOAPPTheme { PilihAplikasiScreen(contohPilihanAwal, {}, {}) }
}

/** Urutan kelompok di tampilan Kategori. */
private val urutanKategori = listOf(
    R.string.kategori_sosial_media,
    R.string.kategori_hiburan,
    R.string.kategori_komunikasi,
    R.string.kategori_lainnya,
)

/**
 * Kelompok untuk ditampilkan: hanya aplikasi yang namanya memuat [cari] (huruf besar/kecil sama saja), urut A–Z.
 * [urutAZ] = satu kelompok "Semua aplikasi"; selain itu per kategori. Kelompok tanpa hasil dibuang.
 */
fun saringAplikasi(aplikasi: List<AplikasiPilihan>, cari: String, urutAZ: Boolean): List<KategoriAplikasi> {
    val cocok = aplikasi.filter { it.nama.contains(cari.trim(), ignoreCase = true) }.sortedBy { it.nama.lowercase() }
    if (cocok.isEmpty()) return emptyList()
    if (urutAZ) return listOf(KategoriAplikasi(R.string.semua_aplikasi, cocok))
    return cocok.groupBy { it.kategori }
        .map { (judul, isi) -> KategoriAplikasi(judul, isi) }
        .sortedBy { urutanKategori.indexOf(it.judul) }
}

/** Pilihan di FigCraft: semua kecuali Facebook. */
val contohPilihanAwal = contohAplikasi.map { it.paket } - "com.facebook.katana"
