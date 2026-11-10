package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisForm
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.components.NotoInput
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/** Tambah = "Blokir - Tambah Aturan" (node 2231/3531), Edit = "Blokir - Edit Aturan" (node 2789). */
enum class ModeForm { Tambah, Edit }

/**
 * Form aturan: Header Halaman, konten bergulir (padding 24/16, jarak antarseksi 24), lalu Bilah Aksi.
 * Tidak ada Bottom Nav di layar ini.
 * shortcut: baris Jadwal dan Kuota (sheet) serta "Hapus aturan ini" dipasang di Tahap 3D.
 */
@Composable
fun FormAturanScreen(
    mode: ModeForm,
    form: FormAturan,
    onUbah: (FormAturan) -> Unit,
    onTutup: () -> Unit,
    onSimpan: () -> Unit,
    onPilihAplikasi: () -> Unit = {},
    onHapus: () -> Unit = {},
    namaTerpakai: List<String> = emptyList(),
) {
    val tambah = mode == ModeForm.Tambah
    // Kolom nama boleh kosong; aturan lalu memakai nama otomatis (Blokir, Blokir 1, …).
    val nama = form.nama.ifBlank {
        namaOtomatis(stringResource(R.string.nama_otomatis), stringResource(R.string.nama_otomatis_nomor), namaTerpakai)
    }
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(
            judul = stringResource(if (tambah) R.string.aturan_baru else R.string.edit_aturan_judul),
            onIkonKiri = onTutup,
            ikonKiri = if (tambah) R.drawable.ic_tutup else R.drawable.ic_kembali,
            deskripsiIkonKiri = stringResource(if (tambah) R.string.tutup else R.string.kembali),
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Seksi(R.string.nama_aturan) {
                NotoInput(form.nama, { onUbah(form.copy(nama = it)) }, placeholder = stringResource(R.string.nama_aturan_contoh))
            }
            Seksi(R.string.apa_yang_diatur) {
                NotoBarisForm(
                    painterResource(R.drawable.ic_aplikasi),
                    stringResource(R.string.aplikasi),
                    if (form.aplikasi.isEmpty()) stringResource(R.string.pilih_aplikasi_ket) else form.aplikasi.joinToString(", "),
                    // Di Edit Aturan baris ini tidak punya interaksi di FigCraft (disepakati: ikuti desain).
                    onKlik = if (tambah) onPilihAplikasi else ({}),
                )
            }
            Seksi(R.string.kapan_berapa_lama) {
                val jadwal = form.jadwal
                NotoBarisForm(
                    painterResource(R.drawable.ic_kalender),
                    stringResource(R.string.jadwal),
                    if (jadwal == null) stringResource(R.string.jadwal_ket) else stringResource(R.string.jadwal_ringkas, jadwal.hariSingkat, jadwal.jam),
                    onKlik = {},
                )
                NotoBarisForm(
                    painterResource(R.drawable.ic_jam_pasir),
                    stringResource(R.string.kuota),
                    if (form.kuota == null) stringResource(R.string.kuota_ket) else stringResource(R.string.kuota_per_hari_aktif, form.kuota),
                    onKlik = {},
                )
            }
            Seksi(R.string.saat_time_out) {
                // Kartu Opsi: Permukaan sudut 16, padding 4/16.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    NotoBarisSetelan(
                        painterResource(R.drawable.ic_lonceng),
                        stringResource(R.string.senyapkan_notifikasi),
                        keterangan = stringResource(R.string.senyapkan_notifikasi_ket_form),
                        toggle = form.senyap,
                        onUbah = { onUbah(form.copy(senyap = it)) },
                    )
                }
            }
            Pratinjau(form, nama)
            if (!tambah) {
                NotoTombol(stringResource(R.string.hapus_aturan_ini), onKlik = onHapus, tipe = TipeTombol.Teks)
            }
        }
        BilahAksi {
            NotoTombol(
                stringResource(if (tambah) R.string.simpan_aturan else R.string.simpan_perubahan),
                onKlik = onSimpan,
                aktif = form.lengkap,
            )
        }
    }
}

/** Label seksi (NOTO/Label, teks sekunder) dan isinya, jarak 8. */
@Composable
private fun Seksi(label: Int, isi: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        isi()
    }
}

/** Pratinjau Aturan: panel Biru pucet sudut 12, padding 12, ikon info biru 24 + teks, jarak 8. */
@Composable
private fun Pratinjau(form: FormAturan, nama: String) {
    val jadwal = form.jadwal
    val teks = if (form.lengkap && jadwal != null && form.kuota != null) {
        stringResource(R.string.pratinjau_aturan, nama, form.kuota, jadwal.jam, jadwal.hariPanjang)
    } else {
        stringResource(R.string.pratinjau_kosong)
    }
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.InfoLatar, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(NotoWarna.Info),
        )
        Text(teks, Modifier.weight(1f), style = NotoTeks.KecilTeks, color = NotoWarna.Info)
    }
}

/**
 * Bilah Aksi: Putih, garis atas 1 px (Garis), 16 kiri-kanan, isi di tengah dengan jarak 8.
 * Bawaan (Tambah/Edit): padding 16 di bawah garis. Di FigCraft tingginya 99 (padding bawah 34)
 * mulai y754, jadi 1 dp terpotong tepi frame 852; yang terlihat hanya 98, maka bawah 33.
 */
@Composable
internal fun BilahAksi(atas: Dp = 17.dp, bawah: Dp = 33.dp, isi: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(NotoWarna.Putih)
            .drawBehind {
                drawLine(NotoWarna.Garis, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx())
            }
            .padding(start = 16.dp, end = 16.dp, top = atas, bottom = bawah),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = isi,
    )
}

@Preview(name = "Blokir - Tambah Aturan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTambahKosong() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Tambah, FormAturan(), {}, {}, {}) }
}

@Preview(name = "Blokir - Tambah Aturan Terisi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTambahTerisi() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Tambah, contohFormTerisi, {}, {}, {}) }
}

@Preview(name = "Blokir - Edit Aturan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewEdit() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Edit, contohFormEdit, {}, {}, {}) }
}
