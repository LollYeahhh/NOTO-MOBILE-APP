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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.withResumed
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
import id.noto.app.ui.blocks.SheetForm
import id.noto.app.ui.blocks.SheetHapus
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.blocks.contohAturanDaftar
import id.noto.app.ui.blocks.contohDetailSosmed
import id.noto.app.ui.blocks.contohFormEdit
import id.noto.app.ui.blocks.namaOtomatis
import id.noto.app.ui.home.contohAturanBeranda
import id.noto.app.ui.home.BerandaScreen
import id.noto.app.ui.settings.AturanPembatasanScreen
import id.noto.app.ui.settings.LapisanSheetSetelan
import id.noto.app.ui.settings.NotifikasiScreen
import id.noto.app.ui.settings.SetelanScreen
import id.noto.app.ui.settings.SheetSetelan
import id.noto.app.ui.settings.CatatanRilisScreen
import id.noto.app.ui.settings.FaqScreen
import id.noto.app.ui.settings.LisensiScreen
import id.noto.app.ui.settings.TeksLisensiScreen
import id.noto.app.ui.settings.TentangScreen
import id.noto.app.ui.settings.daftarLisensi
import id.noto.app.ui.settings.SetelanViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import id.noto.app.ui.splash.DURASI_PUDAR_SPLASH_MS
import id.noto.app.ui.splash.SplashScreen
import id.noto.app.ui.stats.AplikasiStatistik
import id.noto.app.ui.stats.DetailAplikasiScreen
import id.noto.app.ui.stats.DetailDistribusiScreen
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
private const val RUTE_SETELAN_NOTIF = "setelan/notifikasi"
private const val RUTE_SETELAN_ATURAN = "setelan/aturan"
private const val RUTE_SETELAN_TENTANG = "setelan/tentang"
private const val RUTE_SETELAN_CATATAN = "setelan/catatan-rilis"
private const val RUTE_SETELAN_LISENSI = "setelan/lisensi"
private const val RUTE_SETELAN_FAQ = "setelan/faq"
/** Teks lisensi ke-i dari [daftarLisensi]. */
private fun ruteTeksLisensi(i: Int) = "$RUTE_SETELAN_LISENSI/$i"
private const val RUTE_STAT_DISTRIBUSI = "statistik/distribusi"
private const val RUTE_STAT_APLIKASI = "statistik/aplikasi"
private val RUTE_STAT_DALAM = setOf(RUTE_STAT_DISTRIBUSI, RUTE_STAT_APLIKASI)
private val RUTE_SETELAN_DALAM = setOf(
    RUTE_SETELAN_NOTIF, RUTE_SETELAN_ATURAN, RUTE_SETELAN_TENTANG,
    RUTE_SETELAN_CATATAN, RUTE_SETELAN_LISENSI, RUTE_SETELAN_FAQ,
) + daftarLisensi.indices.map(::ruteTeksLisensi)

/** Ketuk nav Setelan dari halaman dalam Setelan: dissolve 150 ms ke Setelan - Utama (FigCraft). */
private const val DURASI_KE_SETELAN_MS = 150

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
    // shortcut: aturan Sosmed yang dihapus hanya disembunyikan di memori; diganti Room di Tahap 4.
    var sosmedDihapus by remember { mutableStateOf(false) }
    // shortcut: toggle "Aturan aktif" dan "Senyapkan notifikasi" Sosmed di memori saja; diganti Room di Tahap 4.
    var sosmedAktif by remember { mutableStateOf(true) }
    var sosmedSenyap by remember { mutableStateOf(true) }
    // Nama aturan yang sudah ada, supaya nama otomatis tidak kembar (sementara dari data contoh).
    val namaAturan = contohAturanDaftar.map { if (it.judul == contohFormEdit.nama) namaSosmed else it.judul }
    val namaDasar = stringResource(R.string.nama_otomatis)
    val polaNomor = stringResource(R.string.nama_otomatis_nomor)
    // Pilih Aplikasi ditutup lewat tombol "Pilih N Aplikasi" (dissolve) atau kembali (instant).
    var pilihLewatTombol by remember { mutableStateOf(false) }
    // Setelah "Pilih N Aplikasi", Tambah tampil dengan Sheet Atur Jadwal sudah terbuka (FigCraft node 2456).
    var bukaJadwal by remember { mutableStateOf(false) }

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
                sosmedDihapus = sosmedDihapus,
                onHapusSosmed = { sosmedDihapus = true },
                sosmedAktif = sosmedAktif,
                onSosmedAktif = { sosmedAktif = it },
                sosmedSenyap = sosmedSenyap,
                onSosmedSenyap = { sosmedSenyap = it },
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
                onPilihAplikasi = { pilihLewatTombol = false; bukaJadwal = false; navController.navigate(RUTE_PILIH) },
                namaTerpakai = namaAturan,
                sheetAwal = if (bukaJadwal) SheetForm.Jadwal else null,
            )
        }
        composable(
            RUTE_PILIH,
            enterTransition = { slideInHorizontally(gerak) { it } },
            popExitTransition = {
                if (pilihLewatTombol) fadeOut(tween(DURASI_DISSOLVE_MS, easing = EaseOutFigCraft)) else ExitTransition.None
            },
        ) {
            // Tombol "Pilih N Aplikasi": kembali ke Tambah (aplikasi terisi) dengan Sheet Atur Jadwal terbuka.
            PilihAplikasiScreen(
                awal = form.aplikasi,
                onKembali = { navController.popBackStack() },
                onPilih = {
                    form = form.copy(aplikasi = it)
                    pilihLewatTombol = true
                    // Hanya Tambah yang lanjut ke Sheet Atur Jadwal; Edit kembali apa adanya.
                    bukaJadwal = navController.previousBackStackEntry?.destination?.route == RUTE_TAMBAH
                    navController.popBackStack()
                },
            )
        }
        composable(
            RUTE_EDIT,
            enterTransition = { slideInHorizontally(gerak) { it } },
            // Ke Pilih Aplikasi: push, Edit bergeser ke kiri.
            exitTransition = { if (targetState.destination.route == RUTE_PILIH) slideOutHorizontally(gerak) { -it } else ExitTransition.None },
            // "Ya, hapus aturan": dissolve 250 ms ke Blokir - Daftar; kembali dan simpan: instant.
            popExitTransition = {
                if (sosmedDihapus) fadeOut(tween(DURASI_DISSOLVE_MS, easing = EaseOutFigCraft)) else ExitTransition.None
            },
        ) {
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
                onHapus = { sosmedDihapus = true; navController.popBackStack() },
                onPilihAplikasi = { pilihLewatTombol = false; bukaJadwal = false; navController.navigate(RUTE_PILIH) },
                // Nama aturan yang sedang diedit tidak dihitung terpakai.
                namaTerpakai = namaAturan - namaSosmed,
            )
        }
    }
}

/** Cangkang: empat tab dan Detail Aturan dengan Bottom Nav di bawahnya. */
@Composable
private fun CangkangTab(
    onTambah: () -> Unit,
    onEdit: () -> Unit,
    namaSosmed: String,
    sosmedDihapus: Boolean,
    onHapusSosmed: () -> Unit,
    sosmedAktif: Boolean,
    onSosmedAktif: (Boolean) -> Unit,
    sosmedSenyap: Boolean,
    onSosmedSenyap: (Boolean) -> Unit,
) {
    val navController = rememberNavController()
    // Sheet Hapus Aturan dari tombol "Hapus aturan" di Detail.
    var hapusDariDetail by remember { mutableStateOf(false) }
    // shortcut: aplikasi yang dibuka di Detail Aplikasi disimpan di memori, bukan argumen rute; cukup untuk data contoh,
    // diganti nama paket aplikasi sebagai argumen rute di Tahap 8.
    var aplikasiStat by remember { mutableStateOf(AplikasiStatistik(R.drawable.logo_instagram, "Instagram", 70)) }

    // Setelah aturan dihapus dari Edit, Detail-nya tidak ada lagi: pindah ke Blokir - Daftar tanpa animasi.
    // Navigasi ditunda sampai cangkang aktif lagi (Edit selesai memudar): navigate() saat cangkang belum aktif
    // membuat NavHost di dalamnya mengabaikan navigasi berikutnya sampai tombol back ditekan.
    // Selama menunggu, rute Detail sudah menampilkan isi Daftar, jadi tidak terlihat lompatan.
    val siklus = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(sosmedDihapus) {
        if (!sosmedDihapus) return@LaunchedEffect
        siklus.withResumed {
            if (navController.currentDestination?.route != RUTE_DETAIL) return@withResumed
            if (navController.previousBackStackEntry?.destination?.route == Tab.Blokir.rute) {
                navController.popBackStack()
            } else {
                navController.navigate(Tab.Blokir.rute) { popUpTo(Tab.Beranda.rute); launchSingleTop = true }
            }
        }
    }

    // Tab aktif dibaca dari layar yang sedang tampil, jadi ketuk tab maupun tombol back
    // sama-sama membuat pill bergeser ke tab yang benar.
    val entri by navController.currentBackStackEntryAsState()
    val tabAktif = Tab.dariRute(entri?.destination?.route) ?: Tab.Beranda

    // Setelan dibaca dari dan disimpan ke DataStore lewat ViewModel.
    val setelanVm: SetelanViewModel = viewModel()
    val setelan by setelanVm.setelan.collectAsState()
    var sheetSetelan by remember { mutableStateOf<SheetSetelan?>(null) }
    // true saat halaman dalam Setelan ditutup lewat nav Setelan (dissolve), bukan tombol kembali (instant).
    var keSetelanLewatNav by remember { mutableStateOf(false) }

    fun pilihTab(tujuan: Tab) {
        if (tujuan == tabAktif) {
            // Di halaman dalam Setelan, nav Setelan kembali ke Setelan - Utama (FigCraft "navigate" ke node 3031).
            if (tujuan == Tab.Setelan && entri?.destination?.route in RUTE_SETELAN_DALAM) {
                keSetelanLewatNav = true
                navController.popBackStack(Tab.Setelan.rute, inclusive = false)
            }
            // Di Detail Distribusi atau Detail Aplikasi, nav Statistik kembali ke Statistik.
            if (tujuan == Tab.Statistik && entri?.destination?.route in RUTE_STAT_DALAM) {
                navController.popBackStack(Tab.Statistik.rute, inclusive = false)
            }
            return
        }
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
    val ruteAsalDetail = setOf(Tab.Blokir.rute, Tab.Beranda.rute, RUTE_STAT_APLIKASI)

    @Composable
    fun DaftarBlokir() = BlokirScreen(
        aturan = contohAturanDaftar.filterNot { sosmedDihapus && it.judul == contohDetailSosmed.nama }
            .map { if (it.judul == contohDetailSosmed.nama) it.copy(judul = namaSosmed) else it },
        onBukaDetail = { navController.navigate(RUTE_DETAIL) },
        onTambah = onTambah,
    )

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
                    aturan = contohAturanBeranda.filterNot { sosmedDihapus && it.aktif }.map { if (it.aktif) it.copy(nama = namaSosmed) else it },
                    onBukaDetail = { navController.navigate(RUTE_DETAIL) },
                    onTambah = onTambah,
                )
            }
            composable(
                Tab.Blokir.rute,
                // Dari Detail aturan yang baru dihapus: isinya sudah sama, jadi tanpa animasi.
                enterTransition = { if (sosmedDihapus && initialState.destination.route == RUTE_DETAIL) EnterTransition.None else fadeIn(pudar) },
                exitTransition = { if (targetState.destination.route == RUTE_DETAIL) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                // Kembali dari Detail: instant, seperti aksi "back" di FigCraft.
                popEnterTransition = { if (initialState.destination.route == RUTE_DETAIL) EnterTransition.None else fadeIn(pudar) },
            ) {
                DaftarBlokir()
            }
            composable(
                RUTE_DETAIL,
                // Dari Daftar atau Beranda: push. Kembali: instant, seperti aksi "back" di FigCraft.
                enterTransition = { if (initialState.destination.route in ruteAsalDetail) slideInHorizontally(push) { it } else fadeIn(pudar) },
                exitTransition = { if (sosmedDihapus) ExitTransition.None else fadeOut(pudar) },
                popExitTransition = { if (targetState.destination.route in ruteAsalDetail) ExitTransition.None else fadeOut(pudar) },
            ) {
                if (sosmedDihapus) {
                    DaftarBlokir()
                } else {
                    DetailAturanScreen(
                        onKembali = { navController.popBackStack() },
                        onEdit = onEdit,
                        onHapus = { hapusDariDetail = true },
                        detail = contohDetailSosmed.copy(nama = namaSosmed),
                        aktif = sosmedAktif,
                        onAktif = onSosmedAktif,
                        senyap = sosmedSenyap,
                        onSenyap = onSosmedSenyap,
                    )
                }
            }
            composable(
                Tab.Statistik.rute,
                exitTransition = { if (targetState.destination.route in RUTE_STAT_DALAM) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                popEnterTransition = { if (initialState.destination.route in RUTE_STAT_DALAM) EnterTransition.None else fadeIn(pudar) },
            ) {
                StatistikScreen(
                    onDistribusi = { navController.navigate(RUTE_STAT_DISTRIBUSI) },
                    onAplikasi = { aplikasiStat = it; navController.navigate(RUTE_STAT_APLIKASI) },
                )
            }
            // Detail Distribusi dan Detail Aplikasi: masuk dengan push, kembali instant (aksi "back" FigCraft).
            val diStatistik = RUTE_STAT_DALAM + Tab.Statistik.rute
            RUTE_STAT_DALAM.forEach { rute ->
                composable(
                    rute,
                    enterTransition = { if (initialState.destination.route in diStatistik) slideInHorizontally(push) { it } else fadeIn(pudar) },
                    exitTransition = {
                        if (targetState.destination.route in RUTE_STAT_DALAM + RUTE_DETAIL) slideOutHorizontally(push) { -it } else fadeOut(pudar)
                    },
                    popEnterTransition = {
                        if (initialState.destination.route in RUTE_STAT_DALAM + RUTE_DETAIL) EnterTransition.None else fadeIn(pudar)
                    },
                    popExitTransition = { if (targetState.destination.route in diStatistik) ExitTransition.None else fadeOut(pudar) },
                ) {
                    val kembali: () -> Unit = { navController.popBackStack() }
                    if (rute == RUTE_STAT_DISTRIBUSI) {
                        DetailDistribusiScreen(kembali) { a ->
                            aplikasiStat = AplikasiStatistik(a.logo, a.nama, a.menit)
                            navController.navigate(RUTE_STAT_APLIKASI)
                        }
                    } else {
                        // Aturan contoh yang memuat aplikasi ini: hanya Sosmed (selama belum dihapus).
                        val dalamSosmed = !sosmedDihapus && contohDetailSosmed.aplikasi.any { it.nama == aplikasiStat.nama }
                        DetailAplikasiScreen(
                            aplikasiStat,
                            namaAturan = if (dalamSosmed) namaSosmed else null,
                            onKembali = kembali,
                            onAturan = { navController.navigate(RUTE_DETAIL) },
                        )
                    }
                }
            }
            composable(
                Tab.Setelan.rute,
                enterTransition = { keSetelanLewatNav = false; fadeIn(pudar) },
                exitTransition = { if (targetState.destination.route in RUTE_SETELAN_DALAM) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                popEnterTransition = {
                    when {
                        initialState.destination.route !in RUTE_SETELAN_DALAM -> fadeIn(pudar)
                        keSetelanLewatNav -> fadeIn(tween(DURASI_KE_SETELAN_MS, easing = EaseOutFigCraft))
                        else -> EnterTransition.None
                    }
                },
            ) {
                SetelanScreen(
                    onNotifikasi = { navController.navigate(RUTE_SETELAN_NOTIF) },
                    onAturan = { navController.navigate(RUTE_SETELAN_ATURAN) },
                    onTentang = { navController.navigate(RUTE_SETELAN_TENTANG) },
                )
            }
            // Halaman dalam Setelan (termasuk halaman di bawah Tentang Noto): masuk dengan push dari halaman
            // yang lebih dangkal; kembali instant (aksi "back" FigCraft).
            val diSetelan = RUTE_SETELAN_DALAM + Tab.Setelan.rute
            RUTE_SETELAN_DALAM.forEach { rute ->
                composable(
                    rute,
                    enterTransition = { if (initialState.destination.route in diSetelan) slideInHorizontally(push) { it } else fadeIn(pudar) },
                    exitTransition = { if (targetState.destination.route in RUTE_SETELAN_DALAM) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                    popEnterTransition = { if (initialState.destination.route in RUTE_SETELAN_DALAM) EnterTransition.None else fadeIn(pudar) },
                    popExitTransition = {
                        when {
                            targetState.destination.route !in diSetelan -> fadeOut(pudar)
                            keSetelanLewatNav -> fadeOut(tween(DURASI_KE_SETELAN_MS, easing = EaseOutFigCraft))
                            else -> ExitTransition.None
                        }
                    },
                ) {
                    val kembali: () -> Unit = { navController.popBackStack() }
                    val bukaSheet: (SheetSetelan) -> Unit = { sheetSetelan = it }
                    when (rute) {
                        RUTE_SETELAN_NOTIF -> NotifikasiScreen(setelan, setelanVm::ubah, kembali, bukaSheet)
                        RUTE_SETELAN_ATURAN -> AturanPembatasanScreen(setelan, setelanVm::ubah, kembali, bukaSheet)
                        RUTE_SETELAN_TENTANG -> TentangScreen(
                            onKembali = kembali,
                            onCatatanRilis = { navController.navigate(RUTE_SETELAN_CATATAN) },
                            onLisensi = { navController.navigate(RUTE_SETELAN_LISENSI) },
                            onFaq = { navController.navigate(RUTE_SETELAN_FAQ) },
                        )
                        RUTE_SETELAN_CATATAN -> CatatanRilisScreen(kembali)
                        RUTE_SETELAN_LISENSI -> LisensiScreen(kembali, onPilih = { navController.navigate(ruteTeksLisensi(it)) })
                        RUTE_SETELAN_FAQ -> FaqScreen(kembali)
                        else -> TeksLisensiScreen(daftarLisensi[rute.substringAfterLast('/').toInt()], kembali)
                    }
                }
            }
        }
        NotoBottomBar(aktif = tabAktif, onPilih = ::pilihTab, modifier = Modifier.align(Alignment.BottomCenter))
        // Sheet Setelan menutupi seluruh layar termasuk nav, seperti overlay FigCraft.
        LapisanSheetSetelan(sheetSetelan, { sheetSetelan = it }, setelan, setelanVm::ubah)
        // "Ya, hapus aturan" dari Detail: aturan disembunyikan, lalu Detail diganti Blokir - Daftar (LaunchedEffect di atas).
        NotoSheet(tampil = hapusDariDetail, onTutup = { hapusDariDetail = false }) {
            SheetHapus(namaSosmed, contohDetailSosmed.aplikasi.size, onTutup = { hapusDariDetail = false }) {
                hapusDariDetail = false
                onHapusSosmed()
            }
        }
    }
}
