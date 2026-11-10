package id.noto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// Komponen "Badge" di FigCraft (node 1327): pill 9999, padding 2/8, teks NOTO/Kecil Label.

enum class TipeBadge(val latar: Color, val teks: Color) {
    Info(NotoWarna.InfoLatar, NotoWarna.Info),
    Netral(NotoWarna.Garis, NotoWarna.TeksSekunder),
    Aktif(NotoWarna.PrimerMuda, NotoWarna.Putih),
    Peringatan(NotoWarna.Peringatan, NotoWarna.TeksUtama),
}

@Composable
fun NotoBadge(teks: String, tipe: TipeBadge, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(tipe.latar, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(teks, style = NotoTeks.KecilLabel, color = tipe.teks, maxLines = 1)
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Badge")
@Composable
private fun PreviewBadge() {
    NOTOAPPTheme {
        androidx.compose.foundation.layout.Column {
            NotoBadge("Bebas lagi 21.00", TipeBadge.Info)
            NotoBadge("Bebas sampai 07.00", TipeBadge.Netral)
            NotoBadge("Berlaku s.d. 21.00", TipeBadge.Aktif)
            NotoBadge("Sisa 5m", TipeBadge.Peringatan)
        }
    }
}
