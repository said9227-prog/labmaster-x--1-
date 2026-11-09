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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReferenceRange
import com.example.ui.theme.*
import com.example.ui.viewmodel.LabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceRangesScreen(
    labViewModel: LabViewModel,
    onBack: () -> Unit
) {
    val ranges by labViewModel.allRanges.collectAsState()
    val calcResult by labViewModel.calcResult.collectAsState()

    var testIdInput by remember { mutableStateOf("cbc") }
    var valueInput by remember { mutableStateOf("14.0") }
    var unitInput by remember { mutableStateOf("g/dL") }
    var genderInput by remember { mutableStateOf("Male") }
    var ageInput by remember { mutableStateOf("25") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("القيم المرجعية ومفسر النتائج (Calculator)", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive Lab Reference Calculator Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lab_calculator_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حاسبة ومفسر النتائج (Lab Reference Calculator)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("اختر الفحص الطبي:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "cbc" to "الهيموجلوبين (HGB)",
                            "fbg" to "سكر الصائم (FBG)",
                            "creatinine" to "الكرياتينين (Cr)",
                            "tsh" to "هرمون (TSH)"
                        ).forEach { (id, label) ->
                            FilterChip(
                                selected = testIdInput == id,
                                onClick = {
                                    testIdInput = id
                                    unitInput = when(id) {
                                        "fbg" -> "mg/dL"
                                        "creatinine" -> "mg/dL"
                                        "tsh" -> "µIU/mL"
                                        else -> "g/dL"
                                    }
                                },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = valueInput,
                            onValueChange = { valueInput = it },
                            label = { Text("نتيجة التحليل") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_value_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = unitInput,
                            onValueChange = { unitInput = it },
                            label = { Text("الوحدة") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it },
                            label = { Text("العمر (سنة)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = genderInput == "Male",
                                onClick = { genderInput = "Male" },
                                label = { Text("ذكر") }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FilterChip(
                                selected = genderInput == "Female",
                                onClick = { genderInput = "Female" },
                                label = { Text("أنثى") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            labViewModel.setCalculatorParams(
                                testId = testIdInput,
                                valStr = valueInput,
                                unit = unitInput,
                                gender = genderInput,
                                age = ageInput
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculate_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تفسير ومقارنة النتيجة")
                    }

                    // Display Result Box
                    calcResult?.let { res ->
                        Spacer(modifier = Modifier.height(16.dp))
                        val flagColor = when (res.flag) {
                            "NORMAL" -> MedicalEmerald400
                            "LOW" -> Color(0xFF3B82F6)
                            "HIGH" -> Color(0xFFF59E0B)
                            else -> MedicalCoral400
                        }

                        Surface(
                            color = flagColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, flagColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "حالة النتيجة: ${res.flag}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = flagColor
                                    )
                                    Text(
                                        text = "المجال: ${res.referenceMin} - ${res.referenceMax} ${res.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(res.clinicalNoteAr, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("المصدر: ${res.source}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Reference Ranges Database List
            Text(
                text = "قاعدة بيانات القيم المرجعية الطبيعية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ranges.forEach { range ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(range.testNameAr, fontWeight = FontWeight.Bold)
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "${range.minVal} - ${range.maxVal} ${range.unit}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الفئة: ${range.gender} - ${range.ageGroup}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("طريقة الجهاز: ${range.methodOrEquipment}", style = MaterialTheme.typography.bodySmall)
                        if (range.notes.isNotBlank()) {
                            Text("ملاحظات: ${range.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
