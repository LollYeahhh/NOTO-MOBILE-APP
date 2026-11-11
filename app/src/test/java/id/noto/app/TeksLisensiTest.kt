package id.noto.app

import id.noto.app.util.rapikanTeksLisensi
import org.junit.Assert.assertEquals
import org.junit.Test

class TeksLisensiTest {
    @Test
    fun barisDalamParagrafDisambung() {
        assertEquals("Satu dua tiga.\n\nEmpat.", rapikanTeksLisensi("  Satu dua\n  tiga.\n\n\nEmpat.\n"))
    }

    @Test
    fun judulKapitalTetapSendiriDanGarisDibuang() {
        val asli = "-----\nSIL OPEN FONT LICENSE Version 1.1\n-----\n\nPREAMBLE\nThe goals of\nthe OFL.\n\nTHE FONT SOFTWARE IS PROVIDED \"AS IS\", WITHOUT\nWARRANTY."
        assertEquals(
            "SIL OPEN FONT LICENSE Version 1.1\n\nPREAMBLE\nThe goals of the OFL.\n\nTHE FONT SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY.",
            rapikanTeksLisensi(asli),
        )
    }
}
