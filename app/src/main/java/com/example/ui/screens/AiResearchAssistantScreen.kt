package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.viewmodel.AiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiResearchAssistantScreen(
    aiViewModel: AiViewModel,
    onBack: () -> Unit,
    onOpenSettings: (() -> Unit)? = null
) {
    val promptInput by aiViewModel.promptInput.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()
    val currentResponse by aiViewModel.currentResponse.collectAsState()
    val history by aiViewModel.queryHistory.collectAsState()
    val savedApiKey by aiViewModel.savedApiKey.collectAsState()

    LaunchedEffect(Unit) {
        aiViewModel.refreshApiKeyStatus()
    }

    val suggestions = listOf(
        "ما المبدأ العلمي لفحص السكر التراكمي HbA1c؟",
        "ما ترتيب السحب لأنبوب EDTA وأنوب سترات الصوديوم؟",
        "كيف أفسر ارتفاع إنزيم ALT مع صفار العين؟",
        "ما هي أسباب الرفض الشائعة لعينة تجلط الدم PT/INR؟",
        "شرح أشكال طفيلي Giardia lamblia تحت المجهر"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مساعد البحث العلمي بالذكاء الاصطناعي", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { aiViewModel.clearHistory() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear History")
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
            // Status Card for Gemini API Key connection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (savedApiKey.isNotBlank()) Color(0xFF1B3828) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (savedApiKey.isNotBlank()) Icons.Default.Key else Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = if (savedApiKey.isNotBlank()) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (savedApiKey.isNotBlank()) "Gemini AI مخصّص ومفعّل ⚡" else "وضع المحتوى التجميعي المحقق ℹ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (savedApiKey.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (savedApiKey.isNotBlank()) "الإجابات تعمل فورياً عبر المفتاح الخاص بك في الإعدادات" else "أدخل مفتاح Gemini API في الإعدادات لإجابات حية ومفتوحة",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (savedApiKey.isNotBlank()) Color(0xFFC8E6C9) else Color.Gray
                            )
                        }
                    }

                    if (onOpenSettings != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onOpenSettings,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("الإعدادات", fontSize = 12.sp)
                        }
                    }
                }
            }
            // Prompt Card Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_research_input_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ادخل استفسارك الطبي والمختبري:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { aiViewModel.updatePrompt(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("ai_prompt_text_field"),
                        placeholder = { Text("مثال: اكتب عن تفاعلات الأجسام المضادة وتجنب التلوث في أنابيب التجلط...") },
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("اقتراحات سريعة:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(suggestions) { sug ->
                            SuggestionChip(
                                onClick = {
                                    aiViewModel.updatePrompt(sug)
                                    aiViewModel.submitResearchQuery(sug)
                                },
                                label = { Text(sug, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { aiViewModel.submitResearchQuery() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_ai_query_button"),
                        enabled = !isLoading && promptInput.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري البحث في المراجع الطبية المعتمدة...")
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("بدء البحث العلمي بالذكاء الاصطناعي")
                        }
                    }
                }
            }

            // Current Response Card
            currentResponse?.let { resp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("النتيجة والتخليص العلمي:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(resp, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                    }
                }
            }

            // History Section
            if (history.isNotEmpty()) {
                Text("سجل البحوث والاستفسارات السابقة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                history.forEach { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                aiViewModel.updatePrompt(record.prompt)
                                aiViewModel.submitResearchQuery(record.prompt)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(record.prompt, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(record.responseMarkdown, maxLines = 2, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
