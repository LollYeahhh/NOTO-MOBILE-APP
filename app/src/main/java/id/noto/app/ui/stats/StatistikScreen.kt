package id.noto.app.ui.stats

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.ui.components.NotoHeaderLogo
import id.noto.app.ui.components.NotoTabPeriode
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna

// Tinggi Bottom Nav (114) yang menutupi bagian bawah layar.
private val TINGGI_NAV = 114.dp

/** Prototype: ketuk Tab Periode = dissolve 150 ms easeOut ke frame periode lain. */
private const val DURASI_GANTI_PERIODE_MS = 150

/**
 * Tab Statistik: Header diam, lalu Tab Periode (y136) dan isi periode yang bisa digeser.
 * Header, Tab Periode dan Nav sama di ketiga frame, jadi hanya isinya yang memudar saat periode diganti.
 */
@Composable
fun StatistikScreen(
    periodeAwal: Int = 0,
    hariIni: StatistikHariIni = contohHariIni,
    bulanan: StatistikBulanan = contohBulanan,
    onDistribusi: () -> Unit = {},
    onAplikasi: (AplikasiStatistik) -> Unit = {},
) {
    var periode by rememberSaveable { mutableIntStateOf(periodeAwal) }
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderLogo()
        Column(Modifier.verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = TINGGI_NAV + 24.dp)) {
            NotoTabPeriode(periode, { periode = it }, Modifier.padding(horizontal = 16.dp))
            Crossfade(periode, animationSpec = tween(DURASI_GANTI_PERIODE_MS, easing = EaseOut), label = "periode") {
                when (it) {
                    0 -> ScreenTimeIsi(hariIni) { a -> onAplikasi(AplikasiStatistik(a.logo, a.nama, a.menit, a.ikon, a.paket)) }
                    1 -> MingguIniIsi(contohMingguan, onDistribusi) { a -> onAplikasi(AplikasiStatistik(a.logo, a.nama, a.menit)) }
                    // Angka di Detail Aplikasi diambil dari pemakaian hari ini (NavGraph), bukan total sebulan ini.
                    else -> BulanIniIsi(bulanan) { a -> onAplikasi(AplikasiStatistik(a.logo, a.nama, a.menit, a.ikon, a.paket)) }
                }
            }
        }
    }
}

@Preview(name = "Statistik - Screen Time", widthDp = 393, heightDp = 882)
@Composable
private fun PreviewStatistik() {
    NOTOAPPTheme { StatistikScreen() }
}
