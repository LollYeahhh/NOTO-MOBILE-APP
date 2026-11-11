package id.noto.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import id.noto.app.R

/**
 * Gambar logo aplikasi: [logoContoh] (drawable data contoh, hanya Preview), lalu [ikon] dari sistem,
 * lalu komponen "Logo / Cadangan" bila ikon sistem gagal dimuat.
 */
@Composable
fun painterAplikasi(ikon: ImageBitmap?, logoContoh: Int? = null): Painter = when {
    logoContoh != null -> painterResource(logoContoh)
    ikon != null -> remember(ikon) { BitmapPainter(ikon) }
    else -> painterResource(R.drawable.ic_aplikasi_cadangan)
}
