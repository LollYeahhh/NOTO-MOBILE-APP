package id.noto.app.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R

// Lembar contoh design system, HANYA untuk dicek di panel Preview Android Studio
// dan dibandingkan dengan FigCraft. Tidak tampil di aplikasi, jadi teksnya tidak
// dimasukkan ke strings.xml.

private val daftarWarna = listOf(
    "Putih" to NotoWarna.Putih,
    "Permukaan" to NotoWarna.Permukaan,
    "Primer" to NotoWarna.Primer,
    "Primer muda" to NotoWarna.PrimerMuda,
    "Teks utama" to NotoWarna.TeksUtama,
    "Teks sekunder" to NotoWarna.TeksSekunder,
    "Garis" to NotoWarna.Garis,
    "Pasif" to NotoWarna.Pasif,
    "Info latar" to NotoWarna.InfoLatar,
    "Info" to NotoWarna.Info,
    "Peringatan" to NotoWarna.Peringatan,
    "Blur dekoratif" to NotoWarna.BlurDekoratif,
)

private val daftarWarnaLama = listOf(
    "Lama: ikon nav" to NotoWarnaLama.IkonNav,
    "Lama: garis nav" to NotoWarnaLama.IkonNavGaris,
    "Lama: hitam" to NotoWarnaLama.Hitam,
    "Lama: ikon Bulan Ini" to NotoWarnaLama.IkonBulanIni,
)

private val daftarTeks = listOf(
    "NOTO/Angka Hero" to NotoTeks.AngkaHero,
    "NOTO/Judul Kartu" to NotoTeks.JudulKartu,
    "NOTO/Judul Item" to NotoTeks.JudulItem,
    "NOTO/Label" to NotoTeks.Label,
    "NOTO/Teks" to NotoTeks.Teks,
    "NOTO/Kecil Label" to NotoTeks.KecilLabel,
    "NOTO/Kecil Teks" to NotoTeks.KecilTeks,
    "NOTO/Kecil Tebal" to NotoTeks.KecilTebal,
    "NOTO/Eyebrow" to NotoTeks.Eyebrow,
    "Lama: Medium 12/14" to NotoTeksLama.Label12Rapat,
    "Lama: SemiBold 15/20" to NotoTeksLama.JudulBulanIni,
    "Lama: Regular 11/16" to NotoTeksLama.KeteranganBulanIni,
)

private val daftarIkon = listOf(
    "jam" to R.drawable.ic_jam,
    "jam pasir" to R.drawable.ic_jam_pasir,
    "lonceng" to R.drawable.ic_lonceng,
    "chevron" to R.drawable.ic_chevron_kanan,
    "kembali" to R.drawable.ic_kembali,
    "tutup" to R.drawable.ic_tutup,
    "cari" to R.drawable.ic_cari,
    "centang" to R.drawable.ic_centang,
    "kunci" to R.drawable.ic_kunci,
    "sampah" to R.drawable.ic_tempat_sampah,
    "kalender" to R.drawable.ic_kalender,
    "pensil" to R.drawable.ic_pensil,
    "minus" to R.drawable.ic_minus,
    "tambah" to R.drawable.ic_tambah,
    "info" to R.drawable.ic_info,
    "perisai" to R.drawable.ic_perisai,
    "bulan" to R.drawable.ic_bulan,
    "aplikasi" to R.drawable.ic_aplikasi,
    "blokir" to R.drawable.ic_blokir,
    "jeda" to R.drawable.ic_jeda,
    "titik tiga" to R.drawable.ic_titik_tiga,
    "plus" to R.drawable.ic_plus,
    "nav beranda" to R.drawable.ic_nav_beranda,
    "nav blokir" to R.drawable.ic_nav_blokir,
    "nav statistik" to R.drawable.ic_nav_statistik,
    "nav setelan" to R.drawable.ic_nav_setelan,
    "lama blokir kartu" to R.drawable.ic_lama_blokir_kartu,
    "lama kebab" to R.drawable.ic_lama_kebab,
    "lama blokir inline" to R.drawable.ic_lama_blokir_inline,
    "lama Bulan Ini" to R.drawable.ic_lama_bulan_ini,
    "lama Minggu Ini" to R.drawable.ic_lama_minggu_ini,
)

@Composable
private fun Judul(teks: String) {
    Text(teks, style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ContohWarna(nama: String, warna: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(40.dp)
                .background(warna, RoundedCornerShape(12.dp))
                .border(1.dp, NotoWarna.Garis, RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(nama, style = NotoTeks.Label, color = NotoWarna.TeksUtama)
            // Tampilkan hex tanpa alpha, mis. #5B7B6A, supaya mudah dicocokkan dengan FigCraft.
            val hex = "#" + Integer.toHexString(warna.toArgb()).uppercase().takeLast(6)
            Text(hex, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
    }
}

@Composable
private fun ContohTeks(nama: String, gaya: TextStyle) {
    val isi = if (gaya == NotoTeks.Eyebrow) "sisa kuota hari ini".uppercase() else "Pakai dengan sadar, ya"
    Column {
        Text(nama, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        // Latar Permukaan memperlihatkan tinggi baris, untuk dicocokkan dengan kotak teks di FigCraft.
        Text(isi, style = gaya, color = NotoWarna.TeksUtama, modifier = Modifier.background(NotoWarna.Permukaan))
    }
}

@Composable
private fun GridAset(daftar: List<Pair<String, Int>>, latar: Color = NotoWarna.Putih) {
    // 4 kolom per baris, tanpa tint. Ikon tampil dengan ukuran aslinya dari FigCraft;
    // yang lebih besar dari kotak 64 dp (logo NOTO, ikon kosong 125 dp) diperkecil agar muat.
    daftar.chunked(4).forEach { baris ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            baris.forEach { (nama, ikon) -> SelAset(nama, ikon, latar) }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SelAset(nama: String, @DrawableRes ikon: Int, latar: Color) {
    Column(
        Modifier.width(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(64.dp)
                .background(latar, RoundedCornerShape(12.dp))
                .border(1.dp, NotoWarna.Garis, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(ikon), contentDescription = nama)
        }
        Text(nama, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
    }
}

// Preview dipecah agar tiap gambar pendek dan tidak terpotong di panel Android Studio.
@Composable
private fun LembarWarna(judul: String, daftar: List<Pair<String, Color>>) {
    NOTOAPPTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Judul(judul)
            daftar.forEach { (nama, warna) -> ContohWarna(nama, warna) }
        }
    }
}

@Preview(name = "1a. Warna NOTO", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewWarna() = LembarWarna("Warna NOTO", daftarWarna)

@Preview(name = "1b. Warna pola lama", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewWarnaLama() = LembarWarna("Warna pola lama", daftarWarnaLama)

@Preview(name = "2. Teks", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewTeks() {
    NOTOAPPTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Judul("Text style")
            daftarTeks.forEach { (nama, gaya) -> ContohTeks(nama, gaya) }
        }
    }
}

@Preview(name = "3a. Ikon", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewIkon() {
    NOTOAPPTheme {
        Column(Modifier.padding(16.dp)) {
            Judul("Ikon")
            GridAset(daftarIkon)
        }
    }
}

@Preview(name = "3b. Ikon besar & logo", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewLogo() {
    NOTOAPPTheme {
        Column(Modifier.padding(16.dp)) {
            Judul("Ikon besar & logo")
            GridAset(listOf("lama blokir kosong" to R.drawable.ic_lama_blokir_kosong))
            GridAset(listOf("logo NOTO" to R.drawable.ic_logo_noto), latar = NotoWarna.Permukaan)
        }
    }
}
