package id.noto.app.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.noto.app.ui.navigation.EaseOutFigCraft
import id.noto.app.ui.theme.NotoWarna

/** Durasi sheet naik (overlay "move in" 300 ms easeOut di FigCraft). */
private const val DURASI_SHEET_MS = 300

/**
 * Bottom sheet seperti overlay FigCraft: scrim Teks utama 40% di seluruh layar, sheet Putih
 * sudut atas 16, padding 8/16/34/16, jarak antarisi 16. Muncul naik dari bawah; menutup tanpa
 * animasi (aksi "close overlay" instant). Ketuk di luar sheet atau tombol back = [onTutup].
 * Kalau [tampil] sudah true saat pertama tampil, sheet langsung terlihat tanpa animasi.
 * Saat sheet ditukar dengan sheet lain (swap overlay), [geserMasuk] = false membuat sheet langsung
 * tampil, dan [scrimMasuk] = false membuat scrim tidak memudar ulang.
 */
@Composable
fun NotoSheet(
    tampil: Boolean,
    onTutup: () -> Unit,
    geserMasuk: Boolean = true,
    scrimMasuk: Boolean = true,
    isi: @Composable ColumnScope.() -> Unit,
) {
    val keadaan = remember { MutableTransitionState(tampil) }
    keadaan.targetState = tampil
    BackHandler(enabled = tampil, onBack = onTutup)

    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(keadaan, enter = if (scrimMasuk) fadeIn(tween(DURASI_SHEET_MS, easing = EaseOutFigCraft)) else EnterTransition.None, exit = ExitTransition.None) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(NotoWarna.TeksUtama.copy(alpha = 0.4f))
                    .clickable(interactionSource = null, indication = null, onClick = onTutup),
            )
        }
        AnimatedVisibility(
            keadaan,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = if (geserMasuk) slideInVertically(tween(DURASI_SHEET_MS, easing = EaseOutFigCraft)) { it } else EnterTransition.None,
            exit = ExitTransition.None,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(NotoWarna.Putih, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    // Menahan ketukan supaya tidak tembus ke scrim.
                    .clickable(interactionSource = null, indication = null, onClick = {})
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 34.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = isi,
            )
        }
    }
}
