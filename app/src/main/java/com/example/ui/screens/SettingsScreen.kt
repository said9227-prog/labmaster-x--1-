package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    val downloadState by mainViewModel.downloadState.collectAsState()
    val storageInfo by mainViewModel.storageInfo.collectAsState()
    val savedApiKey by mainViewModel.geminiApiKey.collectAsState()

    var inputApiKey by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(mainViewModel.userPreferencesRepository.isDarkThemeDefault()) }
    var language by remember { mutableStateOf("العربية (RTL)") }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات التطبيق والمحتوى (Settings)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            if (showSavedSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSavedSnackbar = false }) {
                            Text("تم", color = Color.White)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("تم حفظ مفتاح Gemini AI بنجاح!")
                }
            }
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
            // Section 0: Gemini AI API Key Configuration
            SettingsCard(title = "مفتاح الذكاء الاصطناعي (Gemini AI API Key)", icon = Icons.Default.Key) {
                Text(
                    text = "أدخل مفتاح API الخاص بك لخدمة Google Gemini لاستخدام الباحث الطبي الذكي ومساعد التحاليل المتقدم.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputApiKey,
                    onValueChange = { inputApiKey = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_api_key_input"),
                    label = { Text("Gemini API Key (AIStudio / Google AI)") },
                    placeholder = { Text("AIzaSy...") },
                    singleLine = true,
                    visualTransformation = if (isKeyVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                            Icon(
                                imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (savedApiKey.isNotBlank()) {
                    Text(
                        text = "✓ مفتاح مخصص محمي ومحفوظ محلياً (${savedApiKey.take(6)}...${savedApiKey.takeLast(4)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "ℹ لا يوجد مفتاح مخصص. يتم استخدام المزود الافتراضي المحفوظ أو الوضع الهجين.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            mainViewModel.saveGeminiApiKey(inputApiKey)
                            showSavedSnackbar = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_gemini_api_key_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ المفتاح")
                    }

                    if (savedApiKey.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                mainViewModel.clearGeminiApiKey()
                                inputApiKey = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مسح")
                        }
                    }
                }
            }
            // Section 1: Image Library Package Download Engine
            SettingsCard(title = "تحميل صور التطبيق – Download Application Images", icon = Icons.Default.CloudDownload) {
                Text("تحميل كامل مكتبة أطلس الصور المجهرية والشرائح للعمل 100% دون إنترنت.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("حجم مكتبة الصور:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text("25.4 MB (~15 صورة عالية الدقة)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("عدد الصور المحملة:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text("${downloadState.downloadedFiles} / ${downloadState.totalFiles.coerceAtLeast(15)}", style = MaterialTheme.typography.labelMedium)
                }

                if (downloadState.isDownloading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { downloadState.downloadedFiles.toFloat() / downloadState.totalFiles.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "سرعة التنزيل: ${downloadState.downloadSpeedKbs.toInt()} KB/s - ${downloadState.statusMessage}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { mainViewModel.startImagePackageDownload() },
                        enabled = !downloadState.isDownloading,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_images_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تحميل الصور")
                    }

                    OutlinedButton(
                        onClick = { mainViewModel.clearImages() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف الصور")
                    }
                }
            }

            // Section 2: Medical Database Sync & Updates
            SettingsCard(title = "قاعدة المعلومات والمحتوى العلمي", icon = Icons.Default.Storage) {
                Text("حالة قاعدة البيانات المحلية: محققة وجاهزة لـ 100+ فحص طبي وأنبيب السحب.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(6.dp))
                Text("إصدار المحتوى: LabMaster X DB v1.0.4", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { mainViewModel.refreshStorageInfo() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("فحص وتحديث قاعدة البيانات من السيرفر")
                }
            }

            // Section 3: Storage Breakdown
            SettingsCard(title = "إدارة التخزين والذاكرة", icon = Icons.Default.SdCard) {
                val dbKb = (storageInfo["database"] ?: 0L) / 1024
                val imgKb = (storageInfo["images"] ?: 0L) / 1024
                val pdfKb = (storageInfo["pdf_books"] ?: 0L) / 1024

                Text("مساحة قاعدة البيانات: ${dbKb} KB", style = MaterialTheme.typography.bodySmall)
                Text("مساحة الصور الطبية المحملة: ${imgKb} KB", style = MaterialTheme.typography.bodySmall)
                Text("مساحة كتب الـ PDF المستوردة: ${pdfKb} KB", style = MaterialTheme.typography.bodySmall)
            }

            // Section 4: Interface & Appearance Settings
            SettingsCard(title = "الواجهة واللغة (UI & Localization)", icon = Icons.Default.Palette) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الوضع الداكن (Medical Dark Mode):", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = {
                            isDarkTheme = it
                            mainViewModel.userPreferencesRepository.setDarkTheme(it)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("لغة التطبيق:", style = MaterialTheme.typography.bodySmall)
                    Text(language, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Section 5: About & Licenses
            SettingsCard(title = "حول التطبيق والتراخيص الطبية", icon = Icons.Default.Info) {
                Text("LabMaster X – VIP Medical Intelligence & Laboratory Companion", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text("تطبيق موسوعي متكامل لفنيي المختبرات والأطباء والطلاب، مصمم وفق معايير CLSI, WHO, CDC, Mayo Clinic Labs.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(6.dp))
                Text("الإصدار: 1.0.0 (Build 2026)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SettingsCard(
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
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
