package id.noto.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import id.noto.app.R

// Satu keluarga font: Poppins (file .ttf di res/font, lisensi OFL).
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

// FigCraft menaruh teks di tengah tinggi barisnya, tanpa dipotong atas/bawah.
// Pengaturan ini membuat Compose melakukan hal yang sama, supaya posisi teks persis.
private val SepertiFigCraft = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun gaya(
    ukuran: Int,
    tinggiBaris: TextUnit,
    tebal: FontWeight,
    jarakHuruf: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = Poppins,
    fontWeight = tebal,
    fontSize = ukuran.sp,
    lineHeight = tinggiBaris,
    letterSpacing = jarakHuruf,
    lineHeightStyle = SepertiFigCraft,
)

// 9 text style NOTO/* dari FigCraft (ukuran/tinggi baris dalam sp = px di FigCraft).
object NotoTeks {
    val AngkaHero = gaya(24, 32.sp, FontWeight.Bold)
    val JudulKartu = gaya(16, 24.sp, FontWeight.SemiBold)
    val JudulItem = gaya(14, 20.sp, FontWeight.SemiBold)
    val Label = gaya(12, 16.sp, FontWeight.Medium)
    val Teks = gaya(12, 16.sp, FontWeight.Normal)
    val KecilLabel = gaya(10, 14.sp, FontWeight.Medium)
    val KecilTeks = gaya(10, 14.sp, FontWeight.Normal)
    val KecilTebal = gaya(10, 14.sp, FontWeight.SemiBold)

    // Eyebrow di FigCraft memakai "case: upper". TextStyle Compose tidak punya pengaturan
    // huruf kapital, jadi teksnya harus di-uppercase() saat ditampilkan.
    val Eyebrow = gaya(10, 14.sp, FontWeight.SemiBold, jarakHuruf = 0.5.sp)
}

// Teks pola lama yang tidak memakai style NOTO/* di FigCraft. Direplikasi apa adanya (§0).
object NotoTeksLama {
    val Label12Rapat = gaya(12, 14.sp, FontWeight.Medium) // Statistik (tiga layar)
    val JudulBulanIni = gaya(15, 20.sp, FontWeight.SemiBold) // Top List di Statistik - Bulan Ini
    val KeteranganBulanIni = gaya(11, 16.sp, FontWeight.Normal) // "486 kali dibuka" dll.
}

// Gaya bawaan untuk komponen Material, supaya tidak ada teks yang jatuh ke font Roboto.
val Typography = Typography(
    bodyLarge = NotoTeks.Teks,
    bodyMedium = NotoTeks.Teks,
    labelLarge = NotoTeks.Label,
)
