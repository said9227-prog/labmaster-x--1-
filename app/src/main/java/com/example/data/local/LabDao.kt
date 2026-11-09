package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabTestDao {
    @Query("SELECT * FROM lab_tests ORDER BY nameEn ASC")
    fun getAllTests(): Flow<List<LabTest>>

    @Query("SELECT * FROM lab_tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: String): LabTest?

    @Query("SELECT * FROM lab_tests WHERE category = :category ORDER BY nameEn ASC")
    fun getTestsByCategory(category: String): Flow<List<LabTest>>

    @Query("SELECT * FROM lab_tests WHERE nameAr LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%' OR abbreviation LIKE '%' || :query || '%' OR synonyms LIKE '%' || :query || '%'")
    fun searchTests(query: String): Flow<List<LabTest>>

    @Query("SELECT * FROM lab_tests WHERE isFavorite = 1")
    fun getFavoriteTests(): Flow<List<LabTest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<LabTest>)

    @Update
    suspend fun updateTest(test: LabTest)
}

@Dao
interface BloodTubeDao {
    @Query("SELECT * FROM blood_tubes ORDER BY orderOfDraw ASC")
    fun getAllTubes(): Flow<List<BloodTube>>

    @Query("SELECT * FROM blood_tubes WHERE id = :id LIMIT 1")
    suspend fun getTubeById(id: String): BloodTube?

    @Query("SELECT * FROM blood_tubes WHERE colorNameAr LIKE '%' || :query || '%' OR colorNameEn LIKE '%' || :query || '%' OR additiveAr LIKE '%' || :query || '%' OR suitableTests LIKE '%' || :query || '%'")
    fun searchTubes(query: String): Flow<List<BloodTube>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTubes(tubes: List<BloodTube>)
}

@Dao
interface ReferenceRangeDao {
    @Query("SELECT * FROM reference_ranges ORDER BY testNameEn ASC")
    fun getAllRanges(): Flow<List<ReferenceRange>>

    @Query("SELECT * FROM reference_ranges WHERE testId = :testId OR testNameEn LIKE '%' || :testId || '%'")
    suspend fun getRangesForTest(testId: String): List<ReferenceRange>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRanges(ranges: List<ReferenceRange>)
}

@Dao
interface MedicalImageDao {
    @Query("SELECT * FROM medical_images ORDER BY titleEn ASC")
    fun getAllImages(): Flow<List<MedicalImageItem>>

    @Query("SELECT * FROM medical_images WHERE category = :category")
    fun getImagesByCategory(category: String): Flow<List<MedicalImageItem>>

    @Query("SELECT * FROM medical_images WHERE titleAr LIKE '%' || :query || '%' OR titleEn LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%'")
    fun searchImages(query: String): Flow<List<MedicalImageItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<MedicalImageItem>)

    @Update
    suspend fun updateImage(image: MedicalImageItem)

    @Query("DELETE FROM medical_images")
    suspend fun deleteAllImages()
}

@Dao
interface MedicalWebsiteDao {
    @Query("SELECT * FROM medical_websites ORDER BY titleEn ASC")
    fun getAllWebsites(): Flow<List<MedicalWebsiteItem>>

    @Query("SELECT * FROM medical_websites WHERE category = :category")
    fun getWebsitesByCategory(category: String): Flow<List<MedicalWebsiteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebsites(websites: List<MedicalWebsiteItem>)

    @Update
    suspend fun updateWebsite(website: MedicalWebsiteItem)
}

@Dao
interface PdfBookDao {
    @Query("SELECT * FROM pdf_books ORDER BY addedTimestamp DESC")
    fun getAllBooks(): Flow<List<PdfBookItem>>

    @Query("SELECT * FROM pdf_books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: String): PdfBookItem?

    @Query("SELECT * FROM pdf_books WHERE category = :category ORDER BY title ASC")
    fun getBooksByCategory(category: String): Flow<List<PdfBookItem>>

    @Query("SELECT * FROM pdf_books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<PdfBookItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: PdfBookItem)

    @Update
    suspend fun updateBook(book: PdfBookItem)

    @Delete
    suspend fun deleteBook(book: PdfBookItem)

    @Query("SELECT * FROM pdf_bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getBookmarksForBook(bookId: String): Flow<List<PdfBookmarkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: PdfBookmarkItem)

    @Delete
    suspend fun deleteBookmark(bookmark: PdfBookmarkItem)
}

@Dao
interface AiQueryDao {
    @Query("SELECT * FROM ai_query_history ORDER BY timestamp DESC")
    fun getAllQueries(): Flow<List<AiQueryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(record: AiQueryRecord)

    @Query("DELETE FROM ai_query_history")
    suspend fun clearHistory()
}
