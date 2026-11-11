package id.noto.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Room aplikasi (file "noto.db"). Satu objek untuk seluruh aplikasi, diambil lewat [ambil].
 * Kalau struktur tabel berubah, naikkan [version] dan tambahkan migrasi (skema lama ada di app/schemas).
 */
@Database(entities = [Aturan::class, AturanAplikasi::class, Pemakaian::class], version = 1, exportSchema = true)
abstract class NotoDatabase : RoomDatabase() {
    abstract fun aturanDao(): AturanDao
    abstract fun pemakaianDao(): PemakaianDao

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
