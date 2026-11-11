package id.noto.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.noto.app.R
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoChip
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam

/**
 * "Setelan - Notifikasi & Pengingat" (node 3147). Toggle dan chip langsung mengubah [setelan];
 * baris Ringkasan harian membuka Sheet Jam ([onBukaSheet]).
 */
@Composable
fun NotifikasiScreen(
    setelan: SetelanNoto,
    onUbah: (SetelanNoto) -> Unit,
    onKembali: () -> Unit,
    onBukaSheet: (SheetSetelan) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.notifikasi_pengingat), onIkonKiri = onKembali)
        IsiSetelan {
            SeksiSetelan(
                R.string.seksi_selama_jadwal,
                bawah = {
                    PanelInfoSetelan(
                        R.drawable.ic_lonceng,
                        stringResource(R.string.contoh_pengingat, formatDurasi(pilihanSisaMenit.first()), stringResource(R.string.contoh_nama_aturan)),
                        stringResource(R.string.contoh_pengingat_isi),
                    )
                },
            ) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jam_pasir),
                    stringResource(R.string.pengingat_sisa_kuota),
                    keterangan = stringResource(R.string.pengingat_sisa_kuota_ket),
                    toggle = setelan.pengingatSisa,
                    onUbah = { onUbah(setelan.copy(pengingatSisa = it)) },
                )
                // Pilihan Waktu Pengingat: mulai sejajar teks baris (36), padding bawah 12, jarak 8.
                // Chip tetap tampil walau toggle mati (disepakati; desain keadaan mati belum ada).
                Row(Modifier.fillMaxWidth().padding(start = 36.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pilihanSisaMenit.forEach { m ->
                        val aktif = m in setelan.sisaMenit
                        NotoChip(
                            stringResource(R.string.sisa_menit, formatDurasi(m)),
                            aktif,
                            onKlik = { onUbah(setelan.copy(sisaMenit = if (aktif) setelan.sisaMenit - m else setelan.sisaMenit + m)) },
                        )
                    }
                }
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kunci),
                    stringResource(R.string.saat_kuota_habis),
                    keterangan = stringResource(R.string.saat_kuota_habis_ket),
                    toggle = setelan.saatKuotaHabis,
                    onUbah = { onUbah(setelan.copy(saatKuotaHabis = it)) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kalender),
                    stringResource(R.string.saat_jadwal),
                    keterangan = stringResource(R.string.saat_jadwal_ket),
                    toggle = setelan.saatJadwal,
                    onUbah = { onUbah(setelan.copy(saatJadwal = it)) },
                )
            }
            SeksiSetelan(R.string.seksi_ringkasan) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jam),
                    stringResource(R.string.ringkasan_harian),
                    keterangan = stringResource(R.string.ringkasan_harian_ket),
                    nilai = formatJam(setelan.ringkasanHarianMenit),
                    onKlik = { onBukaSheet(SheetSetelan.JamRingkasan) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kalender),
                    stringResource(R.string.ringkasan_mingguan),
                    keterangan = stringResource(R.string.ringkasan_mingguan_ket),
                    toggle = setelan.ringkasanMingguan,
                    onUbah = { onUbah(setelan.copy(ringkasanMingguan = it)) },
                )
            }
            SeksiSetelan(R.string.seksi_waktu_tenang) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_bulan),
                    stringResource(R.string.jam_tenang),
                    keterangan = stringResource(
                        R.string.jam_tenang_ket,
                        stringResource(R.string.rentang_singkat, formatJam(JAM_TENANG_MULAI), formatJam(JAM_TENANG_SELESAI)),
                    ),
                    toggle = setelan.jamTenang,
                    onUbah = { onUbah(setelan.copy(jamTenang = it)) },
                )
            }
        }
    }
}

@Preview(name = "Setelan - Notifikasi & Pengingat", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewNotifikasi() {
    NOTOAPPTheme { NotifikasiScreen(contohSetelan, {}, {}, {}) }
}
