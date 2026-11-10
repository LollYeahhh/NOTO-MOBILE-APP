package id.noto.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// Bottom Nav dari FigCraft: kotak 393×114 berlatar Permukaan, baris "nav" 393×50 di y10,
// dan 34 dp terbawah untuk ruang navigation bar Android. Semua angka di bawah diambil dari
// komponen "nav" (node 1043), dalam dp dan relatif terhadap baris nav 393×50.

/**
 * Empat tab di Bottom Nav. Angka posisi: pusat ikon (x, y saat aktif, y saat pasif),
 * ukuran gambar hasil ekspor FigCraft, dan pusat label (y saat aktif, y saat tersembunyi).
 * Gambar ekspor punya ruang kosong di sekeliling ikon, jadi ikon diletakkan dari titik pusatnya.
 */
enum class Tab(
    val rute: String,
    @StringRes val label: Int,
    @DrawableRes val ikon: Int,
    val lebarIkon: Dp,
    val tinggiIkon: Dp,
    val pusatX: Dp,
    val pusatYAktif: Dp,
    val pusatYPasif: Dp,
    val pusatLabelAktif: Dp,
    val pusatLabelPasif: Dp,
    val xPill: Dp,
) {
    Beranda("beranda", R.string.nav_beranda, R.drawable.ic_nav_beranda,
        39.dp, 39.dp, 49.13.dp, 17.dp, 25.dp, 38.5.dp, 46.5.dp, 11.dp),
    Blokir("blokir", R.string.nav_blokir, R.drawable.ic_nav_blokir,
        32.026.dp, 32.026.dp, 147.375.dp, 17.445.dp, 24.995.dp, 37.945.dp, 47.5.dp, 109.dp),
    Statistik("statistik", R.string.nav_statistik, R.drawable.ic_nav_statistik,
        39.26.dp, 28.922.dp, 245.63.dp, 15.46.dp, 25.dp, 34.96.dp, 46.5.dp, 208.dp),
    Setelan("setelan", R.string.nav_setelan, R.drawable.ic_nav_setelan,
        24.842.dp, 24.943.dp, 343.87.dp, 17.47.dp, 25.dp, 38.97.dp, 46.5.dp, 306.dp);

    val xKolom: Dp get() = LEBAR_KOLOM * ordinal

    companion object {
        val LEBAR_KOLOM = 98.25.dp
        // Rute di dalam tab (misalnya "blokir/detail") tetap menyalakan tab induknya.
        fun dariRute(rute: String?): Tab? =
            entries.firstOrNull { rute == it.rute || rute?.startsWith(it.rute + "/") == true }
    }
}

/**
 * Setiap pindah tab, pill bergeser ke tab tujuan, ikon naik/turun, dan label memudar
 * (atas permintaan pemilik proyek: semua perpindahan tab memakai animasi geser ini).
 */
@Composable
fun NotoBottomBar(
    aktif: Tab,
    onPilih: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val xPill by animateDpAsState(aktif.xPill, tween(DURASI_GESER_MS, easing = EaseOutFigCraft), label = "pill")

    Box(
        modifier
            .fillMaxWidth()
            .height(114.dp)
            .background(NotoWarna.Permukaan),
    ) {
        // shortcut: baris nav selebar 393 dp di tengah; layar < 393 dp akan terpotong, sesuaikan kalau perlu dukung HP sempit.
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .size(393.dp, 50.dp),
        ) {
            // Pill aktif 75×50, radius 12, Primer muda.
            Box(
                Modifier
                    .offset(x = xPill)
                    .size(75.dp, 50.dp)
                    .background(NotoWarna.PrimerMuda, RoundedCornerShape(12.dp)),
            )
            Tab.entries.forEach { tab -> KolomTab(tab, tab == aktif, onPilih) }
        }
    }
}

@Composable
private fun KolomTab(tab: Tab, aktif: Boolean, onPilih: (Tab) -> Unit) {
    val durasi = DURASI_GESER_MS
    val pusatY by animateDpAsState(
        if (aktif) tab.pusatYAktif else tab.pusatYPasif, tween(durasi, easing = EaseOutFigCraft), label = "ikonY",
    )
    val warna by animateColorAsState(
        if (aktif) NotoWarna.Putih else NotoWarna.TeksUtama, tween(durasi, easing = EaseOutFigCraft), label = "warna",
    )
    val pusatLabel by animateDpAsState(
        if (aktif) tab.pusatLabelAktif else tab.pusatLabelPasif, tween(durasi, easing = EaseOutFigCraft), label = "labelY",
    )
    val alphaLabel by animateFloatAsState(
        if (aktif) 1f else 0f, tween(durasi, easing = EaseOutFigCraft), label = "labelAlpha",
    )

    Image(
        painter = painterResource(tab.ikon),
        contentDescription = null,
        colorFilter = ColorFilter.tint(warna),
        modifier = Modifier
            .offset(x = tab.pusatX - tab.lebarIkon / 2, y = pusatY - tab.tinggiIkon / 2)
            .size(tab.lebarIkon, tab.tinggiIkon),
    )

    // Label hanya terlihat di tab aktif: NOTO/Label 12/16, ditengahkan di kotaknya seperti di FigCraft.
    // (Nav lama di FigCraft memakai tinggi baris 99,93%, tetapi posisi teksnya sama.)
    val tinggiBaris = 16.dp
    val label = stringResource(tab.label)
    Text(
        text = label,
        style = NotoTeks.Label,
        color = NotoWarna.Putih,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier
            .offset(x = tab.xKolom, y = pusatLabel - tinggiBaris / 2)
            .width(Tab.LEBAR_KOLOM)
            .height(tinggiBaris)
            .alpha(alphaLabel),
    )

    // Area ketuk satu kolom penuh 98,25×50. Desain tidak punya efek tekan, jadi ripple dimatikan.
    Box(
        Modifier
            .offset(x = tab.xKolom)
            .size(Tab.LEBAR_KOLOM, 50.dp)
            .semantics {
                contentDescription = label
                selected = aktif
            }
            .clickable(interactionSource = null, indication = null, role = Role.Tab) { onPilih(tab) },
    )
}

// Preview: satu per tab.
@Composable
private fun PreviewNav(tab: Tab) {
    NOTOAPPTheme { NotoBottomBar(tab, onPilih = {}) }
}

@Preview(name = "Nav - Beranda (lama)", widthDp = 393)
@Composable
private fun PreviewNavBeranda() = PreviewNav(Tab.Beranda)

@Preview(name = "Nav - Blokir (komponen)", widthDp = 393)
@Composable
private fun PreviewNavBlokir() = PreviewNav(Tab.Blokir)

@Preview(name = "Nav - Statistik (lama)", widthDp = 393)
@Composable
private fun PreviewNavStatistik() = PreviewNav(Tab.Statistik)

@Preview(name = "Nav - Setelan (komponen)", widthDp = 393)
@Composable
private fun PreviewNavSetelan() = PreviewNav(Tab.Setelan)
