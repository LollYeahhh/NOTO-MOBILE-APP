package id.noto.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.noto.app.R
import id.noto.app.ui.blocks.namaHariSingkat
import id.noto.app.ui.components.NotoBarisSetelan
import id.noto.app.ui.components.NotoHeaderHalaman
import id.noto.app.ui.theme.NOTOAPPTheme
import id.noto.app.ui.theme.NotoWarna
import id.noto.app.util.formatDurasi
import id.noto.app.util.formatJam
import id.noto.app.util.ringkasHari

/**
 * "Setelan - Aturan Pembatasan" (node 3269). Jadwal bawaan, Kuota bawaan dan Jeda sebelum mematikan
 * membuka sheet ([onBukaSheet]); toggle langsung mengubah [setelan].
 */
@Composable
fun AturanPembatasanScreen(
    setelan: SetelanNoto,
    onUbah: (SetelanNoto) -> Unit,
    onKembali: () -> Unit,
    onBukaSheet: (SheetSetelan) -> Unit,
) {
    val jadwal = setelan.jadwalBawaan
    val polaRentang = stringResource(R.string.rentang_singkat)
    Column(Modifier.fillMaxSize().background(NotoWarna.Putih)) {
        NotoHeaderHalaman(stringResource(R.string.aturan_pembatasan), onIkonKiri = onKembali)
        IsiSetelan {
            SeksiSetelan(
                R.string.seksi_bawaan,
                bawah = { PanelInfoSetelan(R.drawable.ic_info, judul = null, isi = stringResource(R.string.info_reset_kuota)) },
            ) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kalender),
                    stringResource(R.string.jadwal_bawaan),
                    keterangan = stringResource(
                        R.string.jadwal_ringkas,
                        ringkasHari(jadwal.hari, namaHariSingkat(), stringResource(R.string.setiap_hari), polaRentang),
                        polaRentang.format(formatJam(jadwal.mulaiMenit), formatJam(jadwal.selesaiMenit)),
                    ),
                    onKlik = { onBukaSheet(SheetSetelan.Jadwal) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jam_pasir),
                    stringResource(R.string.kuota_bawaan),
                    keterangan = stringResource(R.string.kuota_bawaan_ket),
                    nilai = formatDurasi(setelan.kuotaBawaanMenit),
                    onKlik = { onBukaSheet(SheetSetelan.Kuota) },
                )
            }
            SeksiSetelan(R.string.seksi_mematikan) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jam),
                    stringResource(R.string.jeda_mematikan),
                    keterangan = stringResource(R.string.jeda_mematikan_ket),
                    nilai = teksJeda(setelan.jedaDetik),
                    onKlik = { onBukaSheet(SheetSetelan.Jeda) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_kunci),
                    stringResource(R.string.kunci_edit),
                    keterangan = stringResource(R.string.kunci_edit_ket),
                    toggle = setelan.kunciEdit,
                    onUbah = { onUbah(setelan.copy(kunciEdit = it)) },
                )
            }
            SeksiSetelan(R.string.seksi_layar_timeout) {
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jeda),
                    stringResource(R.string.latihan_napas),
                    keterangan = stringResource(R.string.latihan_napas_ket),
                    toggle = setelan.latihanNapas,
                    onUbah = { onUbah(setelan.copy(latihanNapas = it)) },
                )
                NotoBarisSetelan(
                    painterResource(R.drawable.ic_jam),
                    stringResource(R.string.tampilkan_sisa_waktu),
                    keterangan = stringResource(R.string.tampilkan_sisa_waktu_ket),
                    toggle = setelan.sisaWaktu,
                    onUbah = { onUbah(setelan.copy(sisaWaktu = it)) },
                )
            }
        }
    }
}

@Preview(name = "Setelan - Aturan Pembatasan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewAturanPembatasan() {
    NOTOAPPTheme { AturanPembatasanScreen(contohSetelan, {}, {}, {}) }
}
