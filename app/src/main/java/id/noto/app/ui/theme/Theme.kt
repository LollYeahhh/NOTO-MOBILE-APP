package id.noto.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Hanya tema terang. Mode gelap dan "dynamic color" (warna ikut wallpaper Android 12+)
// sengaja tidak dipakai, karena tampilan harus persis seperti desain FigCraft.
private val NotoColorScheme = lightColorScheme(
    primary = NotoWarna.Primer,
    onPrimary = NotoWarna.Putih,
    secondary = NotoWarna.PrimerMuda,
    onSecondary = NotoWarna.Putih,
    background = NotoWarna.Putih,
    onBackground = NotoWarna.TeksUtama,
    surface = NotoWarna.Putih,
    onSurface = NotoWarna.TeksUtama,
    surfaceVariant = NotoWarna.Permukaan,
    onSurfaceVariant = NotoWarna.TeksSekunder,
    outline = NotoWarna.Garis,
)

@Composable
fun NOTOAPPTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NotoColorScheme,
        typography = Typography,
        content = content
    )
}
