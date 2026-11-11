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
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi

/**
 * "Blokir - Pilih Aplikasi" (node 2316): Header Halaman, pencarian dan urutan, daftar per kategori,
 * lalu Bilah Aksi dengan ringkasan pilihan. Tidak ada Bottom Nav.
 * Pencarian dan chip urutan bekerja pada [kategori]; di Tahap 5 isinya diganti aplikasi terpasang.
 */
@Composable
fun PilihAplikasiScreen(
    awal: List<String>,
    onKembali: () -> Unit,
    onPilih: (List<String>) -> Unit,
    kategori: List<KategoriAplikasi> = contohKategoriAplikasi,
) {
    var dipilih by rememberSaveable { mutableStateOf(awal.toSet()) }
    var cari by remember { mutableStateOf("") }
    var urutAZ by remember { mutableStateOf(false) }
    val semua = kategori.flatMap { it.aplikasi }
    val terpilih = semua.filter { it.nama in dipilih }

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
                    NotoChip(stringResource(R.string.urut_paling_sering), aktif = !urutAZ, onKlik = { urutAZ = false })
                    NotoChip(stringResource(R.string.urut_a_z), aktif = urutAZ, onKlik = { urutAZ = true })
                }
                Text(
                    stringResource(R.string.catatan_rata_rata),
                    Modifier.fillMaxWidth(),
                    style = NotoTeks.KecilTeks,
                    color = NotoWarna.TeksSekunder,
                )
            }
            val hasil = saringAplikasi(kategori, cari, urutAZ)
            if (hasil.isEmpty()) TidakDitemukan()
            hasil.forEach { k ->
                Kategori(
                    k,
                    dipilih = dipilih,
                    onUbah = { nama, pilih -> dipilih = if (pilih) dipilih + nama else dipilih - nama },
                    onPilihSemua = { dipilih = dipilih + k.aplikasi.map { it.nama } },
                )
            }
        }
        // Bilah Aksi: padding atas 12 di bawah garis 1 px, bawah 34, ringkasan lalu tombol (jarak 8).
        BilahAksi(atas = 13.dp, bawah = 34.dp) {
            Text(
                stringResource(R.string.ringkasan_pilihan, terpilih.size, formatDurasi(terpilih.sumOf { it.rataRataMenit })),
                Modifier.tinggiBarisFigCraft(NotoTeks.KecilTeks),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.TeksSekunder,
            )
            NotoTombol(
                stringResource(R.string.pilih_n_aplikasi, terpilih.size),
                onKlik = { onPilih(terpilih.map { it.nama }) },
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
                val pilih = a.nama in dipilih
                NotoItemAplikasi(
                    painterResource(a.logo),
                    a.nama,
                    stringResource(R.string.rata_rata_per_hari, formatDurasi(a.rataRataMenit)),
                    modifier = Modifier.clickable(interactionSource = null, indication = null, role = Role.Checkbox) { onUbah(a.nama, !pilih) },
                    dipilih = pilih,
                    onUbah = { onUbah(a.nama, it) },
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

/**
 * Kategori untuk ditampilkan: hanya aplikasi yang namanya memuat [cari] (huruf besar/kecil sama saja),
 * diurut A–Z atau dari rata-rata pemakaian terbesar ("Paling sering"). Kategori tanpa hasil dibuang.
 */
fun saringAplikasi(kategori: List<KategoriAplikasi>, cari: String, urutAZ: Boolean): List<KategoriAplikasi> =
    kategori.mapNotNull { k ->
        val cocok = k.aplikasi.filter { it.nama.contains(cari.trim(), ignoreCase = true) }
        val urut = if (urutAZ) cocok.sortedBy { it.nama.lowercase() } else cocok.sortedByDescending { it.rataRataMenit }
        if (urut.isEmpty()) null else k.copy(aplikasi = urut)
    }

/** Pilihan di FigCraft: semua kecuali Facebook. */
val contohPilihanAwal = listOf("Instagram", "X", "TikTok", "WhatsApp")
