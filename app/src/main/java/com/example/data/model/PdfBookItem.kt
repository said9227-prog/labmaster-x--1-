package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_books")
data class PdfBookItem(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String, // Hematology, Chemistry, Microbiology, etc.
    val localFilePath: String,
    val fileSize: Long,
    val pageCount: Int,
    val coverPath: String = "",
    val description: String = "",
    val isFavorite: Boolean = false,
    val lastReadPage: Int = 1,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pdf_bookmarks")
data class PdfBookmarkItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val pageNumber: Int,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
