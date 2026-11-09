package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiQueryRecord
import com.example.data.repository.AiResearchRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiResearchRepository(application)
    private val userPrefs = com.example.data.repository.UserPreferencesRepository(application)

    private val _savedApiKey = MutableStateFlow(userPrefs.getGeminiApiKey())
    val savedApiKey: StateFlow<String> = _savedApiKey.asStateFlow()

    val queryHistory: StateFlow<List<AiQueryRecord>> = repository.queryHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentResponse = MutableStateFlow<String?>(null)
    val currentResponse: StateFlow<String?> = _currentResponse.asStateFlow()

    fun refreshApiKeyStatus() {
        _savedApiKey.value = userPrefs.getGeminiApiKey()
    }

    fun updatePrompt(input: String) {
        _promptInput.value = input
    }

    fun submitResearchQuery(query: String? = null) {
        val q = query ?: _promptInput.value
        if (q.isBlank()) return

        _isLoading.value = true
        _currentResponse.value = null

        viewModelScope.launch {
            val response = repository.performScientificResearch(q)
            _currentResponse.value = response
            _isLoading.value = false
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
