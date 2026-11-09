package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AssetSyncManager
import com.example.data.repository.DownloadProgressState
import com.example.data.repository.LabRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenRoute {
    HOME,
    TESTS_ENCYCLOPEDIA,
    TEST_DETAIL,
    TUBES_GUIDE,
    REFERENCE_RANGES,
    CALCULATOR,
    MEDICAL_SECTIONS,
    IMAGE_LIBRARY,
    IMAGE_VIEWER,
    GLOBAL_MEDICAL_LIBRARY,
    WEBVIEW_VIEWER,
    PDF_LIBRARY,
    PDF_READER,
    AI_RESEARCH,
    GLOBAL_SEARCH,
    SETTINGS,
    FAVORITES,
    BLOOD_TYPES_3D,
    TUBES_3D
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val labRepository = LabRepository(application)
    val syncManager = AssetSyncManager(application)
    val userPreferencesRepository = com.example.data.repository.UserPreferencesRepository(application)

    private val _geminiApiKey = MutableStateFlow(userPreferencesRepository.getGeminiApiKey())
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _currentRoute = MutableStateFlow(ScreenRoute.HOME)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    private val _selectedTestId = MutableStateFlow<String?>(null)
    val selectedTestId: StateFlow<String?> = _selectedTestId.asStateFlow()

    private val _selectedImageId = MutableStateFlow<String?>(null)
    val selectedImageId: StateFlow<String?> = _selectedImageId.asStateFlow()

    private val _selectedWebUrl = MutableStateFlow("https://www.cdc.gov/dpdx")
    val selectedWebUrl: StateFlow<String> = _selectedWebUrl.asStateFlow()

    private val _selectedBookId = MutableStateFlow<String?>(null)
    val selectedBookId: StateFlow<String?> = _selectedBookId.asStateFlow()

    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    private val _storageInfo = MutableStateFlow<Map<String, Long>>(emptyMap())
    val storageInfo: StateFlow<Map<String, Long>> = _storageInfo.asStateFlow()

    val downloadState: StateFlow<DownloadProgressState> = syncManager.downloadState

    init {
        refreshStorageInfo()
    }

    fun navigateTo(route: ScreenRoute) {
        _currentRoute.value = route
    }

    fun navigateToTestDetail(testId: String) {
        _selectedTestId.value = testId
        _currentRoute.value = ScreenRoute.TEST_DETAIL
    }

    fun navigateToImageViewer(imageId: String) {
        _selectedImageId.value = imageId
        _currentRoute.value = ScreenRoute.IMAGE_VIEWER
    }

    fun navigateToWebView(url: String) {
        _selectedWebUrl.value = url
        _currentRoute.value = ScreenRoute.WEBVIEW_VIEWER
    }

    fun navigateToPdfReader(bookId: String) {
        _selectedBookId.value = bookId
        _currentRoute.value = ScreenRoute.PDF_READER
    }

    fun updateGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
        if (query.isNotBlank() && _currentRoute.value != ScreenRoute.GLOBAL_SEARCH) {
            _currentRoute.value = ScreenRoute.GLOBAL_SEARCH
        }
    }

    fun startImagePackageDownload() {
        viewModelScope.launch {
            syncManager.downloadAllApplicationImages()
            refreshStorageInfo()
        }
    }

    fun cancelImageDownload() {
        syncManager.cancelDownload()
    }

    fun clearImages() {
        viewModelScope.launch {
            syncManager.clearDownloadedImages()
            refreshStorageInfo()
        }
    }

    fun refreshStorageInfo() {
        viewModelScope.launch {
            _storageInfo.value = syncManager.getStorageUsageInfo()
        }
    }

    fun saveGeminiApiKey(key: String) {
        userPreferencesRepository.saveGeminiApiKey(key)
        _geminiApiKey.value = key.trim()
    }

    fun clearGeminiApiKey() {
        userPreferencesRepository.clearGeminiApiKey()
        _geminiApiKey.value = ""
    }
}
