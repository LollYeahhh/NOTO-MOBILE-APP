package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// "Baris Form" (node 1655), "Baris Setelan" (node 1722) dan "Item Aplikasi" (node 1664).
// Semua baris berjarak 12 antarisi dan isinya rata tengah secara vertikal.

/** Kotak ikon putih 40, sudut 12, isi di tengah (dipakai Baris Form dan Item Aplikasi). */
@Composable
private fun KotakIkon(ikon: Painter, ukuranIkon: Dp) {
    Box(Modifier.size(40.dp).background(NotoWarna.Putih, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
        Image(ikon, contentDescription = null, modifier = Modifier.size(ukuranIkon))
    }
}

/** Judul NOTO/Judul Item + keterangan NOTO/Kecil Teks, jarak 2. Mengisi sisa lebar baris. */
@Composable
private fun RowScope.TeksBaris(judul: String, keterangan: String?) {
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(judul, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama)
        if (keterangan != null) Text(keterangan, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
    }
}

/** Baris Form: kartu Permukaan sudut 16, padding 16, ikon dalam kotak putih, chevron di kanan. */
@Composable
fun NotoBarisForm(ikon: Painter, judul: String, nilai: String, onKlik: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KotakIkon(ikon, 24.dp)
        TeksBaris(judul, nilai)
        Image(painterResource(R.drawable.ic_chevron_kanan), contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

/**
 * Baris Setelan: tanpa latar, padding atas-bawah 12. Ikon 24, judul, keterangan dan nilai (Primer) opsional.
 * [toggle] = null → Aksi=Chevron (seluruh baris bisa diketuk, [onKlik]);
 * selain itu → Aksi=Toggle (seluruh baris mengubah toggle, [onUbah]).
 */
@Composable
fun NotoBarisSetelan(
    ikon: Painter,
    judul: String,
    modifier: Modifier = Modifier,
    keterangan: String? = null,
    nilai: String? = null,
    toggle: Boolean? = null,
    onKlik: () -> Unit = {},
    onUbah: (Boolean) -> Unit = {},
) {
    val aksi = if (toggle == null) {
        Modifier.clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
    } else {
        Modifier.toggleable(toggle, interactionSource = null, indication = null, role = Role.Switch, onValueChange = onUbah)
    }
    Row(
        modifier.fillMaxWidth().then(aksi).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(ikon, contentDescription = null, modifier = Modifier.size(24.dp))
        TeksBaris(judul, keterangan)
        if (nilai != null) Text(nilai, style = NotoTeks.Label, color = NotoWarna.Primer)
        if (toggle == null) {
            Image(painterResource(R.drawable.ic_chevron_kanan), contentDescription = null, modifier = Modifier.size(24.dp))
        } else {
            NotoToggle(toggle, onUbah)
        }
    }
}

/**
 * Item Aplikasi: padding atas-bawah 8, logo 27 dalam kotak putih 40.
 * Tipe=Statistik: isi [durasi]. Tipe=Pilih: isi [dipilih] (checkbox di kanan).
 * Logo di sini dari drawable contoh; di Tahap 5 diganti ikon dari PackageManager.
 */
@Composable
fun NotoItemAplikasi(
    logo: Painter,
    nama: String,
    keterangan: String,
    modifier: Modifier = Modifier,
    durasi: String? = null,
    dipilih: Boolean? = null,
    onUbah: (Boolean) -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KotakIkon(logo, 27.dp)
        TeksBaris(nama, keterangan)
        if (durasi != null) Text(durasi, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama)
        if (dipilih != null) NotoCheckbox(dipilih, onUbah)
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Baris", widthDp = 361)
@Composable
private fun PreviewBaris() {
    NOTOAPPTheme {
        Column(Modifier.background(NotoWarna.Putih), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NotoBarisForm(painterResource(R.drawable.ic_aplikasi), "Aplikasi", "Pilih aplikasi yang ingin diatur", {})
            Column(Modifier.width(329.dp)) {
                val lonceng = painterResource(R.drawable.ic_lonceng)
                NotoBarisSetelan(lonceng, "Notifikasi & Pengingat", keterangan = "Pengingat kuota dan ringkasan harian", nilai = "15m")
                NotoBarisSetelan(lonceng, "Notifikasi & Pengingat", keterangan = "Pengingat kuota dan ringkasan harian", toggle = true)
                val ig = painterResource(R.drawable.logo_instagram)
                NotoItemAplikasi(ig, "Instagram", "Rata-rata 1j 10m per hari", durasi = "25m")
                NotoItemAplikasi(ig, "Instagram", "Rata-rata 1j 10m per hari", dipilih = true)
            }
        }
    }
}
