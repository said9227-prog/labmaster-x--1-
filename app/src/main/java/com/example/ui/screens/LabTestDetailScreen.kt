package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LabTest
import com.example.ui.components.DetailRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabTestDetailScreen(
    test: LabTest?,
    onBack: () -> Unit,
    onOpenCalculator: (String) -> Unit
) {
    if (test == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لم يتم العثور على تفاصيل الفحص.")
        }
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("نظرة عامة", "العينة والأنبوب", "خطوات الكشف والتحليل", "القيم والتفسير")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(test.nameAr, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${test.nameEn} (${test.abbreviation})", style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenCalculator(test.id) }) {
                        Icon(Icons.Default.Calculate, contentDescription = "Calculator")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> OverviewTab(test)
                    1 -> SpecimenTab(test)
                    2 -> ProcedureTab(test)
                    3 -> ReferenceInterpretationTab(test, onOpenCalculator)
                }
            }
        }
    }
}

@Composable
fun OverviewTab(test: LabTest) {
    DetailSectionCard(title = "الهدف من الفحص (Clinical Purpose)", icon = Icons.Default.Flag) {
        Text(test.purposeAr, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(6.dp))
        Text(test.purposeEn, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }

    DetailSectionCard(title = "المبدأ العلمي (Scientific Principle)", icon = Icons.Default.Biotech) {
        Text(test.scientificPrincipleAr, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(6.dp))
        Text(test.scientificPrincipleEn, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }

    DetailSectionCard(title = "المرادفات والأسماء البديلة (Synonyms)", icon = Icons.Default.Translate) {
        Text(test.synonyms, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SpecimenTab(test: LabTest) {
    DetailSectionCard(title = "نوع العينة والأنبوب (Specimen & Tube)", icon = Icons.Default.WaterDrop) {
        DetailRow("نوع العينة:", test.sampleType)
        DetailRow("الأنبوب المناسب:", test.tubeColor)
        DetailRow("المادة المضافة:", test.tubeAdditive)
        DetailRow("حجم العينة المطلوب:", test.sampleVolume)
    }

    DetailSectionCard(title = "طريقة السحب والحفظ (Collection & Stability)", icon = Icons.Default.Inventory) {
        DetailRow("طريقة السحب:", test.collectionMethod)
        DetailRow("شروط النقل:", test.transportConditions)
        DetailRow("ظروف الحفظ:", test.preservationConditions)
        DetailRow("مد استقرار العينة:", test.sampleStability)
    }

    DetailSectionCard(title = "أسباب رفض العينة (Rejection Criteria)", icon = Icons.Default.Warning) {
        Text(test.rejectionCriteria, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun ProcedureTab(test: LabTest) {
    DetailSectionCard(title = "خطوات إجراء الفحص (Procedure Steps)", icon = Icons.Default.ListAlt) {
        Text(test.procedureSteps, style = MaterialTheme.typography.bodyMedium)
    }

    DetailSectionCard(title = "الكواشف والأجهزة المستخدمة (Reagents & Equipment)", icon = Icons.Default.PrecisionManufacturing) {
        DetailRow("الكواشف المطلوبة:", test.requiredReagents)
        DetailRow("الأجهزة المستخدمة:", test.usedEquipment)
    }
}

@Composable
fun ReferenceInterpretationTab(test: LabTest, onOpenCalculator: (String) -> Unit) {
    DetailSectionCard(title = "موجز القيم المرجعية (Reference Ranges)", icon = Icons.Default.Analytics) {
        Text(test.referenceRangesSummary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onOpenCalculator(test.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("فتح محسب ومفسر النتائج التفاعلي")
        }
    }

    DetailSectionCard(title = "تفسير النتائج المرتفعة (High Result Interpretation)", icon = Icons.Default.ArrowUpward) {
        Text(test.highInterpretationAr, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(test.highInterpretationEn, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }

    DetailSectionCard(title = "تفسير النتائج المنخفضة (Low Result Interpretation)", icon = Icons.Default.ArrowDownward) {
        Text(test.lowInterpretationAr, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(test.lowInterpretationEn, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }

    DetailSectionCard(title = "العوامل المؤثرة والأخطاء الشائعة (Errors & Pitfalls)", icon = Icons.Default.ReportProblem) {
        DetailRow("العوامل المؤثرة:", test.affectingFactors)
        DetailRow("الأخطاء الشائعة:", test.commonErrors)
        DetailRow("المصادر الموثقة:", test.sources)
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
