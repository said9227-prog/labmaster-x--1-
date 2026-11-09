package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BloodTube
import com.example.ui.components.BloodTubeGraphic
import com.example.ui.components.TubeInfoRow
import com.example.ui.viewmodel.LabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodTubesGuideScreen(
    labViewModel: LabViewModel,
    onBack: () -> Unit,
    onOpen3dView: (() -> Unit)? = null
) {
    val tubes by labViewModel.allTubes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredTubes = remember(tubes, searchQuery) {
        if (searchQuery.isBlank()) tubes
        else tubes.filter { t ->
            t.colorNameAr.contains(searchQuery, true) ||
            t.colorNameEn.contains(searchQuery, true) ||
            t.additiveAr.contains(searchQuery, true) ||
            t.suitableTests.contains(searchQuery, true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل أنابيب سحب الدم (Order of Draw)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (onOpen3dView != null) {
                        IconButton(onClick = onOpen3dView) {
                            Icon(Icons.Default.ViewInAr, contentDescription = "3D View", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onOpen3dView != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpen3dView() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("فتح أطلس الأنابيب ثلاثي الأبعاد 3D", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("معاينة تفاعلية Three.js للأنابيب والفحوصات", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tube_search_input"),
                placeholder = { Text("ابحث باللون (أصفر، أرجواني)، المادة (EDTA) أو التحليل...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ترتيب السحب (Order of Draw) يمنع التلوث الخلطي بين مانعات التثبت كالبوتاسيوم EDTA الذي يخرب فحوصات السيروم.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTubes) { tube ->
                    BloodTubeDetailCard(tube = tube)
                }
            }
        }
    }
}

@Composable
fun BloodTubeDetailCard(tube: BloodTube) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tube_card_${tube.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BloodTubeGraphic(tube = tube, modifier = Modifier.size(46.dp, 100.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "${tube.colorNameAr} (${tube.colorNameEn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "الترتيب المعياري: #${tube.orderOfDraw} in Order of Draw",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${tube.mixingInversions} مزج",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            TubeInfoRow("المادة المضافة:", tube.additiveAr)
            TubeInfoRow("نوع العينة:", tube.specimenType)
            TubeInfoRow("الفحوصات المناسبة:", tube.suitableTests)
            TubeInfoRow("أسباب الاختيار:", tube.drawReasons)
            TubeInfoRow("مواصفات الطرد المركزي:", tube.centrifugationSpecs)

            if (tube.errorsIfWrongTube.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تحذير الأخطاء: ${tube.errorsIfWrongTube}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
