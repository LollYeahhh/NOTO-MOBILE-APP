package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBadge
import id.noto.app.ui.components.NotoBar24Jam
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoChipHari
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.components.NotoItemAplikasi
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeBadge
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft

// Tinggi Bottom Nav (114) yang menutupi bagian bawah layar.
private val TINGGI_NAV = 114.dp

private val namaHari = listOf(
    R.string.hari_sen, R.string.hari_sel, R.string.hari_rab, R.string.hari_kam,
    R.string.hari_jum, R.string.hari_sab, R.string.hari_min,
)

/**
 * Blokir - Detail Aturan (node 2030): Header Halaman + ikon pensil di kanan, lalu konten bergulir
 * (padding 24/16, jarak antarkartu 16).
 * shortcut: pensil, "Edit aturan" (ke Edit Aturan, 3C) dan "Hapus aturan" (Sheet Hapus, 3D) belum dipasang.
 */
@Composable
fun DetailAturanScreen(onKembali: () -> Unit, detail: DetailAturan = contohDetailSosmed) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        Box {
            NotoHeaderHalaman(judul = detail.nama, onIkonKiri = onKembali)
            Image(
                painterResource(R.drawable.ic_pensil),
                contentDescription = stringResource(R.string.edit_aturan),
                modifier = Modifier.offset(x = 353.dp, y = 72.dp).size(24.dp),
            )
        }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = TINGGI_NAV + 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            KartuSisaKuota(detail)
            KartuJadwal(detail)
            KartuPemakaian(detail)
            KartuPengaturan()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NotoTombol(stringResource(R.string.edit_aturan), onKlik = {}, modifier = Modifier.weight(1f), tipe = TipeTombol.Permukaan)
                NotoTombol(stringResource(R.string.hapus_aturan), onKlik = {}, modifier = Modifier.weight(1f), tipe = TipeTombol.Teks)
            }
        }
    }
}

/** Kartu Permukaan sudut 16; padding dan jarak antarisi berbeda per kartu. */
@Composable
private fun Kartu(
    modifier: Modifier = Modifier.padding(16.dp),
    jarak: Int = 12,
    isi: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(jarak.dp),
        content = isi,
    )
}

/** Judul NOTO/Judul Kartu + subjudul NOTO/Teks, jarak 2. */
@Composable
private fun JudulKartu(judul: String, subjudul: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(judul, style = NotoTeks.JudulKartu, color = NotoWarna.TeksUtama)
        Text(subjudul, Modifier.tinggiBarisFigCraft(NotoTeks.Teks), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
    }
}

/** Kartu Sisa Kuota (node 2032). */
@Composable
private fun KartuSisaKuota(d: DetailAturan) {
    Kartu {
        Row(Modifier.fillMaxWidth().height(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.sisa_kuota_hari_ini).uppercase(), style = NotoTeks.Eyebrow, color = NotoWarna.TeksSekunder)
            NotoBadge(d.labelStatus, TipeBadge.Aktif)
        }
        // Angka besar dan "dari …" sejajar garis dasar, jarak 6.
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(d.sisa, Modifier.alignByBaseline().tinggiBarisFigCraft(NotoTeks.AngkaHero), style = NotoTeks.AngkaHero, color = NotoWarna.Primer)
            Text(stringResource(R.string.dari_kuota, d.kuota), Modifier.alignByBaseline(), style = NotoTeks.Teks, color = NotoWarna.TeksSekunder)
        }
        Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(NotoWarna.InfoLatar)) {
            Box(Modifier.fillMaxWidth(d.progres).fillMaxHeight().background(NotoWarna.PrimerMuda, CircleShape))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.terpakai_dibuka, d.terpakai, d.dibuka), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
            Text(stringResource(R.string.bebas_lagi_dalam, d.bebasLagi), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
        // Panel Info: putih sudut 12, padding 12, ikon info 24 + teks, jarak 8.
        Row(
            Modifier.fillMaxWidth().background(NotoWarna.Putih, RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.ic_info), null, Modifier.size(24.dp))
            Text(stringResource(R.string.info_waktu_dihitung, d.jamReset), Modifier.weight(1f), style = NotoTeks.KecilTeks, color = NotoWarna.TeksSekunder)
        }
    }
}

/** Kartu Jadwal (node 2049): judul, tujuh Chip Hari (tidak bisa diubah di sini), Bar 24 Jam. */
@Composable
private fun KartuJadwal(d: DetailAturan) {
    Kartu {
        JudulKartu(stringResource(R.string.jadwal), stringResource(R.string.dibatasi_jam, d.jam))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            namaHari.forEachIndexed { i, hari ->
                NotoChipHari(stringResource(hari), aktif = i in d.hariAktif, onKlik = {})
            }
        }
        NotoBar24Jam(d.mulaiMenit, d.selesaiMenit, sekarangMenit = d.sekarangMenit)
    }
}

/** Kartu Pemakaian (node 2085): judul + Item Aplikasi Tipe=Statistik. */
@Composable
private fun KartuPemakaian(d: DetailAturan) {
    Kartu {
        JudulKartu(stringResource(R.string.pemakaian_hari_ini), stringResource(R.string.kuota_dipakai_bersama, d.aplikasi.size))
        Column {
            d.aplikasi.forEach { app ->
                NotoItemAplikasi(
                    logo = painterResource(app.logo),
                    nama = app.nama,
                    keterangan = stringResource(R.string.dibuka_kali, app.dibuka),
                    durasi = app.durasi,
                )
            }
        }
    }
}

/** Kartu Pengaturan (node 2158): padding 4/16, dua Baris Setelan dengan toggle (belum disimpan, Tahap 4). */
@Composable
private fun KartuPengaturan() {
    Kartu(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), jarak = 0) {
        var aktif by remember { mutableStateOf(true) }
        var senyap by remember { mutableStateOf(true) }
        NotoBarisSetelan(
            painterResource(R.drawable.ic_jeda),
            stringResource(R.string.aturan_aktif),
            keterangan = stringResource(R.string.aturan_aktif_ket),
            toggle = aktif,
            onUbah = { aktif = it },
        )
        NotoBarisSetelan(
            painterResource(R.drawable.ic_lonceng),
            stringResource(R.string.senyapkan_notifikasi),
            keterangan = stringResource(R.string.senyapkan_notifikasi_ket),
            toggle = senyap,
            onUbah = { senyap = it },
        )
    }
}

@Preview(name = "Blokir - Detail Aturan", widthDp = 393, heightDp = 1150)
@Composable
private fun PreviewDetailAturan() {
    NOTOAPPTheme { DetailAturanScreen(onKembali = {}) }
}
