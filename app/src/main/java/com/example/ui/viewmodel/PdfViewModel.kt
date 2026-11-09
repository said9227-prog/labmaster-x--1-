package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.PdfBookItem
import com.example.data.model.PdfBookmarkItem
import com.example.data.repository.PdfRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PdfViewModel(application: Application) : AndroidViewModel(application) {
    val repository = PdfRepository(application)

    val allBooks: StateFlow<List<PdfBookItem>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val filteredBooks: StateFlow<List<PdfBookItem>> = combine(
        allBooks, searchQuery, selectedCategory
    ) { books, query, category ->
        books.filter { b ->
            val matchCat = category == null || b.category.equals(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                    b.title.contains(query, ignoreCase = true) ||
                    b.author.contains(query, ignoreCase = true)
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _importStatus = MutableStateFlow<String?>(null)
    val importStatus: StateFlow<String?> = _importStatus.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun importPdf(uri: Uri, customTitle: String?, category: String) {
        viewModelScope.launch {
            _importStatus.value = "جاري استيراد كتاب الـ PDF..."
            val book = repository.importPdfFromUri(uri, customTitle, category)
            if (book != null) {
                _importStatus.value = "تمت إضافة '${book.title}' بنجاح إلى المكتبة!"
            } else {
                _importStatus.value = "تعذر استيراد الملف. يرجى التأكد من أن الملف صيغة PDF صالحة."
            }
        }
    }

    fun updateReadingProgress(bookId: String, page: Int) {
        viewModelScope.launch {
            repository.updateBookProgress(bookId, page)
        }
    }

    fun toggleFavorite(book: PdfBookItem) {
        viewModelScope.launch {
            repository.toggleFavorite(book)
        }
    }

    fun deleteBook(book: PdfBookItem) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    fun addBookmark(bookId: String, page: Int, note: String) {
        viewModelScope.launch {
            repository.addBookmark(bookId, page, note)
        }
    }

    fun getBookmarks(bookId: String): Flow<List<PdfBookmarkItem>> = repository.getBookmarksForBook(bookId)
}
