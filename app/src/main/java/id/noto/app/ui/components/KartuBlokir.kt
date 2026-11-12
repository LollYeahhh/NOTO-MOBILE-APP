package id.noto.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

/**
 * Lima status aturan (§5) dan tampilannya di "Kartu Blokir" (node 1483):
 * jenis badge, warna progress kuota (null = progress tidak tampil), dan apakah bagian kuota tampil.
 */
enum class StatusAturan(val badge: TipeBadge, val warnaProgress: Color?, val tampilkanKuota: Boolean = true) {
    Berlaku(TipeBadge.Aktif, NotoWarna.PrimerMuda),
    HampirHabis(TipeBadge.Peringatan, NotoWarna.Peringatan),
    TimeOut(TipeBadge.Info, NotoWarna.Pasif),
    DiLuarJadwal(TipeBadge.Netral, null),
    Nonaktif(TipeBadge.Netral, null, tampilkanKuota = false),
}

/**
 * Kartu Blokir: Permukaan sudut 16, padding 16, jarak antarbagian 12.
 * Atas: judul + jadwal, badge status di kanan. Kuota: sisa (kiri) dan total (kanan) sejajar garis dasar,
 * lalu track 6 Biru pucet dengan progress [progres] (0..1). Bawah: logo aplikasi 27, jarak 5, padding atas 12.
 * Status Nonaktif: bagian kuota disembunyikan dan logo pudar 50%.
 */
@Composable
fun NotoKartuBlokir(
    judul: String,
    jadwal: String,
    status: StatusAturan,
    labelStatus: String,
    sisa: String,
    total: String,
    progres: Float,
    logo: List<Painter>,
    onKlik: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
            // onKlik null = kartu tidak bisa diketuk.
            .then(if (onKlik != null) Modifier.clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onKlik) else Modifier)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(judul, style = NotoTeks.JudulItem, color = NotoWarna.TeksUtama, maxLines = 1)
                Text(jadwal, style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder, maxLines = 1)
            }
            NotoBadge(labelStatus, status.badge)
        }
        if (status.tampilkanKuota) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(sisa, Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksUtama, maxLines = 1)
                    Text(total, Modifier.alignByBaseline(), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder, maxLines = 1)
                }
                Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(NotoWarna.InfoLatar)) {
                    if (status.warnaProgress != null) {
                        Box(
                            Modifier
                                .fillMaxWidth(progres.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(status.warnaProgress, CircleShape),
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .padding(top = 12.dp)
                .height(28.dp)
                .alpha(if (status == StatusAturan.Nonaktif) 0.5f else 1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            logo.forEach { Image(it, contentDescription = null, modifier = Modifier.size(27.dp)) }
        }
    }
}

// Data di Preview sama dengan nilai bawaan komponen di FigCraft (hanya untuk Preview).
@Preview(name = "Kartu Blokir", widthDp = 361)
@Composable
private fun PreviewKartuBlokir() {
    val logo = listOf(
        painterResource(R.drawable.ic_aplikasi_cadangan),
        painterResource(R.drawable.ic_aplikasi_cadangan),
        painterResource(R.drawable.ic_aplikasi_cadangan),
        painterResource(R.drawable.ic_aplikasi_cadangan),
        painterResource(R.drawable.ic_aplikasi_cadangan),
    )
    val jadwal = "07.00–21.00 · Sen–Jum"
    NOTOAPPTheme {
        Column(Modifier.background(NotoWarna.Putih), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NotoKartuBlokir("Sosmed", jadwal, StatusAturan.Berlaku, "Berlaku s.d. 21.00", "Sisa 45m", "45m dari 1j 30m terpakai", 0.5f, logo, {})
            NotoKartuBlokir("Sosmed", jadwal, StatusAturan.HampirHabis, "Hampir habis", "Sisa 5m", "1j 25m dari 1j 30m terpakai", 309f / 329f, logo, {})
            NotoKartuBlokir("Sosmed", jadwal, StatusAturan.TimeOut, "Bebas lagi 21.00", "Kuota habis, waktunya time out", "1j 30m terpakai", 1f, logo, {})
            NotoKartuBlokir("Sosmed", jadwal, StatusAturan.DiLuarJadwal, "Bebas sampai 07.00", "Kuota siap 1j 30m", "Mulai lagi 07.00", 0f, logo, {})
            NotoKartuBlokir("Sosmed", jadwal, StatusAturan.Nonaktif, "Nonaktif", "", "", 0f, logo, {})
        }
    }
}
