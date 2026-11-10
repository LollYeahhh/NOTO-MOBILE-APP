package id.noto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// Chip (node 1313) dan Chip Hari (node 1318). Garis 1 px di Tidak Aktif ikut dihitung dalam
// tata letak FigCraft, jadi padding-nya 1 dp lebih besar daripada chip Aktif.

/** Chip tinggi 32, lebar mengikuti teks: Aktif = Primer + teks Putih; Tidak Aktif = Putih + garis Garis. */
@Composable
fun NotoChip(teks: String, aktif: Boolean, onKlik: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(32.dp)
            .then(
                if (aktif) Modifier.background(NotoWarna.Primer, CircleShape)
                else Modifier.background(NotoWarna.Putih, CircleShape).border(1.dp, NotoWarna.Garis, CircleShape),
            )
            .semantics { selected = aktif }
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
            .padding(horizontal = if (aktif) 12.dp else 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(teks, style = NotoTeks.Label, color = if (aktif) NotoWarna.Putih else NotoWarna.TeksUtama, maxLines = 1)
    }
}

/** Chip Hari bulat 40: Aktif = Primer + teks Putih; Tidak Aktif = Putih + garis Garis + teks sekunder. */
@Composable
fun NotoChipHari(hari: String, aktif: Boolean, onKlik: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(40.dp)
            .then(
                if (aktif) Modifier.background(NotoWarna.Primer, CircleShape)
                else Modifier.background(NotoWarna.Putih, CircleShape).border(1.dp, NotoWarna.Garis, CircleShape),
            )
            .semantics { selected = aktif }
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik),
        contentAlignment = Alignment.Center,
    ) {
        Text(hari, style = NotoTeks.KecilLabel, color = if (aktif) NotoWarna.Putih else NotoWarna.TeksSekunder, maxLines = 1)
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Chip & Chip Hari")
@Composable
private fun PreviewChip() {
    NOTOAPPTheme {
        Row {
            NotoChip("1j 30m", aktif = true, onKlik = {})
            NotoChip("1j 30m", aktif = false, onKlik = {})
            NotoChipHari("Sen", aktif = true, onKlik = {})
            NotoChipHari("Min", aktif = false, onKlik = {})
        }
    }
}
