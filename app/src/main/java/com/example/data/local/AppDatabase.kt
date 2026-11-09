package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        LabTest::class,
        BloodTube::class,
        ReferenceRange::class,
        MedicalImageItem::class,
        MedicalWebsiteItem::class,
        PdfBookItem::class,
        PdfBookmarkItem::class,
        AiQueryRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun labTestDao(): LabTestDao
    abstract fun bloodTubeDao(): BloodTubeDao
    abstract fun referenceRangeDao(): ReferenceRangeDao
    abstract fun medicalImageDao(): MedicalImageDao
    abstract fun medicalWebsiteDao(): MedicalWebsiteDao
    abstract fun pdfBookDao(): PdfBookDao
    abstract fun aiQueryDao(): AiQueryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labmaster_x_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getDatabase(context).seedInitialData()
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val tests = PrepopulatedData.getInitialLabTests()
        val tubes = PrepopulatedData.getInitialBloodTubes()
        val ranges = PrepopulatedData.getInitialReferenceRanges()
        val images = PrepopulatedData.getInitialMedicalImages()
        val websites = PrepopulatedData.getInitialMedicalWebsites()
        val sampleBooks = PrepopulatedData.getInitialPdfBooks()

        labTestDao().insertTests(tests)
        bloodTubeDao().insertTubes(tubes)
        referenceRangeDao().insertRanges(ranges)
        medicalImageDao().insertImages(images)
        medicalWebsiteDao().insertWebsites(websites)
        for (b in sampleBooks) {
            pdfBookDao().insertBook(b)
        }
    }
}
