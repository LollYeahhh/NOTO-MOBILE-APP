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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import id.noto.app.data.AturanLengkap
import id.noto.app.data.Kuota
import id.noto.app.ui.blocks.AturanViewModel
import id.noto.app.ui.blocks.BlokirScreen
import id.noto.app.ui.blocks.AplikasiPilihan
import id.noto.app.ui.permission.Izin
import id.noto.app.ui.permission.SheetIzinBerurutan
import id.noto.app.ui.blocks.keBeranda
import id.noto.app.ui.blocks.keDaftar
import id.noto.app.ui.blocks.keDetail
import id.noto.app.ui.blocks.keForm
import id.noto.app.ui.blocks.rememberWaktu
import id.noto.app.ui.blocks.DetailAturanScreen
import id.noto.app.ui.blocks.FormAturan
import id.noto.app.ui.blocks.FormAturanScreen
import id.noto.app.ui.blocks.ModeForm
import id.noto.app.ui.blocks.PilihAplikasiScreen
import id.noto.app.ui.blocks.SheetForm
import id.noto.app.ui.blocks.SheetHapus
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.blocks.namaOtomatis
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
import id.noto.app.ui.stats.StatistikViewModel
import id.noto.app.ui.stats.harianAplikasi
import id.noto.app.ui.stats.kejadianAplikasi
import id.noto.app.ui.stats.statistikBulanan
import id.noto.app.ui.stats.statistikHariIni
import java.time.LocalDate

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
fun NotoNavGraph(keStatistik: Boolean = false, onKeStatistik: () -> Unit = {}) {
    val navController = rememberNavController()
    // Aturan dibaca dari dan disimpan ke Room; Setelan dari DataStore. Keduanya satu untuk seluruh layar.
    val aturanVm: AturanViewModel = viewModel()
    val daftarAturan by aturanVm.semua.collectAsState()
    val aplikasiTerpasang by aturanVm.terpasang.collectAsState()
    val kuotaTercatat by aturanVm.kuota.collectAsState()
    val setelanVm: SetelanViewModel = viewModel()
    val setelan by setelanVm.setelan.collectAsState()
    // Isian form Tambah/Edit yang sedang dikerjakan (belum disimpan).
    var form by remember { mutableStateOf(FormAturan()) }
    // Aturan yang dibuka di Detail dan Edit (id Room); disimpan supaya tetap ada setelah aplikasi dipulihkan.
    var idDetail by rememberSaveable { mutableLongStateOf(0L) }
    val aturanDetail = daftarAturan.firstOrNull { it.aturan.id == idDetail }
    // true sejak "Ya, hapus aturan" sampai Detail aturan lain dibuka: Detail diganti Blokir - Daftar.
    var dihapus by remember { mutableStateOf(false) }
    // Nama aturan yang sudah ada, supaya nama otomatis tidak kembar.
    val namaAturan = daftarAturan.map { it.aturan.nama }
    val namaDasar = stringResource(R.string.nama_otomatis)
    val polaNomor = stringResource(R.string.nama_otomatis_nomor)
    // Pilih Aplikasi ditutup lewat tombol "Pilih N Aplikasi" (dissolve) atau kembali (instant).
    var pilihLewatTombol by remember { mutableStateOf(false) }
    // Setelah "Pilih N Aplikasi", Tambah tampil dengan Sheet Atur Jadwal sudah terbuka (FigCraft node 2456).
    var bukaJadwal by remember { mutableStateOf(false) }
    // Sheet izin yang menunggu tampil (setelah aturan disimpan, atau dari baris izin di Setelan).
    var antreanIzin by remember { mutableStateOf(emptyList<Izin>()) }
    // Sheet izin pabrik (tidak bisa dicek) dan Aksesibilitas (opsional) hanya muncul otomatis sekali;
    // setelahnya lewat baris Setelan.
    val antreanSetelahSimpan = Izin.entries.filterNot {
        (it == Izin.Perangkat && setelan.izinPerangkatDijawab) || (it == Izin.Aksesibilitas && setelan.izinAksesibilitasDijawab)
    }

    // Dissolve splash → Beranda: cangkang (Beranda beserta nav) muncul memudar di atas splash.
    val pudarSplash = tween<Float>(DURASI_PUDAR_SPLASH_MS, easing = EaseInOutFigCraft)
    val gerak = tween<IntOffset>(DURASI_PUSH_MS, easing = EaseOutFigCraft)
    // Layar lama tetap terlihat penuh di bawah layar baru selama animasi (move in).
    val diamDiBawah = fadeOut(tween(DURASI_PUSH_MS), targetAlpha = 1f)

    Box(Modifier.fillMaxSize()) {
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
                aturan = daftarAturan,
                terpasang = aplikasiTerpasang,
                kuotaTercatat = kuotaTercatat,
                setelanVm = setelanVm,
                onTambah = { form = FormAturan(); navController.navigate(RUTE_TAMBAH) },
                onEdit = { aturanDetail?.let { form = it.keForm(); navController.navigate(RUTE_EDIT) } },
                aturanDetail = aturanDetail,
                onBukaDetail = { idDetail = it; dihapus = false },
                dihapus = dihapus,
                onHapus = { aturanVm.hapus(idDetail); dihapus = true },
                onAktif = { aturanVm.ubahAktif(idDetail, it) },
                onSenyap = { aturanVm.ubahSenyap(idDetail, it) },
                onMintaIzin = { antreanIzin = listOf(it) },
                keStatistik = keStatistik,
                onKeStatistik = onKeStatistik,
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
                onSimpan = {
                    // Nama kosong → nama otomatis (Blokir, Blokir 1, …).
                    aturanVm.tambah(form, form.nama.trim().ifBlank { namaOtomatis(namaDasar, polaNomor, namaAturan) })
                    antreanIzin = antreanSetelahSimpan
                    navController.popBackStack()
                },
                onPilihAplikasi = { pilihLewatTombol = false; bukaJadwal = false; navController.navigate(RUTE_PILIH) },
                namaTerpakai = namaAturan,
                sheetAwal = if (bukaJadwal) SheetForm.Jadwal else null,
                jadwalAwal = setelan.jadwalBawaan,
                kuotaAwal = setelan.kuotaBawaanMenit,
                namaAplikasi = { p -> aplikasiTerpasang.firstOrNull { it.paket == p }?.nama ?: p },
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
            // Daftar dibaca ulang tiap layar dibuka, supaya aplikasi yang baru dipasang ikut muncul.
            LaunchedEffect(Unit) { aturanVm.muatAplikasi() }
            PilihAplikasiScreen(
                awal = form.aplikasi,
                aplikasi = aplikasiTerpasang,
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
                if (dihapus) fadeOut(tween(DURASI_DISSOLVE_MS, easing = EaseOutFigCraft)) else ExitTransition.None
            },
        ) {
            // Kembali dan "Simpan Perubahan": back instant.
            FormAturanScreen(
                mode = ModeForm.Edit,
                form = form,
                onUbah = { form = it },
                onTutup = { navController.popBackStack() },
                onSimpan = {
                    aturanDetail?.let { lama ->
                        // Nama kosong → nama otomatis (Blokir, Blokir 1, …).
                        val nama = form.nama.trim().ifBlank { namaOtomatis(namaDasar, polaNomor, namaAturan - lama.aturan.nama) }
                        aturanVm.ubah(lama.aturan, form, nama)
                    }
                    antreanIzin = antreanSetelahSimpan
                    navController.popBackStack()
                },
                onHapus = { aturanVm.hapus(idDetail); dihapus = true; navController.popBackStack() },
                onPilihAplikasi = { pilihLewatTombol = false; bukaJadwal = false; navController.navigate(RUTE_PILIH) },
                // Nama aturan yang sedang diedit tidak dihitung terpakai.
                namaTerpakai = namaAturan - aturanDetail?.aturan?.nama.orEmpty(),
                namaAplikasi = { p -> aplikasiTerpasang.firstOrNull { it.paket == p }?.nama ?: p },
            )
        }
    }
    // Setiap aturan disimpan: sheet izin yang belum diberikan muncul berurutan di atas layar.
    SheetIzinBerurutan(
        antreanIzin,
        onDijawab = {
            when (it) {
                Izin.Perangkat -> setelanVm.ubah(setelan.copy(izinPerangkatDijawab = true))
                Izin.Aksesibilitas -> setelanVm.ubah(setelan.copy(izinAksesibilitasDijawab = true))
                else -> {}
            }
        },
    ) { antreanIzin = it }
    }
}

/** Cangkang: empat tab dan Detail Aturan dengan Bottom Nav di bawahnya. */
@Composable
private fun CangkangTab(
    aturan: List<AturanLengkap>,
    terpasang: List<AplikasiPilihan>,
    kuotaTercatat: List<Kuota>,
    setelanVm: SetelanViewModel,
    onTambah: () -> Unit,
    onEdit: () -> Unit,
    aturanDetail: AturanLengkap?,
    onBukaDetail: (Long) -> Unit,
    dihapus: Boolean,
    onHapus: () -> Unit,
    onAktif: (Boolean) -> Unit,
    onSenyap: (Boolean) -> Unit,
    onMintaIzin: (Izin) -> Unit,
    keStatistik: Boolean = false,
    onKeStatistik: () -> Unit = {},
) {
    val navController = rememberNavController()
    // Sheet Hapus Aturan dari tombol "Hapus aturan" di Detail.
    var hapusDariDetail by remember { mutableStateOf(false) }
    // Aplikasi yang dibuka di Detail Aplikasi (disimpan di memori, bukan argumen rute).
    var aplikasiStat by remember { mutableStateOf(AplikasiStatistik(R.drawable.logo_instagram, "Instagram", 70)) }
    val statistikVm: StatistikViewModel = viewModel()
    val pemakaian by statistikVm.pemakaian.collectAsState()
    val kejadian by statistikVm.kejadian.collectAsState()
    val petaAplikasi = remember(terpasang) { terpasang.associateBy { it.paket } }

    // Setelah aturan dihapus dari Edit, Detail-nya tidak ada lagi: pindah ke Blokir - Daftar tanpa animasi.
    // Navigasi ditunda sampai cangkang aktif lagi (Edit selesai memudar): navigate() saat cangkang belum aktif
    // membuat NavHost di dalamnya mengabaikan navigasi berikutnya sampai tombol back ditekan.
    // Selama menunggu, rute Detail sudah menampilkan isi Daftar, jadi tidak terlihat lompatan.
    val siklus = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(dihapus) {
        if (!dihapus) return@LaunchedEffect
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

    // "Lihat statistik hari ini" di layar Terkunci: pindah ke tab Statistik.
    LaunchedEffect(keStatistik) {
        if (keStatistik) {
            pilihTab(Tab.Statistik)
            onKeStatistik()
        }
    }

    // Isi layar memudar selama pill bergeser (layer yang tidak cocok di smart animate memudar).
    val pudar = tween<Float>(DURASI_GESER_MS, easing = EaseOutFigCraft)
    // Push: layar baru masuk dari kanan, layar lama keluar ke kiri.
    val push = tween<IntOffset>(DURASI_PUSH_MS, easing = EaseOutFigCraft)
    val ruteAsalDetail = setOf(Tab.Blokir.rute, Tab.Beranda.rute, RUTE_STAT_APLIKASI)

    // Status aturan (Berlaku / Di Luar Jadwal) dihitung dari jam sekarang, diperbarui tiap menit.
    val waktu = rememberWaktu()
    val ambang = setelanVm.setelan.collectAsState().value.ambangHampirHabis

    val bukaDetail: (Long) -> Unit = { onBukaDetail(it); navController.navigate(RUTE_DETAIL) }
    // Aturan terakhir di Detail, supaya Sheet Hapus tetap berisi selama memudar setelah aturannya terhapus.
    var detailTerakhir by remember { mutableStateOf(aturanDetail) }
    LaunchedEffect(aturanDetail) { if (aturanDetail != null) detailTerakhir = aturanDetail }

    @Composable
    fun DaftarBlokir() = BlokirScreen(
        aturan = aturan.map { it.keDaftar(waktu, terpasang, kuotaTercatat, ambang) },
        onBukaDetail = bukaDetail,
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
                    aturan = aturan.map { it.keBeranda(terpasang) },
                    onBukaDetail = bukaDetail,
                    onTambah = onTambah,
                )
            }
            composable(
                Tab.Blokir.rute,
                // Dari Detail aturan yang baru dihapus: isinya sudah sama, jadi tanpa animasi.
                enterTransition = { if (dihapus && initialState.destination.route == RUTE_DETAIL) EnterTransition.None else fadeIn(pudar) },
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
                exitTransition = { if (dihapus) ExitTransition.None else fadeOut(pudar) },
                popExitTransition = { if (targetState.destination.route in ruteAsalDetail) ExitTransition.None else fadeOut(pudar) },
            ) {
                val d = aturanDetail
                if (dihapus || d == null) {
                    DaftarBlokir()
                } else {
                    DetailAturanScreen(
                        onKembali = { navController.popBackStack() },
                        onEdit = onEdit,
                        onHapus = { hapusDariDetail = true },
                        detail = d.keDetail(waktu, terpasang, kuotaTercatat, ambang),
                        aktif = d.aturan.aktif,
                        onAktif = onAktif,
                        senyap = d.aturan.senyap,
                        onSenyap = onSenyap,
                    )
                }
            }
            composable(
                Tab.Statistik.rute,
                exitTransition = { if (targetState.destination.route in RUTE_STAT_DALAM) slideOutHorizontally(push) { -it } else fadeOut(pudar) },
                popEnterTransition = { if (initialState.destination.route in RUTE_STAT_DALAM) EnterTransition.None else fadeIn(pudar) },
            ) {
                val hari = LocalDate.now()
                StatistikScreen(
                    hariIni = remember(pemakaian, petaAplikasi, hari) { statistikHariIni(pemakaian, hari, petaAplikasi) },
                    bulanan = remember(pemakaian, petaAplikasi, hari) { statistikBulanan(pemakaian, hari, petaAplikasi) },
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
                        // Aturan pertama yang memuat aplikasi ini.
                        val aturanApp = aturan.firstOrNull { aplikasiStat.paket in it.paket }
                        val terpasangApp = petaAplikasi[aplikasiStat.paket]
                        DetailAplikasiScreen(
                            aplikasiStat,
                            aturan = aturanApp?.aturan,
                            onKembali = kembali,
                            onAturan = { aturanApp?.let { bukaDetail(it.aturan.id) } },
                            kategori = terpasangApp?.kategori,
                            // Aplikasi contoh (Minggu Ini, sampai Tahap 8C) tidak terpasang: tetap data contoh.
                            harian = terpasangApp?.let { harianAplikasi(pemakaian, it.paket, LocalDate.now()) },
                            kejadian = terpasangApp?.let { kejadianAplikasi(kejadian, it.paket, aturan, LocalDate.now()) },
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
                    onMintaIzin = onMintaIzin,
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
        // "Ya, hapus aturan" dari Detail: aturan dihapus, lalu Detail diganti Blokir - Daftar (LaunchedEffect di atas).
        NotoSheet(tampil = hapusDariDetail, onTutup = { hapusDariDetail = false }) {
            val d = aturanDetail ?: detailTerakhir
            SheetHapus(d?.aturan?.nama.orEmpty(), d?.paket?.size ?: 0, onTutup = { hapusDariDetail = false }) {
                hapusDariDetail = false
                onHapus()
            }
        }
    }
}
