package id.noto.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import id.noto.app.ui.components.painterAplikasi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

/**
 * Kartu pola lama di Beranda (node 71/111): Permukaan 361×120, sudut 16, padding 16, jarak 12.
 * Atas: nama + keterangan (teks utama), ikon blokir 20 dan kebab 3×15 di kanan.
 * Bawah: logo 27, jarak 5, padding atas-bawah 4. Kartu nonaktif tampil sama (tidak pudar) seperti desain.
 */
@Composable
fun KartuBlokirBeranda(aturan: AturanBeranda, onKlik: (() -> Unit)?, modifier: Modifier = Modifier) {
    val keterangan = if (aturan.jam != null && aturan.kuota != null) {
        stringResource(R.string.jadwal_kuota, aturan.jam, aturan.kuota)
    } else {
        stringResource(R.string.blokir_manual)
    }
    Column(
        modifier
            .fillMaxWidth()
            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
            // onKlik null = kartu tidak bisa diketuk.
            .then(if (onKlik != null) Modifier.clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik) else Modifier)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth().height(36.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(aturan.nama, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama, maxLines = 1)
                Text(keterangan, style = NotoTeks.KecilTeks, color = NotoWarna.TeksUtama, maxLines = 1)
            }
            // Vector diekspor dengan ruang tepi (30 untuk ikon 20, 4×16 untuk kebab 3×15),
            // jadi kotaknya seukuran desain dan gambarnya ditaruh di tengah dengan requiredSize.
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.ic_lama_blokir_kartu), null, Modifier.requiredSize(30.dp))
            }
            Box(Modifier.padding(start = 13.dp).size(3.dp, 15.dp), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.ic_lama_kebab), null, Modifier.requiredSize(4.dp, 16.dp))
            }
        }
        Row(
            Modifier.height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            aturan.logo.forEach { Image(painterAplikasi(it.ikon, it.logoContoh), null, Modifier.size(27.dp)) }
        }
    }
}

@Preview(name = "Kartu Blokir Beranda", widthDp = 361)
@Composable
private fun PreviewKartuBlokirBeranda() {
    NOTOAPPTheme {
        Column(Modifier.background(NotoWarna.Putih), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contohAturanBeranda.forEach { KartuBlokirBeranda(it, onKlik = {}) }
        }
    }
}
