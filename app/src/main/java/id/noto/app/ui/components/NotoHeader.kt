package id.noto.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

// Komponen "Header" di FigCraft (node 1442): tinggi 114, latar Permukaan.
// 59 dp teratas adalah ruang status bar. Status bar Android asli digambar di atasnya (edge-to-edge).

/** Header Tipe=Logo: logo NOTO di x16 y74. */
@Composable
fun NotoHeaderLogo(modifier: Modifier = Modifier) {
    LatarHeader(modifier) {
        Image(
            painter = painterResource(R.drawable.ic_logo_noto),
            contentDescription = stringResource(R.string.logo_noto),
            modifier = Modifier.offset(x = 16.dp, y = 74.dp),
        )
    }
}

/** Header Tipe=Halaman: ikon kiri 24 dp di x16 y72, judul (Judul Kartu) di x52 y72. */
@Composable
fun NotoHeaderHalaman(
    judul: String,
    onIkonKiri: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes ikonKiri: Int = R.drawable.ic_kembali,
    deskripsiIkonKiri: String = stringResource(R.string.kembali),
) {
    LatarHeader(modifier) {
        Image(
            painter = painterResource(ikonKiri),
            contentDescription = deskripsiIkonKiri,
            modifier = Modifier
                .offset(x = 16.dp, y = 72.dp)
                .size(24.dp)
                // Desain tidak punya efek tekan, jadi ripple dimatikan.
                .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onIkonKiri),
        )
        Text(
            text = judul,
            style = NotoTeks.JudulKartu,
            color = NotoWarna.TeksUtama,
            maxLines = 1,
            modifier = Modifier.offset(x = 52.dp, y = 72.dp).height(24.dp),
        )
    }
}

@Composable
private fun LatarHeader(modifier: Modifier, isi: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(114.dp)
            .background(NotoWarna.Permukaan),
        content = isi,
    )
}

@Preview(name = "Header - Logo", widthDp = 393)
@Composable
private fun PreviewHeaderLogo() {
    NOTOAPPTheme { NotoHeaderLogo() }
}

// Judul di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Header - Halaman", widthDp = 393)
@Composable
private fun PreviewHeaderHalaman() {
    NOTOAPPTheme { NotoHeaderHalaman(judul = "Aturan Baru", onIkonKiri = {}) }
}
