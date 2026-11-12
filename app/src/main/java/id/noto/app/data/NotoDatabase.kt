package id.noto.app.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Room aplikasi (file "noto.db"). Satu objek untuk seluruh aplikasi, diambil lewat [ambil].
 * Kalau struktur tabel berubah, naikkan [version] dan tambahkan migrasi (skema lama ada di app/schemas).
 */
@Database(
    entities = [Aturan::class, AturanAplikasi::class, Pemakaian::class, Kuota::class, Kejadian::class],
    version = 3,
    exportSchema = true,
    // Versi 2 menambah tabel kuota. Room membuat migrasinya sendiri dari skema di app/schemas;
    // data aturan dan pemakaian yang sudah ada tidak berubah. Versi 3 menambah tabel kejadian.
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
)
abstract class NotoDatabase : RoomDatabase() {
    abstract fun aturanDao(): AturanDao
    abstract fun pemakaianDao(): PemakaianDao
    abstract fun kuotaDao(): KuotaDao
    abstract fun kejadianDao(): KejadianDao

    companion object {
        @Volatile
        private var instance: NotoDatabase? = null

        fun ambil(context: Context): NotoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, "noto.db")
                    .build()
                    .also { instance = it }
            }
    }
}
