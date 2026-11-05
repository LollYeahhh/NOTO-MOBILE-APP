package id.noto.app.ui.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.noto.app.ui.blocks.BlokirScreen
import id.noto.app.ui.home.BerandaScreen
import id.noto.app.ui.settings.SetelanScreen
import id.noto.app.ui.stats.StatistikScreen

/** Durasi animasi geser pill nav saat pindah tab (smart animate 400 ms di FigCraft). */
const val DURASI_GESER_MS = 400

// SEMENTARA: FigCraft hanya menyimpan nama "easeOut" tanpa angka kurva. Nilai ini kurva CSS
// ease-out standar dan akan diganti dengan angka yang dicek pemilik proyek di FigCraft.
val EaseOutFigCraft = CubicBezierEasing(0f, 0f, 0.58f, 1f)

/**
 * Varian nav per LAYAR. Untuk 2A tiap tab baru punya satu layar kosong:
 * Beranda & Statistik memakai nav lama, Blokir & Setelan nav komponen.
 * Di Tahap 3 ini diganti per layar (mis. Blokir - Kosong memakai nav lama, Blokir - Daftar komponen).
 */
fun varianNavAwal(tab: Tab): VarianNav = when (tab) {
    Tab.Beranda, Tab.Statistik -> VarianNav.Lama
    Tab.Blokir, Tab.Setelan -> VarianNav.Komponen
}

@Composable
fun NotoNavGraph() {
    val navController = rememberNavController()

    // Tab aktif dibaca dari layar yang sedang tampil, jadi ketuk tab maupun tombol back
    // sama-sama membuat pill bergeser ke tab yang benar.
    val entri by navController.currentBackStackEntryAsState()
    val tabAktif = Tab.dariRute(entri?.destination?.route) ?: Tab.Beranda

    fun pilihTab(tujuan: Tab) {
        if (tujuan == tabAktif) return
        navController.navigate(tujuan.rute) {
            // Tab selalu satu tingkat di atas Beranda, jadi back dari tab lain kembali ke Beranda.
            popUpTo(Tab.Beranda.rute) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Isi layar memudar selama pill bergeser (layer yang tidak cocok di smart animate memudar).
    val pudar = tween<Float>(DURASI_GESER_MS, easing = EaseOutFigCraft)

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Tab.Beranda.rute,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(pudar) },
            exitTransition = { fadeOut(pudar) },
            popEnterTransition = { fadeIn(pudar) },
            popExitTransition = { fadeOut(pudar) },
        ) {
            composable(Tab.Beranda.rute) { BerandaScreen() }
            composable(Tab.Blokir.rute) { BlokirScreen() }
            composable(Tab.Statistik.rute) { StatistikScreen() }
            composable(Tab.Setelan.rute) { SetelanScreen() }
        }

        NotoBottomBar(
            aktif = tabAktif,
            varian = varianNavAwal(tabAktif),
            onPilih = ::pilihTab,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
