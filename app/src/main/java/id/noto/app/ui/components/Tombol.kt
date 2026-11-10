package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// "Tombol Utama" (node 1294) dan "Tombol Tambah Blokir" (node 1734). Lebar 361 di desain =
// lebar layar dikurangi margin 16, jadi tombol mengisi lebar yang tersedia.

enum class TipeTombol(val latar: Color, val teks: Color) {
    Utama(NotoWarna.Primer, NotoWarna.Putih),
    Teks(Color.Transparent, NotoWarna.Primer),
    Permukaan(NotoWarna.Permukaan, NotoWarna.TeksUtama),
}

/**
 * Tombol tinggi 48, sudut 12, label NOTO/Label di tengah.
 * [aktif] = false: opacity 40% dan tidak bisa diketuk (catatan "Bilah Aksi" di desain).
 */
@Composable
fun NotoTombol(
    label: String,
    onKlik: () -> Unit,
    modifier: Modifier = Modifier,
    tipe: TipeTombol = TipeTombol.Utama,
    aktif: Boolean = true,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .alpha(if (aktif) 1f else 0.4f)
            .background(tipe.latar, RoundedCornerShape(12.dp))
            .clickable(enabled = aktif, interactionSource = null, indication = null, role = Role.Button, onClick = onKlik)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = NotoTeks.Label, color = tipe.teks, maxLines = 1)
    }
}

/** Tombol Tambah Blokir: tinggi 46, Permukaan, sudut 16, ikon plus 24 di tengah. */
@Composable
fun NotoTombolTambahBlokir(onKlik: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.tambah_aturan),
            modifier = Modifier.size(24.dp),
        )
    }
}

// Label di Preview sama dengan nilai di FigCraft (hanya untuk Preview).
@Preview(name = "Tombol", widthDp = 361)
@Composable
private fun PreviewTombol() {
    NOTOAPPTheme {
        Column {
            NotoTombol("Simpan Aturan", onKlik = {})
            NotoTombol("Lihat statistik", onKlik = {}, tipe = TipeTombol.Teks)
            NotoTombol("Batal", onKlik = {}, tipe = TipeTombol.Permukaan)
            NotoTombol("Simpan Aturan", onKlik = {}, aktif = false)
            NotoTombolTambahBlokir(onKlik = {})
        }
    }
}
