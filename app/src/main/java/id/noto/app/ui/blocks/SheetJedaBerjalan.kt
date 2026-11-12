package id.noto.app.ui.blocks

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import id.noto.app.R
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.settings.teksJeda
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import kotlinx.coroutines.delay

/**
 * Isi "Overlay - Jeda Berjalan (Tahap 8E)" (node 9490): Kepala Sheet, angka hitung mundur, lalu
 * "Batal, tetap aktif". Hitungan habis → [onSelesai]. X, tombol batal, atau Noto ditinggalkan → [onBatal].
 */
@Composable
fun ColumnScope.SheetJedaBerjalan(nama: String, detik: Int, onBatal: () -> Unit, onSelesai: () -> Unit) {
    var sisa by remember { mutableIntStateOf(detik) }
    val selesai by rememberUpdatedState(onSelesai)
    LaunchedEffect(Unit) {
        while (sisa > 0) { delay(1000); sisa-- }
        selesai()
    }
    // Pindah ke aplikasi lain atau layar dimatikan = batal (keputusan 3a).
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { onBatal() }

    NotoKepalaSheet(stringResource(R.string.jeda_berjalan_judul), stringResource(R.string.jeda_berjalan_ket, nama), onBatal)
    // Di atas 1 menit dibulatkan ke atas per menit ("2 menit" = paling lama 2 menit lagi), di bawahnya per detik.
    val tampil = if (sisa > 60) (sisa + 59) / 60 * 60 else sisa.coerceAtLeast(1)
    Text(
        teksJeda(tampil),
        Modifier.fillMaxWidth().tinggiBarisFigCraft(NotoTeks.AngkaHero),
        style = NotoTeks.AngkaHero,
        color = NotoWarna.TeksUtama,
        textAlign = TextAlign.Center,
    )
    NotoTombol(stringResource(R.string.batal_tetap_aktif), onKlik = onBatal)
}

@Preview(name = "Overlay - Jeda Berjalan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetJedaBerjalan() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetJedaBerjalan("Sosmed", 10, {}, {}) } }
}
