package id.noto.app.service

/** Satu kejadian dari UsageStatsManager yang dipakai pemantau. */
sealed interface Peristiwa {
    /** Aplikasi [paket] tampil di layar. */
    data class Masuk(val paket: String) : Peristiwa

    /** Aplikasi [paket] tidak lagi di layar. */
    data class Keluar(val paket: String) : Peristiwa

    /** Layar mati: tidak ada aplikasi di layar. */
    data object LayarMati : Peristiwa
}

/**
 * Mengingat aplikasi yang sedang di layar dari urutan [Peristiwa].
 * Logika murni (tanpa Android) supaya bisa diuji di unit test.
 */
class PelacakLatar {
    /** Aplikasi yang sedang di layar, null kalau tidak ada (layar mati, atau belum tahu). */
    var diLayar: String? = null
        private set

    /** Aplikasi terakhir yang tampil, untuk membedakan "dibuka lagi" dari "kembali ke aplikasi yang sama". */
    private var terakhir: String? = null

    /**
     * Proses [peristiwa] berurutan. Mengembalikan aplikasi yang baru dibuka (satu kali per perpindahan
     * ke aplikasi lain). [hitungBuka] = false saat pemantau baru menyala: kejadian lama hanya dipakai
     * untuk mencari aplikasi di layar, tidak dihitung sebagai "dibuka".
     */
    fun proses(peristiwa: List<Peristiwa>, hitungBuka: Boolean = true): List<String> {
        val dibuka = mutableListOf<String>()
        for (p in peristiwa) {
            when (p) {
                is Peristiwa.Masuk -> {
                    if (hitungBuka && p.paket != terakhir) dibuka += p.paket
                    diLayar = p.paket
                    terakhir = p.paket
                }
                // Saat pindah aplikasi, "Keluar" aplikasi lama bisa datang setelah "Masuk" aplikasi baru.
                is Peristiwa.Keluar -> if (p.paket == diLayar) diLayar = null
                // Layar mati: membuka aplikasi yang sama setelah layar menyala dihitung dibuka lagi.
                Peristiwa.LayarMati -> {
                    diLayar = null
                    terakhir = null
                }
            }
        }
        return dibuka
    }
}
