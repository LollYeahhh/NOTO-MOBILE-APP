package id.noto.app.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/** Alamat tujuan "Kirim masukan" dan "Kirim email". Sengaja tidak ditampilkan di layar. */
private const val EMAIL_PENGEMBANG = "widierik51@gmail.com"
private const val URL_KODE_SUMBER = "https://github.com/LollYeahhh/NOTO-MOBILE-APP"

/**
 * "Setelan - Tentang Noto" (node 4847): logo, tagline dan versi, lalu dua kartu baris
 * (Catatan rilis, Lisensi; Kirim masukan, Kode sumber, FAQ) dan hak cipta.
 */
@Composable
fun TentangScreen(onKembali: () -> Unit, onCatatanRilis: () -> Unit, onLisensi: () -> Unit, onFaq: () -> Unit) {
    val context = LocalContext.current
    val subjekMasukan = stringResource(R.string.subjek_masukan)
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.tentang_noto), onIkonKiri = onKembali)
        IsiSetelan {
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(R.drawable.ic_logo_noto_tentang), contentDescription = stringResource(R.string.logo_noto))
                Text(stringResource(R.string.tentang_noto_ket), style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama)
                Text(stringResource(R.string.versi_teks), Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
            }
            SeksiSetelan(label = null) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_dokumen),
                    stringResource(R.string.catatan_rilis),
                    keterangan = stringResource(R.string.catatan_rilis_ket),
                    onKlik = onCatatanRilis,
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_info),
                    stringResource(R.string.lisensi_open_source),
                    keterangan = stringResource(R.string.lisensi_open_source_ket),
                    onKlik = onLisensi,
                )
            }
            SeksiSetelan(label = null) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_surat),
                    stringResource(R.string.kirim_masukan),
                    keterangan = stringResource(R.string.kirim_masukan_ket),
                    onKlik = { kirimEmail(context, subjekMasukan) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kode),
                    stringResource(R.string.kode_sumber),
                    keterangan = stringResource(R.string.kode_sumber_ket),
                    onKlik = { bukaAplikasiLain(context, Intent(Intent.ACTION_VIEW, Uri.parse(URL_KODE_SUMBER))) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_tanya),
                    stringResource(R.string.faq),
                    keterangan = stringResource(R.string.faq_ket),
                    onKlik = onFaq,
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

/** Membuka aplikasi email dengan penerima pengembang dan [subjek] terisi. */
internal fun kirimEmail(context: Context, subjek: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        .putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_PENGEMBANG))
        .putExtra(Intent.EXTRA_SUBJECT, subjek)
    bukaAplikasiLain(context, intent)
}

/** Kalau HP tidak punya aplikasi yang bisa membukanya (misalnya tanpa aplikasi email), tidak terjadi apa-apa. */
private fun bukaAplikasiLain(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    }
}

@Preview(name = "Setelan - Tentang Noto", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTentang() {
    NOTOAPPTheme { TentangScreen({}, {}, {}, {}) }
}
