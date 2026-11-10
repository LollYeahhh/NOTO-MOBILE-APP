package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

// "Stepper" (node 1687) dan "Kolom Waktu" (node 2453), dipakai di sheet Atur Kuota dan Atur Jadwal.

/** Tombol bulat 48 Permukaan dengan ikon 24 di tengah. */
@Composable
private fun TombolBulat(ikon: Int, keterangan: String, onKlik: () -> Unit) {
    Box(
        Modifier
            .size(48.dp)
            .background(NotoWarna.Permukaan, CircleShape)
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik),
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(ikon), contentDescription = keterangan, modifier = Modifier.size(24.dp))
    }
}

/** Stepper: tombol kurangi di kiri, angka (NOTO/Angka Hero) + satuan di tengah, tombol tambah di kanan. */
@Composable
fun NotoStepper(
    angka: String,
    satuan: String,
    onKurangi: () -> Unit,
    onTambah: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TombolBulat(R.drawable.ic_minus, stringResource(R.string.kurangi), onKurangi)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(angka, Modifier.tinggiBarisFigCraft(NotoTeks.AngkaHero), style = NotoTeks.AngkaHero, color = NotoWarna.TeksUtama)
            Text(satuan, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
        TombolBulat(R.drawable.ic_tambah, stringResource(R.string.tambah), onTambah)
    }
}

/** Kolom Waktu: kotak Permukaan sudut 12, padding 10/16, label kecil di atas jam besar. Lebar diatur pemanggil. */
@Composable
fun NotoKolomWaktu(label: String, waktu: String, onKlik: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(NotoWarna.Permukaan, RoundedCornerShape(12.dp))
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(label, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        Text(waktu, Modifier.tinggiBarisFigCraft(NotoTeks.AngkaHero), style = NotoTeks.AngkaHero, color = NotoWarna.TeksUtama)
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Stepper & Kolom Waktu", widthDp = 329)
@Composable
private fun PreviewStepper() {
    NOTOAPPTheme {
        Column(Modifier.background(NotoWarna.Putih), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NotoStepper("1j 30m", "per hari aktif", {}, {})
            NotoKolomWaktu("Mulai", "07.00", {}, Modifier.width(176.dp))
        }
    }
}
