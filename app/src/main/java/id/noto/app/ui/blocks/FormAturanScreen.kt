package id.noto.app.ui.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisForm
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.components.NotoInput
import id.noto.app.ui.components.NotoSheet
import id.noto.app.ui.components.NotoTombol
import id.noto.app.ui.components.TipeTombol
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoTeks
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.ui.theme.tinggiBarisFigCraft
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam
import id.noto.app.util.ringkasHari

/** Tambah = "Blokir - Tambah Aturan" (node 2231/3531), Edit = "Blokir - Edit Aturan" (node 2789). */
enum class ModeForm { Tambah, Edit }

/**
 * Sheet yang bisa terbuka di atas form: Atur Jadwal (overlay 3612), Atur Kuota (overlay 3678), dan
 * Jam Mulai / Jam Selesai (overlay 4528 / 4580) yang menggantikan Atur Jadwal sementara.
 */
enum class SheetForm { Jadwal, Kuota, JamMulai, JamSelesai, Hapus }

/**
 * Form aturan: Header Halaman, konten bergulir (padding 24/16, jarak antarseksi 24), lalu Bilah Aksi.
 * Tidak ada Bottom Nav di layar ini. Baris Jadwal dan Kuota membuka sheet; [sheetAwal] = sheet yang
 * sudah terbuka saat layar tampil (setelah Pilih Aplikasi, seperti FigCraft).
 * "Hapus aturan ini" (Edit) membuka Sheet Hapus Aturan; [onHapus] dipanggil setelah "Ya, hapus aturan".
 * [jadwalAwal] dan [kuotaAwal] = isi awal sheet selama form belum punya jadwal/kuota (bawaan dari Setelan).
 */
@Composable
fun FormAturanScreen(
    mode: ModeForm,
    form: FormAturan,
    onUbah: (FormAturan) -> Unit,
    onTutup: () -> Unit,
    onSimpan: () -> Unit,
    onPilihAplikasi: () -> Unit = {},
    onHapus: () -> Unit = {},
    namaTerpakai: List<String> = emptyList(),
    sheetAwal: SheetForm? = null,
    jadwalAwal: JadwalForm = jadwalBawaan,
    kuotaAwal: Int = KUOTA_BAWAAN_MENIT,
) {
    var sheet by remember { mutableStateOf(sheetAwal) }
    // Draf Sheet Atur Jadwal; tetap ada selama sheet ditukar dengan Sheet Jam.
    var drafJadwal by remember { mutableStateOf(form.jadwal ?: jadwalAwal) }
    // Sheet Jadwal yang kembali dari Sheet Jam tampil tanpa animasi (FigCraft: swap overlay instant).
    var dariJam by remember { mutableStateOf(false) }
    val tambah = mode == ModeForm.Tambah
    // Kolom nama boleh kosong; aturan lalu memakai nama otomatis (Blokir, Blokir 1, …).
    val nama = form.nama.ifBlank {
        namaOtomatis(stringResource(R.string.nama_otomatis), stringResource(R.string.nama_otomatis_nomor), namaTerpakai)
    }
    val hariSingkat = namaHariSingkat()
    val setiapHari = stringResource(R.string.setiap_hari)
    val polaRentang = stringResource(R.string.rentang_singkat)
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
            NotoHeaderHalaman(
                judul = stringResource(if (tambah) R.string.aturan_baru else R.string.edit_aturan_judul),
                onIkonKiri = onTutup,
                ikonKiri = if (tambah) R.drawable.ic_tutup else R.drawable.ic_kembali,
                deskripsiIkonKiri = stringResource(if (tambah) R.string.tutup else R.string.kembali),
            )
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Seksi(R.string.nama_aturan) {
                    NotoInput(form.nama, { onUbah(form.copy(nama = it)) }, placeholder = stringResource(R.string.nama_aturan_contoh))
                }
                Seksi(R.string.apa_yang_diatur) {
                    NotoBarisForm(
                        painterResource(R.drawable.ic_aplikasi),
                        stringResource(R.string.aplikasi),
                        if (form.aplikasi.isEmpty()) stringResource(R.string.pilih_aplikasi_ket) else form.aplikasi.joinToString(", "),
                        onKlik = onPilihAplikasi,
                    )
                }
                Seksi(R.string.kapan_berapa_lama) {
                    val jadwal = form.jadwal
                    NotoBarisForm(
                        painterResource(R.drawable.ic_kalender),
                        stringResource(R.string.jadwal),
                        if (jadwal == null) {
                            stringResource(R.string.jadwal_ket)
                        } else {
                            stringResource(
                                R.string.jadwal_ringkas,
                                ringkasHari(jadwal.hari, hariSingkat, setiapHari, polaRentang),
                                teksRentangJam(jadwal),
                            )
                        },
                        onKlik = { drafJadwal = form.jadwal ?: jadwalAwal; dariJam = false; sheet = SheetForm.Jadwal },
                    )
                    NotoBarisForm(
                        painterResource(R.drawable.ic_jam_pasir),
                        stringResource(R.string.kuota),
                        if (form.kuotaMenit == null) stringResource(R.string.kuota_ket) else stringResource(R.string.kuota_per_hari_aktif, formatDurasi(form.kuotaMenit)),
                        onKlik = { sheet = SheetForm.Kuota },
                    )
                }
                Seksi(R.string.saat_time_out) {
                    // Kartu Opsi: Permukaan sudut 16, padding 4/16.
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(NotoWarna.Permukaan, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        NotoBarisSetelan(
                            painterResource(R.drawable.ic_lonceng),
                            stringResource(R.string.senyapkan_notifikasi),
                            keterangan = stringResource(R.string.senyapkan_notifikasi_ket_form),
                            toggle = form.senyap,
                            onUbah = { onUbah(form.copy(senyap = it)) },
                        )
                    }
                }
                Pratinjau(form, nama)
                if (!tambah) {
                    NotoTombol(stringResource(R.string.hapus_aturan_ini), onKlik = { sheet = SheetForm.Hapus }, tipe = TipeTombol.Teks)
                }
            }
            BilahAksi {
                NotoTombol(
                    stringResource(if (tambah) R.string.simpan_aturan else R.string.simpan_perubahan),
                    onKlik = onSimpan,
                    aktif = form.lengkap,
                )
            }
        }
        NotoSheet(tampil = sheet == SheetForm.Jadwal, onTutup = { sheet = null }, geserMasuk = !dariJam, scrimMasuk = !dariJam) {
            SheetJadwal(
                drafJadwal,
                onUbah = { drafJadwal = it },
                onPilihJam = { mulai -> sheet = if (mulai) SheetForm.JamMulai else SheetForm.JamSelesai },
                onTutup = { sheet = null },
                onSimpan = { onUbah(form.copy(jadwal = it)); sheet = null },
            )
        }
        NotoSheet(tampil = sheet == SheetForm.Hapus, onTutup = { sheet = null }) {
            SheetHapus(nama, form.aplikasi.size, onTutup = { sheet = null }, onHapus = onHapus)
        }
        // Sheet Jam: X, back, dan ketuk di luar kembali ke Atur Jadwal tanpa menyimpan jam.
        val kembaliKeJadwal = { dariJam = true; sheet = SheetForm.Jadwal }
        NotoSheet(tampil = sheet == SheetForm.JamMulai, onTutup = kembaliKeJadwal, scrimMasuk = false) {
            SheetJam(R.string.jam_mulai, R.string.jam_mulai_ket, drafJadwal.mulaiMenit, drafJadwal.selesaiMenit, kembaliKeJadwal) {
                drafJadwal = drafJadwal.copy(mulaiMenit = it); kembaliKeJadwal()
            }
        }
        NotoSheet(tampil = sheet == SheetForm.JamSelesai, onTutup = kembaliKeJadwal, scrimMasuk = false) {
            SheetJam(R.string.jam_selesai, R.string.jam_selesai_ket, drafJadwal.selesaiMenit, drafJadwal.mulaiMenit, kembaliKeJadwal) {
                drafJadwal = drafJadwal.copy(selesaiMenit = it); kembaliKeJadwal()
            }
        }
        NotoSheet(tampil = sheet == SheetForm.Kuota, onTutup = { sheet = null }) {
            SheetKuota(form.kuotaMenit ?: kuotaAwal, onTutup = { sheet = null }, onSimpan = { onUbah(form.copy(kuotaMenit = it)); sheet = null })
        }
    }
}

/** "07.00–21.00" dari jam mulai dan selesai jadwal. */
@Composable
private fun teksRentangJam(j: JadwalForm) = stringResource(R.string.rentang_singkat, formatJam(j.mulaiMenit), formatJam(j.selesaiMenit))

/** Label seksi (NOTO/Label, teks sekunder) dan isinya, jarak 8. */
@Composable
private fun Seksi(label: Int, isi: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(label), Modifier.tinggiBarisFigCraft(NotoTeks.Label), style = NotoTeks.Label, color = NotoWarna.TeksSekunder)
        isi()
    }
}

/** Pratinjau Aturan: panel Biru pucet sudut 12, padding 12, ikon info biru 24 + teks, jarak 8. */
@Composable
private fun Pratinjau(form: FormAturan, nama: String) {
    val jadwal = form.jadwal
    val teks = if (form.lengkap && jadwal != null && form.kuotaMenit != null) {
        val hari = ringkasHari(
            jadwal.hari,
            namaHariPanjang(),
            stringResource(R.string.setiap_hari_kalimat),
            stringResource(R.string.rentang_hari_panjang),
        )
        stringResource(R.string.pratinjau_aturan, nama, formatDurasi(form.kuotaMenit), teksRentangJam(jadwal), hari)
    } else {
        stringResource(R.string.pratinjau_kosong)
    }
    Row(
        Modifier.fillMaxWidth().background(NotoWarna.InfoLatar, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(NotoWarna.Info),
        )
        Text(teks, Modifier.weight(1f), style = NotoTeks.KecilTeks, color = NotoWarna.Info)
    }
}

/**
 * Bilah Aksi: Putih, garis atas 1 px (Garis), 16 kiri-kanan, isi di tengah dengan jarak 8.
 * Bawaan (Tambah/Edit): padding 16 di bawah garis. Di FigCraft tingginya 99 (padding bawah 34)
 * mulai y754, jadi 1 dp terpotong tepi frame 852; yang terlihat hanya 98, maka bawah 33.
 */
@Composable
internal fun BilahAksi(atas: Dp = 17.dp, bawah: Dp = 33.dp, isi: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(NotoWarna.Putih)
            .drawBehind {
                drawLine(NotoWarna.Garis, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx())
            }
            .padding(start = 16.dp, end = 16.dp, top = atas, bottom = bawah),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = isi,
    )
}

@Preview(name = "Blokir - Tambah Aturan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTambahKosong() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Tambah, FormAturan(), {}, {}, {}) }
}

@Preview(name = "Blokir - Tambah Aturan Terisi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewTambahTerisi() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Tambah, contohFormTerisi, {}, {}, {}) }
}

@Preview(name = "Blokir - Edit Aturan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewEdit() {
    NOTOAPPTheme { FormAturanScreen(ModeForm.Edit, contohFormEdit, {}, {}, {}) }
}
