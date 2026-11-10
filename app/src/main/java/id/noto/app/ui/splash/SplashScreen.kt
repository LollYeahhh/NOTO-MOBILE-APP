package id.noto.app.ui.splash

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Splash dari 7 frame "Splash - ..." di FigCraft (393×852, latar Permukaan).
// Smart animate di FigCraft menggeser/mengubah ukuran/memudarkan layer yang namanya sama.
// Di sini tiap keadaan frame ditulis sebagai satu Keadaan, lalu nilainya dicampur sesuai progres animasi.

/** Posisi dan transparansi layer di satu frame. Semua dp; x/y relatif terhadap layer induknya di FigCraft. */
private data class Keadaan(
    // "O Jam" di dalam "Logo NOTO"
    val jamX: Float, val jamY: Float, val jamW: Float, val jamH: Float,
    // "Putaran Luar" (kotak tempat jarum berputar, sama dengan "Dalam") di dalam "O Jam"
    val putaranX: Float, val putaranY: Float, val putaranW: Float, val putaranH: Float,
    // "Jarum Garis" di dalam kotak putaran
    val jarumX: Float, val jarumY: Float, val jarumW: Float, val jarumH: Float,
    val alphaTitik: Float,
    val oX: Float, val alphaO: Float,
    val nX: Float, val alphaN: Float,
    val tX: Float, val alphaT: Float,
    // "Tagline" di dalam frame
    val taglineY: Float, val alphaTagline: Float,
)

// Frame 1 Jam sampai 2c Putar: jam besar 120, hanya sudut jarum yang berubah.
private val JamBesar = Keadaan(
    jamX = 40f, jamY = -35.4f, jamW = 120.01f, jamH = 119.98f,
    putaranX = 15.59f, putaranY = 15.58f, putaranW = 88.84f, putaranH = 88.82f,
    jarumX = 37.42f, jarumY = 9.42f, jarumW = 14f, jarumH = 42f,
    alphaTitik = 1f,
    oX = 165.81f, alphaO = 0f,
    nX = -16f, alphaN = 0f,
    tX = 119.66f, alphaT = 0f,
    taglineY = 468.7f, alphaTagline = 0f,
)

// Frame 3 Mengecil: jam 50 di tengah, titik tengah hilang.
private val Mengecil = JamBesar.copy(
    jamX = 74.9f, jamY = 0f, jamW = 50.19f, jamH = 50.18f,
    putaranX = 6.52f, putaranY = 6.51f, putaranW = 37.15f, putaranH = 37.15f,
    jarumX = 15.78f, jarumY = 1.75f, jarumW = 5.67f, jarumH = 20.59f,
    alphaTitik = 0f,
)

// Frame 4 Wordmark: O-jam ke posisi O pertama, N/T/O masuk 16 dp dari samping.
private val Wordmark = Mengecil.copy(
    jamX = 49.83f,
    oX = 149.81f, alphaO = 1f,
    nX = 0f, alphaN = 1f,
    tX = 103.66f, alphaT = 1f,
)

// Frame 5 Tagline: tagline naik 8 dp sambil muncul.
private val Tagline = Wordmark.copy(taglineY = 460.7f, alphaTagline = 1f)

private fun antara(a: Float, b: Float, p: Float) = a + (b - a) * p

/** Nilai di antara dua frame; p = 0 berarti [a], p = 1 berarti [b]. */
private fun campur(a: Keadaan, b: Keadaan, p: Float) = Keadaan(
    antara(a.jamX, b.jamX, p), antara(a.jamY, b.jamY, p), antara(a.jamW, b.jamW, p), antara(a.jamH, b.jamH, p),
    antara(a.putaranX, b.putaranX, p), antara(a.putaranY, b.putaranY, p),
    antara(a.putaranW, b.putaranW, p), antara(a.putaranH, b.putaranH, p),
    antara(a.jarumX, b.jarumX, p), antara(a.jarumY, b.jarumY, p), antara(a.jarumW, b.jarumW, p), antara(a.jarumH, b.jarumH, p),
    antara(a.alphaTitik, b.alphaTitik, p),
    antara(a.oX, b.oX, p), antara(a.alphaO, b.alphaO, p),
    antara(a.nX, b.nX, p), antara(a.alphaN, b.alphaN, p),
    antara(a.tX, b.tX, p), antara(a.alphaT, b.alphaT, p),
    antara(a.taglineY, b.taglineY, p), antara(a.alphaTagline, b.alphaTagline, p),
)

// Easing dari prototype FigCraft.
private val MulaiPelan = CubicBezierEasing(0.4f, 0f, 0.6f, 0.6f)
private val Melambat = CubicBezierEasing(0.4f, 0.4f, 0.6f, 1f)

// Custom spring di FigCraft (mass 1, stiffness 100, damping 15). Compose memakai rasio redaman:
// damping / (2 * akar(stiffness * mass)) = 15 / 20 = 0,75.
private val Pegas = spring<Float>(dampingRatio = 0.75f, stiffness = 100f)

/** Lama dissolve dari splash ke Beranda (FigCraft: dissolve 600 ms, easeInOut). */
const val DURASI_PUDAR_SPLASH_MS = 600

/**
 * Splash animasi. Total ±5,55 detik; [onSelesai] dipanggil saat dissolve ke Beranda harus dimulai.
 */
@Composable
fun SplashScreen(onSelesai: () -> Unit, modifier: Modifier = Modifier, mulaiAnimasi: Boolean = true) {
    val selesai by rememberUpdatedState(onSelesai)

    // Sudut jarum dalam derajat searah jarum jam (Compose). FigCraft: −55° → −156,67° → 101,67° → 0°,
    // yaitu 305° searah jarum jam, jadi di sini 55° → 156,67° → 258,33° → 360°.
    val sudut = remember { Animatable(55f) }
    val keMengecil = remember { Animatable(0f) }
    val keWordmark = remember { Animatable(0f) }
    val keTagline = remember { Animatable(0f) }

    LaunchedEffect(mulaiAnimasi) {
        if (!mulaiAnimasi) return@LaunchedEffect
        // Tiap langkah: tahan (after delay), lalu smart animate selama durasinya.
        delay(300); sudut.animateTo(156.67f, tween(500, easing = MulaiPelan))      // 1 → 2a
        delay(1); sudut.animateTo(258.33f, tween(500, easing = LinearEasing))      // 2a → 2b
        delay(1); sudut.animateTo(360f, tween(500, easing = Melambat))             // 2b → 2c
        // Pegas dijalankan tanpa ditunggu; langkah berikutnya mulai tepat setelah durasi di FigCraft.
        delay(150); launch { keMengecil.animateTo(1f, Pegas) }; delay(600)         // 2c → 3
        delay(100); launch { keWordmark.animateTo(1f, Pegas) }; delay(700)         // 3 → 4
        delay(200); launch { keTagline.animateTo(1f, Pegas) }; delay(600)          // 4 → 5
        delay(700); selesai()                                                     // tahan, lalu ke Beranda
    }

    val k = campur(campur(campur(JamBesar, Mengecil, keMengecil.value), Wordmark, keWordmark.value), Tagline, keTagline.value)
    // Pegas bisa sedikit melewati 1, jadi transparansi dijaga tetap 0..1.
    fun a(nilai: Float) = nilai.coerceIn(0f, 1f)

    val deskripsi = stringResource(R.string.logo_noto) + ", " + stringResource(R.string.tagline_noto)
    Box(
        modifier
            .fillMaxSize()
            .background(NotoWarna.Permukaan)
            .semantics { contentDescription = deskripsi },
    ) {
        // Frame 393×852 diletakkan di tengah layar, jadi jarak logo ke titik tengah layar sama dengan di desain.
        Box(Modifier.align(Alignment.Center).requiredSize(393.dp, 852.dp)) {
            // Layer "Logo NOTO" (200×50,18) ada di x96,5 y375,5. Isinya diletakkan langsung di frame
            // (posisi logo + posisi layer), karena jam 120 lebih tinggi dari kotak logo.
            val lx = 96.5f
            val ly = 375.5f
            Lapisan(R.drawable.ic_splash_n, lx + k.nX, ly + 0.6f, 39.57f, 48.93f, a(k.alphaN))
            Lapisan(R.drawable.ic_splash_t, lx + k.tX, ly + 0.58f, 39.87f, 48.98f, a(k.alphaT))

            // "O" biasa (O kedua di wordmark): lingkar 50 dengan bagian dalam Permukaan.
            Box(Modifier.offset((lx + k.oX).dp, ly.dp).size(50.19.dp, 50.18.dp).alpha(a(k.alphaO))) {
                Lapisan(R.drawable.ic_splash_lingkar, 0f, 0f, 50.19f, 50.18f)
                Lapisan(R.drawable.ic_splash_dalam, 6.52f, 6.52f, 37.15f, 37.15f)
            }

            // "O Jam": lingkar, bagian dalam, jarum yang berputar, dan titik tengah.
            Box(Modifier.offset((lx + k.jamX).dp, (ly + k.jamY).dp).size(k.jamW.dp, k.jamH.dp)) {
                Lapisan(R.drawable.ic_splash_lingkar, 0f, 0f, k.jamW, k.jamH)
                Lapisan(R.drawable.ic_splash_dalam, k.putaranX, k.putaranY, k.putaranW, k.putaranH)
                Box(
                    Modifier
                        .offset(k.putaranX.dp, k.putaranY.dp)
                        .size(k.putaranW.dp, k.putaranH.dp)
                        .graphicsLayer { rotationZ = sudut.value },
                ) {
                    // Bentuk jarum besar dan kecil sedikit berbeda di FigCraft: berganti pelan selama jam mengecil.
                    val pKecil = a(keMengecil.value)
                    Lapisan(R.drawable.ic_splash_jarum, k.jarumX, k.jarumY, k.jarumW, k.jarumH, 1f - pKecil)
                    Lapisan(R.drawable.ic_splash_jarum_kecil, k.jarumX, k.jarumY, k.jarumW, k.jarumH, pKecil)
                }
                // "Titik Tengah": 1/8 ukuran jam, tepat di tengah.
                val titik = k.jamW * 0.125f
                Lapisan(R.drawable.ic_splash_titik, (k.jamW - titik) / 2, (k.jamH - titik) / 2, titik, titik, a(k.alphaTitik))
            }

            // "Tagline" 291,3×15,38 di x50,85
            Lapisan(R.drawable.ic_splash_tagline, 50.85f, k.taglineY, 291.3f, 15.38f, a(k.alphaTagline))
        }
    }
}

/** Satu gambar vektor dari FigCraft di posisi (x, y) dengan ukuran w×h, dalam dp. */
@Composable
private fun Lapisan(@DrawableRes gambar: Int, x: Float, y: Float, w: Float, h: Float, alpha: Float = 1f) {
    Image(
        painter = painterResource(gambar),
        contentDescription = null,
        // FillBounds: jarum diregangkan bebas saat mengecil, seperti smart animate.
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.offset(x.dp, y.dp).size(w.dp, h.dp).alpha(alpha),
    )
}

// Preview menampilkan frame 1 (animasi tidak berjalan di Preview statis).
@Preview(name = "Splash - 1 Jam", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSplash() {
    NOTOAPPTheme { SplashScreen(onSelesai = {}, mulaiAnimasi = false) }
}
