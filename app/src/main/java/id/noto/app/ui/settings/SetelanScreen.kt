package id.noto.app.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderLogo
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/** Tinggi Bottom Nav (114) yang menutupi bagian bawah layar. */
internal val TINGGI_NAV = 114.dp

/**
 * "Setelan - Utama" (node 3031): judul halaman, tiga seksi berisi Baris Setelan, lalu penutup.
 * Notifikasi & Pengingat dan Aturan Pembatasan membuka halamannya (push).
 * Akses penggunaan dan Aksesibilitas membuka halaman setelan Android yang sesuai.
 */
@Composable
fun SetelanScreen(onNotifikasi: () -> Unit, onAturan: () -> Unit, onTentang: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderLogo()
        IsiSetelan {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(stringResource(R.string.nav_setelan), style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
                Text(stringResource(R.string.setelan_sub), Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
            }
            SeksiSetelan(R.string.seksi_pengingat_aturan) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_lonceng),
                    stringResource(R.string.notifikasi_pengingat),
                    keterangan = stringResource(R.string.notifikasi_pengingat_ket),
                    onKlik = onNotifikasi,
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_blokir),
                    stringResource(R.string.aturan_pembatasan),
                    keterangan = stringResource(R.string.aturan_pembatasan_ket),
                    onKlik = onAturan,
                )
            }
            SeksiSetelan(R.string.seksi_izin) {
                // shortcut: status "Aktif" masih contoh; dibaca dari izin sungguhan di Tahap 6–7.
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_perisai),
                    stringResource(R.string.akses_penggunaan),
                    keterangan = stringResource(R.string.akses_penggunaan_ket),
                    nilai = stringResource(R.string.status_aktif),
                    onKlik = { bukaSetelanAndroid(context, Settings.ACTION_USAGE_ACCESS_SETTINGS) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kunci),
                    stringResource(R.string.aksesibilitas),
                    keterangan = stringResource(R.string.aksesibilitas_ket),
                    nilai = stringResource(R.string.status_aktif),
                    onKlik = { bukaSetelanAndroid(context, Settings.ACTION_ACCESSIBILITY_SETTINGS) },
                )
            }
            SeksiSetelan(R.string.seksi_tentang) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_info),
                    stringResource(R.string.tentang_noto),
                    keterangan = stringResource(R.string.tentang_noto_ket),
                    nilai = stringResource(R.string.versi_aplikasi),
                    onKlik = onTentang,
                )
            }
            Text(
                stringResource(R.string.hak_cipta),
                Modifier.fillMaxWidth(),
                style = NotoTeks.KecilTeks,
                color = NotoWarna.Pasif,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Membuka halaman setelan Android (Akses penggunaan, Aksesibilitas). Beberapa HP tidak punya halamannya. */
private fun bukaSetelanAndroid(context: Context, aksi: String) {
    try {
        context.startActivity(Intent(aksi))
    } catch (_: ActivityNotFoundException) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

// ---------- Bagian yang dipakai ketiga halaman Setelan ----------

/** Area isi yang bisa digeser: mulai 24 di bawah header, margin 16, jarak antarseksi 24. */
@Composable
internal fun IsiSetelan(isi: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = TINGGI_NAV + 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = isi,
    )
}

/** Seksi: Label Seksi (NOTO/Label, teks sekunder; [label] null = tanpa label), lalu Kartu (Permukaan sudut 16, padding 4/16) dan [bawah], jarak 8. */
@Composable
internal fun SeksiSetelan(label: Int?, bawah: @Composable () -> Unit = {}, isi: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (label != null) Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        Column(
            Modifier.fillMaxWidth().background(NotoWarna.Permukaan, RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 4.dp),
            content = isi,
        )
        bawah()
    }
}

/** Panel info: Biru pucet sudut 12, padding 12, ikon biru 24 lalu teks biru (rata atas), jarak 8. */
@Composable
internal fun PanelInfoSetelan(ikon: Int, judul: String?, isi: String) {
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.InfoLatar, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(ikon), contentDescription = null, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(NotoWarna.Info))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (judul != null) Text(judul, Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.Info)
            Text(isi, style = NotoTeks.KecilTeks, color = NotoWarna.Info)
        }
    }
}

@Preview(name = "Setelan - Utama", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSetelan() {
    NOTOAPPTheme { SetelanScreen({}, {}, {}) }
}
