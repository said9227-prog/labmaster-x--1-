package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.LabRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LabRepository(application)

    val allTests: StateFlow<List<LabTest>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTubes: StateFlow<List<BloodTube>> = repository.allTubes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRanges: StateFlow<List<ReferenceRange>> = repository.allRanges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allImages: StateFlow<List<MedicalImageItem>> = repository.allImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWebsites: StateFlow<List<MedicalWebsiteItem>> = repository.allWebsites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTests: StateFlow<List<LabTest>> = repository.favoriteTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val filteredTests: StateFlow<List<LabTest>> = combine(
        allTests, searchQuery, selectedCategory
    ) { tests, query, category ->
        tests.filter { t ->
            val matchCat = category == null || t.category.equals(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                    t.nameAr.contains(query, ignoreCase = true) ||
                    t.nameEn.contains(query, ignoreCase = true) ||
                    t.abbreviation.contains(query, ignoreCase = true) ||
                    t.synonyms.contains(query, ignoreCase = true)
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculator State
    private val _calcTestId = MutableStateFlow("cbc")
    val calcTestId: StateFlow<String> = _calcTestId.asStateFlow()

    private val _calcValue = MutableStateFlow("14.0")
    val calcValue: StateFlow<String> = _calcValue.asStateFlow()

    private val _calcUnit = MutableStateFlow("g/dL")
    val calcUnit: StateFlow<String> = _calcUnit.asStateFlow()

    private val _calcGender = MutableStateFlow("Male")
    val calcGender: StateFlow<String> = _calcGender.asStateFlow()

    private val _calcAge = MutableStateFlow("25")
    val calcAge: StateFlow<String> = _calcAge.asStateFlow()

    private val _calcResult = MutableStateFlow<LabRepository.CalculationResult?>(null)
    val calcResult: StateFlow<LabRepository.CalculationResult?> = _calcResult.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(test: LabTest) {
        viewModelScope.launch {
            repository.toggleFavorite(test)
        }
    }

    fun setCalculatorParams(testId: String, valStr: String, unit: String, gender: String, age: String) {
        _calcTestId.value = testId
        _calcValue.value = valStr
        _calcUnit.value = unit
        _calcGender.value = gender
        _calcAge.value = age
        calculateReferenceFlag()
    }

    fun calculateReferenceFlag() {
        val valDouble = _calcValue.value.toDoubleOrNull() ?: return
        val ageInt = _calcAge.value.toIntOrNull() ?: 25
        viewModelScope.launch {
            _calcResult.value = repository.calculateResultFlag(
                testId = _calcTestId.value,
                userValue = valDouble,
                unit = _calcUnit.value,
                gender = _calcGender.value,
                ageYears = ageInt
            )
        }
    }
}
