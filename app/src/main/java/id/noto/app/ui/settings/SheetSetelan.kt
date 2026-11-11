package id.noto.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.blocks.SheetJadwal
import id.noto.app.ui.blocks.SheetJam
import id.noto.app.ui.blocks.SheetKuota
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoKepalaSheet
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoStepper
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna

/**
 * Semua sheet tab Setelan. Digambar di atas Bottom Nav (seperti overlay FigCraft yang menutupi nav),
 * jadi dipanggil di cangkang, bukan di dalam layar Setelan.
 * Jadwal bawaan, Kuota bawaan dan Ringkasan harian memakai sheet yang sama dengan form aturan.
 */
@Composable
fun LapisanSheetSetelan(
    sheet: SheetSetelan?,
    onSheet: (SheetSetelan?) -> Unit,
    setelan: SetelanNoto,
    onUbah: (SetelanNoto) -> Unit,
) {
    val tutup = { onSheet(null) }
    // Draf Jadwal bawaan; tetap ada selama sheet ditukar dengan Sheet Jam, diulang dari awal setelah ditutup.
    var draf by remember { mutableStateOf(setelan.jadwalBawaan) }
    var dariJam by remember { mutableStateOf(false) }
    LaunchedEffect(sheet) {
        if (sheet == null) { draf = setelan.jadwalBawaan; dariJam = false }
    }

    NotoSheet(tampil = sheet == SheetSetelan.Jadwal, onTutup = tutup, geserMasuk = !dariJam, scrimMasuk = !dariJam) {
        SheetJadwal(
            draf,
            onUbah = { draf = it },
            onPilihJam = { mulai -> onSheet(if (mulai) SheetSetelan.JamMulai else SheetSetelan.JamSelesai) },
            onTutup = tutup,
            onSimpan = { onUbah(setelan.copy(jadwalBawaan = it)); tutup() },
        )
    }
    // Sheet Jam: X, back, dan ketuk di luar kembali ke Atur Jadwal tanpa menyimpan jam.
    val kembaliKeJadwal = { dariJam = true; onSheet(SheetSetelan.Jadwal) }
    NotoSheet(tampil = sheet == SheetSetelan.JamMulai, onTutup = kembaliKeJadwal, scrimMasuk = false) {
        SheetJam(R.string.jam_mulai, R.string.jam_mulai_ket, draf.mulaiMenit, draf.selesaiMenit, kembaliKeJadwal) {
            draf = draf.copy(mulaiMenit = it); kembaliKeJadwal()
        }
    }
    NotoSheet(tampil = sheet == SheetSetelan.JamSelesai, onTutup = kembaliKeJadwal, scrimMasuk = false) {
        SheetJam(R.string.jam_selesai, R.string.jam_selesai_ket, draf.selesaiMenit, draf.mulaiMenit, kembaliKeJadwal) {
            draf = draf.copy(selesaiMenit = it); kembaliKeJadwal()
        }
    }
    NotoSheet(tampil = sheet == SheetSetelan.Kuota, onTutup = tutup) {
        SheetKuota(setelan.kuotaBawaanMenit, onTutup = tutup, onSimpan = { onUbah(setelan.copy(kuotaBawaanMenit = it)); tutup() })
    }
    NotoSheet(tampil = sheet == SheetSetelan.JamRingkasan, onTutup = tutup) {
        // Tidak ada jam lawan (bukan rentang), jadi semua jam boleh dipilih.
        SheetJam(R.string.ringkasan_harian, R.string.ringkasan_harian_ket, setelan.ringkasanHarianMenit, lawanMenit = -1, onTutup = tutup) {
            onUbah(setelan.copy(ringkasanHarianMenit = it)); tutup()
        }
    }
    NotoSheet(tampil = sheet == SheetSetelan.Jeda, onTutup = tutup) {
        SheetJeda(setelan.jedaDetik, onTutup = tutup, onSimpan = { onUbah(setelan.copy(jedaDetik = it)); tutup() })
    }
}

/** Langkah tombol −/+: per 10 detik sampai 1 menit, lalu per menit sampai maksimal 10 menit. */
private val LANGKAH_JEDA_DETIK = (0..50 step 10) + (60..600 step 60)

/** Pilihan cepat Sheet Jeda (FigCraft: Tanpa jeda, 10 detik, 30 detik, 1 menit). */
private val PILIHAN_JEDA_DETIK = listOf(0, 10, 30, 60)

/**
 * Isi "Overlay - Jeda Mematikan" (node 3735): Kepala Sheet, Stepper, Pilihan cepat, catatan,
 * lalu tombol Simpan. Perubahan baru dipakai setelah Simpan.
 */
@Composable
fun ColumnScope.SheetJeda(awalDetik: Int, onTutup: () -> Unit, onSimpan: (Int) -> Unit) {
    var i by remember { mutableIntStateOf(LANGKAH_JEDA_DETIK.indexOf(awalDetik).coerceAtLeast(0)) }
    val detik = LANGKAH_JEDA_DETIK[i]

    NotoKepalaSheet(stringResource(R.string.jeda_mematikan), stringResource(R.string.jeda_sheet_ket), onTutup)
    NotoStepper(
        teksJeda(detik),
        stringResource(R.string.jeda_maksimal),
        onKurangi = { i = (i - 1).coerceAtLeast(0) },
        onTambah = { i = (i + 1).coerceAtMost(LANGKAH_JEDA_DETIK.lastIndex) },
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
        PILIHAN_JEDA_DETIK.forEach { d -> NotoChip(teksJeda(d), aktif = detik == d, onKlik = { i = LANGKAH_JEDA_DETIK.indexOf(d) }) }
    }
    Text(stringResource(R.string.catatan_jeda), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
    NotoTombol(stringResource(R.string.simpan), onKlik = { onSimpan(detik) })
}

/** "Tanpa jeda", "10 detik", "1 menit" (detik ditulis lengkap, sesuai panduan teks). */
@Composable
internal fun teksJeda(detik: Int): String = when {
    detik == 0 -> stringResource(R.string.tanpa_jeda)
    detik < 60 -> pluralStringResource(R.plurals.jumlah_detik, detik, detik)
    else -> pluralStringResource(R.plurals.jumlah_menit, detik / 60, detik / 60)
}

@Preview(name = "Sheet Jeda", widthDp = 393)
@Composable
private fun PreviewSheetJeda() {
    NOTOAPPTheme { NotoSheet(tampil = true, onTutup = {}) { SheetJeda(10, {}, {}) } }
}
