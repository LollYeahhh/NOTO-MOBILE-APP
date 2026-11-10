package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

// Komponen "Input" (node 1299): tinggi 48, Putih, garis 1 px Garis, sudut 12, padding 16, NOTO/Teks.
// Garis 1 px ikut dihitung dalam tata letak FigCraft, jadi isi mulai di x17.
// Status Kosong = placeholder sage pucet; Terisi = teks utama. Ikon cari opsional ("Tampilkan Ikon").

@Composable
fun NotoInput(
    nilai: String,
    onUbah: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    tampilkanIkon: Boolean = false,
) {
    val bentuk = RoundedCornerShape(12.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(NotoWarna.Putih, bentuk)
            .border(1.dp, NotoWarna.Garis, bentuk)
            .padding(horizontal = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (tampilkanIkon) {
            Image(painterResource(R.drawable.ic_cari), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
        }
        BasicTextField(
            value = nilai,
            onValueChange = onUbah,
            singleLine = true,
            textStyle = NotoTeks.Teks.copy(color = NotoWarna.TeksUtama),
            cursorBrush = SolidColor(NotoWarna.TeksUtama),
            modifier = Modifier.weight(1f),
            decorationBox = { isian ->
                Box {
                    if (nilai.isEmpty()) Text(placeholder, style = NotoTeks.Teks, color = NotoWarna.Pasif, maxLines = 1)
                    isian()
                }
            },
        )
    }
}

// Teks di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Input", widthDp = 361)
@Composable
private fun PreviewInput() {
    NOTOAPPTheme {
        Column {
            NotoInput("", {}, placeholder = "Beri nama aturan, misal Sosmed")
            NotoInput("Sosmed", {}, placeholder = "Beri nama aturan, misal Sosmed")
            NotoInput("", {}, placeholder = "Beri nama aturan, misal Sosmed", tampilkanIkon = true)
        }
    }
}
