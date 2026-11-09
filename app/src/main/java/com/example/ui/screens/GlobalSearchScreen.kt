package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.BloodTubeChip
import com.example.ui.components.LabTestHomeCard
import com.example.ui.viewmodel.LabViewModel
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    mainViewModel: MainViewModel,
    labViewModel: LabViewModel,
    onTestClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val query by mainViewModel.globalSearchQuery.collectAsState()
    val allTests by labViewModel.allTests.collectAsState()
    val allTubes by labViewModel.allTubes.collectAsState()

    val testResults = remember(query, allTests) {
        if (query.isBlank()) emptyList()
        else allTests.filter {
            it.nameAr.contains(query, true) ||
            it.nameEn.contains(query, true) ||
            it.abbreviation.contains(query, true) ||
            it.synonyms.contains(query, true)
        }
    }

    val tubeResults = remember(query, allTubes) {
        if (query.isBlank()) emptyList()
        else allTubes.filter {
            it.colorNameAr.contains(query, true) ||
            it.colorNameEn.contains(query, true) ||
            it.additiveAr.contains(query, true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("البحث الشامل والموحد", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { mainViewModel.updateGlobalSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input"),
                placeholder = { Text("اكتب أي كلمة للبحث الموحد بكل النوافذ...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Text("نتائج البحث للفحوصات (${testResults.size}):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(testResults) { test ->
                    LabTestHomeCard(test = test, onClick = { onTestClick(test.id) })
                }
                items(tubeResults) { tube ->
                    BloodTubeChip(tube = tube, onClick = {})
                }
            }
        }
    }
}
