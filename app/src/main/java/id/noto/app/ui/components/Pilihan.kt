package id.noto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna

// Toggle (node 1304) dan Checkbox (node 1308). Desain tidak punya animasi perpindahan,
// jadi keadaan berganti langsung. Ripple dimatikan karena desain tidak punya efek tekan.

/**
 * Toggle 44×24: Nyala = Primer, kenop di kanan; Mati = sage pucet, kenop di kiri. Kenop putih 20, jarak tepi 2.
 * [terkunci]: sage pucet dan tidak bisa diketuk (Detail Aturan saat edit dikunci, frame 9521).
 */
@Composable
fun NotoToggle(nyala: Boolean, onUbah: (Boolean) -> Unit, modifier: Modifier = Modifier, terkunci: Boolean = false) {
    Box(
        modifier
            .size(44.dp, 24.dp)
            .background(if (nyala && !terkunci) NotoWarna.Primer else NotoWarna.Pasif, CircleShape)
            .toggleable(nyala, enabled = !terkunci, interactionSource = null, indication = null, role = Role.Switch, onValueChange = onUbah)
            .padding(2.dp),
        contentAlignment = if (nyala) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(Modifier.size(20.dp).background(NotoWarna.Putih, CircleShape))
    }
}

/** Checkbox bulat 22: Dipilih = Primer + centang putih; Kosong = Putih + garis 1,5 sage pucet (di dalam). */
@Composable
fun NotoCheckbox(dipilih: Boolean, onUbah: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val centang = painterResource(R.drawable.ic_checkbox_centang)
    val dasar = modifier
        .size(22.dp)
        .toggleable(dipilih, interactionSource = null, indication = null, role = Role.Checkbox, onValueChange = onUbah)
    Box(
        if (dipilih) {
            dasar
                .background(NotoWarna.Primer, CircleShape)
                .drawBehind {
                    // Ekspor centang 23×20,5 punya ruang kosong; geser agar garisnya mulai di x6,5 y8 seperti di FigCraft.
                    translate(left = -0.5.dp.toPx(), top = 1.dp.toPx()) {
                        with(centang) { draw(Size(23.dp.toPx(), 20.5.dp.toPx())) }
                    }
                }
        } else {
            dasar
                .background(NotoWarna.Putih, CircleShape)
                .border(1.5.dp, NotoWarna.Pasif, CircleShape)
        },
    )
}

@Preview(name = "Toggle & Checkbox")
@Composable
private fun PreviewPilihan() {
    NOTOAPPTheme {
        Row {
            NotoToggle(nyala = true, onUbah = {})
            NotoToggle(nyala = false, onUbah = {})
            NotoCheckbox(dipilih = true, onUbah = {})
            NotoCheckbox(dipilih = false, onUbah = {})
        }
    }
}
