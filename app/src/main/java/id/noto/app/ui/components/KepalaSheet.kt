package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

/**
 * "Kepala Sheet" (node 1714): pegangan 36×4 di tengah, jarak 12, lalu judul (NOTO/Judul Kartu)
 * + subjudul (NOTO/Teks) di kiri dan tombol tutup 24 di kanan.
 */
@Composable
fun NotoKepalaSheet(judul: String, subjudul: String, onTutup: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(36.dp, 4.dp).background(NotoWarna.Garis, CircleShape))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(judul, Modifier.tinggiBarisFigCraft(NotoTeks.JudulKartu), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
                Text(subjudul, Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
            }
            Image(
                painterResource(R.drawable.ic_tutup),
                contentDescription = stringResource(R.string.tutup),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onTutup),
            )
        }
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Kepala Sheet", widthDp = 361)
@Composable
private fun PreviewKepalaSheet() {
    NOTOAPPTheme {
        Box(Modifier.background(NotoWarna.Putih)) { NotoKepalaSheet("Atur Jadwal", "Kapan aturan ini berlaku?", {}) }
    }
}
