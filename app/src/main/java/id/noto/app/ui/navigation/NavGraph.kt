package id.noto.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.noto.app.R
import id.noto.app.ui.blocks.BlokirScreen
import id.noto.app.ui.blocks.DetailAturanScreen
import id.noto.app.ui.blocks.FormAturan
import id.noto.app.ui.blocks.FormAturanScreen
import id.noto.app.ui.blocks.ModeForm
import id.noto.app.ui.blocks.PilihAplikasiScreen
import id.noto.app.ui.blocks.contohAturanDaftar
import id.noto.app.ui.blocks.contohDetailSosmed
import id.noto.app.ui.blocks.contohFormEdit
import id.noto.app.ui.blocks.namaOtomatis
import id.noto.app.ui.home.contohAturanBeranda
import id.noto.app.ui.home.BerandaScreen
import id.noto.app.ui.settings.SetelanScreen
import id.noto.app.ui.splash.DURASI_PUDAR_SPLASH_MS
import id.noto.app.ui.splash.SplashScreen
import id.noto.app.ui.stats.StatistikScreen

/** Durasi animasi geser pill nav saat pindah tab (smart animate 400 ms di FigCraft). */
const val DURASI_GESER_MS = 400

// FigCraft hanya menyimpan nama "easeOut" tanpa angka kurva. Disepakati pemilik proyek:
// pakai kurva ease-out standar (0, 0, 0.58, 1).
val EaseOutFigCraft = CubicBezierEasing(0f, 0f, 0.58f, 1f)

// "easeInOut" FigCraft (dissolve splash ke Beranda). Disepakati: kurva standar (0.42, 0, 0.58, 1).
val EaseInOutFigCraft = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

// Navigasi dua tingkat:
// - luar: splash, "utama" (cangkang: tab + Bottom Nav), lalu layar penuh tanpa nav (Tambah, Edit).
//   Layar penuh menutupi seluruh cangkang termasuk nav, seperti di FigCraft.
// - dalam (di cangkang): empat tab dan Detail Aturan (nav tetap tampil, tab Blokir aktif).
private const val RUTE_SPLASH = "splash"
private const val RUTE_UTAMA = "utama"
private const val RUTE_TAMBAH = "tambah"
private const val RUTE_EDIT = "edit"
private const val RUTE_PILIH = "pilih"
private const val RUTE_DETAIL = "blokir/detail"

/** Push (Daftar → Detail, Detail → Edit) dan move in (+ → Tambah Aturan): 300 ms easeOut. */
private const val DURASI_PUSH_MS = 300

/** Dissolve Tambah Aturan Terisi → Blokir - Daftar: 250 ms easeOut. */
private const val DURASI_DISSOLVE_MS = 250

@Composable
fun NotoNavGraph() {
    val navController = rememberNavController()
    // shortcut: isian form disimpan di sini selama Tahap 3; diganti ViewModel + Room di Tahap 4.
    var form by remember { mutableStateOf(FormAturan()) }
    // shortcut: nama aturan Sosmed (satu-satunya yang bisa diedit) disimpan di memori saja, hilang saat
    // aplikasi ditutup; diganti Room di Tahap 4.
    var namaSosmed by remember { mutableStateOf(contohFormEdit.nama) }
    // Nama aturan yang sudah ada, supaya nama otomatis tidak kembar (sementara dari data contoh).
    val namaAturan = contohAturanDaftar.map { if (it.judul == contohFormEdit.nama) namaSosmed else it.judul }
    val namaDasar = stringResource(R.string.nama_otomatis)
    val polaNomor = stringResource(R.string.nama_otomatis_nomor)
    // Pilih Aplikasi ditutup lewat tombol "Pilih N Aplikasi" (dissolve) atau kembali (instant).
    var pilihLewatTombol by remember { mutableStateOf(false) }

    // Dissolve splash → Beranda: cangkang (Beranda beserta nav) muncul memudar di atas splash.
    val pudarSplash = tween<Float>(DURASI_PUDAR_SPLASH_MS, easing = EaseInOutFigCraft)
    val gerak = tween<IntOffset>(DURASI_PUSH_MS, easing = EaseOutFigCraft)
    // Layar lama tetap terlihat penuh di bawah layar baru selama animasi (move in).
    val diamDiBawah = fadeOut(tween(DURASI_PUSH_MS), targetAlpha = 1f)

    NavHost(
        navController = navController,
        startDestination = RUTE_SPLASH,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        // Splash tetap terlihat penuh di bawah Beranda selama dissolve, lalu dibuang dari riwayat.
        composable(RUTE_SPLASH, exitTransition = { fadeOut(pudarSplash, targetAlpha = 1f) }) {
            SplashScreen(onSelesai = {
                navController.navigate(RUTE_UTAMA) { popUpTo(RUTE_SPLASH) { inclusive = true } }
            })
        }
        composable(
            RUTE_UTAMA,
            enterTransition = { fadeIn(pudarSplash) },
            // Ke Edit: push, seluruh cangkang (termasuk nav) bergeser ke kiri.
            // Ke Tambah: move in, cangkang diam di bawah.
            exitTransition = { if (targetState.destination.route == RUTE_EDIT) slideOutHorizontally(gerak) { -it } else diamDiBawah },
        ) {
            CangkangTab(
                onTambah = { form = FormAturan(); navController.navigate(RUTE_TAMBAH) },
                onEdit = { form = contohFormEdit.copy(nama = namaSosmed); navController.navigate(RUTE_EDIT) },
                namaSosmed = namaSosmed,
            )
        }
        composable(
            RUTE_TAMBAH,
            // moveIn arah "top": naik dari bawah, seperti sheet.
            enterTransition = { slideInVertically(gerak) { it } },
            // Ke Pilih Aplikasi: push, Tambah bergeser ke kiri.
            exitTransition = { if (targetState.destination.route == RUTE_PILIH) slideOutHorizontally(gerak) { -it } else ExitTransition.None },
            // X saat form kosong = back instant; saat terisi, X dan Simpan = dissolve 250 ms ke Daftar.
            popExitTransition = {
                if (form.lengkap) fadeOut(tween(DURASI_DISSOLVE_MS, easing = EaseOutFigCraft)) else ExitTransition.None
            },
        ) {
            FormAturanScreen(
                mode = ModeForm.Tambah,
                form = form,
                onUbah = { form = it },
                onTutup = { navController.popBackStack() },
                // Belum disimpan ke mana pun sampai Tahap 4.
                onSimpan = { navController.popBackStack() },
                onPilihAplikasi = { pilihLewatTombol = false; navController.navigate(RUTE_PILIH) },
                namaTerpakai = namaAturan,
            )
        }
        composable(
            RUTE_PILIH,
            enterTransition = { slideInHorizontally(gerak) { it } },
            popExitTransition = {
                if (pilihLewatTombol) fadeOut(tween(DURASI_DISSOLVE_MS, easing = EaseOutFigCraft)) else ExitTransition.None
            },
        ) {
            // shortcut: FigCraft membawa tombol ini ke "Tambah Aturan Terisi"; di sini kembali ke Tambah
            // dengan aplikasi terisi (disepakati 4a).
            PilihAplikasiScreen(
                awal = form.aplikasi,
                onKembali = { navController.popBackStack() },
                onPilih = {
                    form = form.copy(aplikasi = it)
                    pilihLewatTombol = true
                    navController.popBackStack()
                },
            )
        }
        composable(RUTE_EDIT, enterTransition = { slideInHorizontally(gerak) { it } }) {
            // Kembali dan "Simpan Perubahan": back instant.
            FormAturanScreen(
                mode = ModeForm.Edit,
                form = form,
                onUbah = { form = it },
                onTutup = { navController.popBackStack() },
                onSimpan = {
                    // Nama kosong → nama otomatis (Blokir, Blokir 1, …).
                    namaSosmed = form.nama.trim().ifBlank { namaOtomatis(namaDasar, polaNomor, namaAturan - namaSosmed) }
                    navController.popBackStack()
                },
                // Nama aturan yang sedang diedit tidak dihitung terpakai.
                namaTerpakai = namaAturan - namaSosmed,
            )
        }
    }
}

/** Cangkang: empat tab dan Detail Aturan dengan Bottom Nav di bawahnya. */
@Composable
private fun CangkangTab(onTambah: () -> Unit, onEdit: () -> Unit, namaSosmed: String) {
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
    // Push: layar baru masuk dari kanan, layar lama keluar ke kiri.
    val push = tween<IntOffset>(DURASI_PUSH_MS, easing = EaseOutFigCraft)
    val ruteAsalDetail = setOf(Tab.Blokir.rute, Tab.Beranda.rute)

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
            // Kartu di Beranda membuka Detail Aturan dengan push, seperti FigCraft.
            composable(
                Tab.Beranda.rute,
                exitTransition = { if (targetState.destination.route == RUTE_DETAIL) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                popEnterTransition = { if (initialState.destination.route == RUTE_DETAIL) EnterTransition.None else fadeIn(pudar) },
            ) {
                BerandaScreen(
                    aturan = contohAturanBeranda.map { if (it.aktif) it.copy(nama = namaSosmed) else it },
                    onBukaDetail = { navController.navigate(RUTE_DETAIL) },
                    onTambah = onTambah,
                )
            }
            composable(
                Tab.Blokir.rute,
                exitTransition = { if (targetState.destination.route == RUTE_DETAIL) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                // Kembali dari Detail: instant, seperti aksi "back" di FigCraft.
                popEnterTransition = { if (initialState.destination.route == RUTE_DETAIL) EnterTransition.None else fadeIn(pudar) },
            ) {
                BlokirScreen(
                    aturan = contohAturanDaftar.map { if (it.judul == contohDetailSosmed.nama) it.copy(judul = namaSosmed) else it },
                    onBukaDetail = { navController.navigate(RUTE_DETAIL) },
                    onTambah = onTambah,
                )
            }
            composable(
                RUTE_DETAIL,
                // Dari Daftar atau Beranda: push. Kembali: instant, seperti aksi "back" di FigCraft.
                enterTransition = { if (initialState.destination.route in ruteAsalDetail) slideInHorizontally(push) { it } else fadeIn(pudar) },
                popExitTransition = { if (targetState.destination.route in ruteAsalDetail) ExitTransition.None else fadeOut(pudar) },
            ) {
                DetailAturanScreen(onKembali = { navController.popBackStack() }, onEdit = onEdit, detail = contohDetailSosmed.copy(nama = namaSosmed))
            }
            composable(Tab.Statistik.rute) { StatistikScreen() }
            composable(Tab.Setelan.rute) { SetelanScreen() }
        }
        NotoBottomBar(aktif = tabAktif, onPilih = ::pilihTab, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
