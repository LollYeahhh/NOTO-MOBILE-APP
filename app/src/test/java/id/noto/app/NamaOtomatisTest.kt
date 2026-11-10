package id.noto.app

import id.noto.app.ui.blocks.namaOtomatis
import org.junit.Assert.assertEquals
import org.junit.Test

class NamaOtomatisTest {
    private fun nama(vararg terpakai: String) = namaOtomatis("Blokir", "Blokir %1\$d", terpakai.toList())

    @Test fun belumAdaYangDipakai() = assertEquals("Blokir", nama("Sosmed"))

    @Test fun dasarSudahDipakai() = assertEquals("Blokir 1", nama("Blokir"))

    @Test fun lewatiNomorYangDipakai() = assertEquals("Blokir 3", nama("blokir", "Blokir 1", "BLOKIR 2"))

    @Test fun celahDiisiDulu() = assertEquals("Blokir 1", nama("Blokir", "Blokir 2"))
}
