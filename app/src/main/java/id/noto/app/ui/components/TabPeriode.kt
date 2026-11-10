package id.noto.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// "Tab Periode" (node 1132): tiga segmen sama lebar di atas Permukaan, tinggi 43, sudut 12.
// Segmen aktif = Primer + teks putih. Sudut segmen ujung mengikuti wadah; segmen tengah persegi.
// Prototype: smart animate 300 ms easeOut, jadi warna latar dan teks berpindah dengan animasi itu.
// EaseOut Compose = cubic-bezier(0, 0, 0.58, 1), sama dengan easeOut di FigCraft.

private val labelTab = listOf(R.string.tab_screen_time, R.string.tab_minggu_ini, R.string.tab_bulan_ini)
private val bentukTab = listOf(
    RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
    RoundedCornerShape(0.dp),
    RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
)

/** [terpilih]: 0 = Screen Time, 1 = Minggu Ini, 2 = Bulan Ini. */
@Composable
fun NotoTabPeriode(terpilih: Int, onPilih: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(43.dp).background(NotoWarna.Permukaan, RoundedCornerShape(12.dp))) {
        labelTab.forEachIndexed { i, label ->
            val aktif = i == terpilih
            val gerak = tween<Color>(300, easing = EaseOut)
            val latar by animateColorAsState(if (aktif) NotoWarna.Primer else NotoWarna.Primer.copy(alpha = 0f), gerak)
            val teks by animateColorAsState(if (aktif) NotoWarna.Putih else NotoWarna.TeksUtama, gerak)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(latar, bentukTab[i])
                    .clickable(interactionSource = null, indication = null, role = Role.Tab) { onPilih(i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(label), style = NotoTeks.Label, color = teks, maxLines = 1)
            }
        }
    }
}

@Preview(name = "Tab Periode", widthDp = 361)
@Composable
private fun PreviewTabPeriode() {
    NOTOAPPTheme {
        var terpilih by remember { mutableIntStateOf(0) }
        Box(Modifier.fillMaxWidth().background(NotoWarna.Putih)) { NotoTabPeriode(terpilih, { terpilih = it }) }
    }
}
