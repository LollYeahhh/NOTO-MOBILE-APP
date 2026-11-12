package id.noto.app.ui.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.noto.app.R
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/** Teks dan ikon satu sheet izin (FigCraft "Izin - Sheet …"). */
private class IsiSheet(val judul: Int, val ket: Int, val ikon: Int, val panelJudul: Int, val panelKet: Int, val tombol: Int)

private fun isiSheet(izin: Izin) = when (izin) {
    Izin.AksesPenggunaan -> IsiSheet(
        R.string.izin_penggunaan_judul, R.string.izin_penggunaan_ket, R.drawable.ic_perisai,
        R.string.izin_penggunaan_panel_judul, R.string.izin_penggunaan_panel_ket, R.string.buka_setelan,
    )
    Izin.Notifikasi -> IsiSheet(
        R.string.izin_notifikasi_judul, R.string.izin_notifikasi_ket, R.drawable.ic_lonceng,
        R.string.izin_notifikasi_panel_judul, R.string.izin_notifikasi_panel_ket, R.string.izinkan_notifikasi,
    )
    Izin.Baterai -> IsiSheet(
        R.string.izin_baterai_judul, R.string.izin_baterai_ket, R.drawable.ic_info,
        R.string.izin_baterai_panel_judul, R.string.izin_baterai_panel_ket, R.string.buka_setelan_baterai,
    )
    Izin.TampilDiAtas -> IsiSheet(
        R.string.izin_atas_judul, R.string.izin_atas_ket, R.drawable.ic_info,
        R.string.izin_atas_panel_judul, R.string.izin_atas_panel_ket, R.string.buka_setelan,
    )
    Izin.Perangkat -> IsiSheet(
        R.string.izin_perangkat_judul, R.string.izin_perangkat_sub, R.drawable.ic_info,
        R.string.izin_perangkat_panel_judul, R.string.izin_perangkat_panel_ket, R.string.buka_info_aplikasi,
    )
}

/**
 * Isi "Izin - Sheet Akses Penggunaan / Notifikasi / Optimasi Baterai / Tampil di Atas / Perangkat": Kepala Sheet, panel petunjuk
 * (sama dengan Panel Alternatif Sheet Hapus), lalu tombol utama dan "Nanti saja".
 * [onSelesai] dipanggil setelah tombol utama atau "Nanti saja": sheet berikutnya (kalau ada) yang tampil.
 */
@Composable
fun ColumnScope.SheetIzin(izin: Izin, onTutup: () -> Unit, onSelesai: () -> Unit) {
    val context = LocalContext.current
    val isi = isiSheet(izin)
    // Android 13+: dialog izin notifikasi. Kalau sistem tidak mau menampilkannya lagi (sudah ditolak dua kali),
    // pengguna dibawa ke halaman notifikasi Noto di Setelan Android.
    val mintaNotifikasi = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { diizinkan ->
        val activity = context as? Activity
        if (!diizinkan && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
        ) {
            bukaSetelanNotifikasi(context)
        }
        onSelesai()
    }
    val aksi: () -> Unit = {
        val bisaMintaNotifikasi = izin == Izin.Notifikasi && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (bisaMintaNotifikasi) {
            // Sheet berikutnya baru tampil setelah dialog dijawab (lihat mintaNotifikasi).
            mintaNotifikasi.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            when (izin) {
                Izin.AksesPenggunaan -> bukaSetelanAksesPenggunaan(context)
                Izin.Baterai -> bukaSetelanBaterai(context)
                Izin.TampilDiAtas -> bukaSetelanTampilDiAtas(context)
                Izin.Perangkat -> bukaSetelanPerangkat(context)
                // Izin sudah ada tapi notifikasi dimatikan di Setelan (atau Android < 13): buka halamannya.
                Izin.Notifikasi -> bukaSetelanNotifikasi(context)
            }
            onSelesai()
        }
    }

    NotoKepalaSheet(stringResource(isi.judul), stringResource(isi.ket), onTutup)
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(isi.ikon), contentDescription = null, modifier = Modifier.size(24.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(isi.panelJudul), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksUtama)
            Text(stringResource(isi.panelKet), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NotoTombol(stringResource(isi.tombol), onKlik = aksi)
        NotoTombol(stringResource(R.string.nanti_saja), onKlik = onSelesai, tipe = TipeTombol.Permukaan)
    }
}

/**
 * Antrean sheet izin: menampilkan [antrean].first(), lalu yang berikutnya setelah dijawab.
 * Izin yang sudah diberikan dilewati. [onUbah] menerima sisa antrean; [onDijawab] menerima izin
 * yang sheet-nya baru ditutup atau dijawab.
 */
@Composable
fun SheetIzinBerurutan(antrean: List<Izin>, onDijawab: (Izin) -> Unit = {}, onUbah: (List<Izin>) -> Unit) {
    val context = LocalContext.current
    val sekarang = antrean.firstOrNull { !izinDiberikan(context, it) }
    NotoSheet(tampil = sekarang != null, onTutup = { onUbah(emptyList()) }) {
        if (sekarang != null) {
            SheetIzin(
                sekarang,
                onTutup = { onDijawab(sekarang); onUbah(emptyList()) },
                onSelesai = { onDijawab(sekarang); onUbah(antrean.dropWhile { it != sekarang }.drop(1)) },
            )
        }
    }
}

@Preview(name = "Izin - Sheet Akses Penggunaan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSheetIzin() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetIzin(Izin.AksesPenggunaan, {}, {}) } }
}
