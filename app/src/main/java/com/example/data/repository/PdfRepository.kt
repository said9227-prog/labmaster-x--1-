package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.PdfBookItem
import com.example.data.model.PdfBookmarkItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class PdfRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)

    val allBooks: Flow<List<PdfBookItem>> = db.pdfBookDao().getAllBooks()

    fun getBooksByCategory(category: String): Flow<List<PdfBookItem>> =
        db.pdfBookDao().getBooksByCategory(category)

    fun searchBooks(query: String): Flow<List<PdfBookItem>> =
        db.pdfBookDao().searchBooks(query)

    suspend fun getBookById(id: String): PdfBookItem? = db.pdfBookDao().getBookById(id)

    suspend fun importPdfFromUri(
        uri: Uri,
        customTitle: String?,
        category: String
    ): PdfBookItem? = withContext(Dispatchers.IO) {
        try {
            val pdfDir = File(context.filesDir, "pdf_books").apply { if (!exists()) mkdirs() }
            val bookId = UUID.randomUUID().toString()
            val destFile = File(pdfDir, "book_$bookId.pdf")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            val fileName = customTitle?.ifBlank { null }
                ?: getFileNameFromUri(uri)
                ?: "Medical Book $bookId"

            val fileSize = destFile.length()
            val pageCount = try {
                val pfd = android.os.ParcelFileDescriptor.open(destFile, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = android.graphics.pdf.PdfRenderer(pfd)
                val count = renderer.pageCount
                renderer.close()
                pfd.close()
                count
            } catch (e: Exception) {
                1
            }

            val book = PdfBookItem(
                id = bookId,
                title = fileName,
                author = "Medical Reference",
                category = category,
                localFilePath = destFile.absolutePath,
                fileSize = fileSize,
                pageCount = pageCount,
                description = "استيراد ملف PDF محلي إلى مكتبة LabMaster X",
                addedTimestamp = System.currentTimeMillis()
            )

            db.pdfBookDao().insertBook(book)
            book
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    suspend fun updateBookProgress(bookId: String, page: Int) {
        val book = db.pdfBookDao().getBookById(bookId) ?: return
        db.pdfBookDao().updateBook(book.copy(lastReadPage = page))
    }

    suspend fun toggleFavorite(book: PdfBookItem) {
        db.pdfBookDao().updateBook(book.copy(isFavorite = !book.isFavorite))
    }

    suspend fun deleteBook(book: PdfBookItem) = withContext(Dispatchers.IO) {
        if (book.localFilePath.isNotBlank()) {
            val file = File(book.localFilePath)
            if (file.exists()) file.delete()
        }
        db.pdfBookDao().deleteBook(book)
    }

    fun getBookmarksForBook(bookId: String): Flow<List<PdfBookmarkItem>> =
        db.pdfBookDao().getBookmarksForBook(bookId)

    suspend fun addBookmark(bookId: String, pageNumber: Int, note: String) {
        db.pdfBookDao().insertBookmark(
            PdfBookmarkItem(bookId = bookId, pageNumber = pageNumber, note = note)
        )
    }

    suspend fun getTotalStorageBytes(): Long = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "pdf_books")
        if (!pdfDir.exists()) return@withContext 0L
        pdfDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
}
