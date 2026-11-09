package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.BloodTube
import com.example.data.model.LabTest
import com.example.ui.components.BloodTubeChip
import com.example.ui.components.LabTestHomeCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    labTests: List<LabTest>,
    bloodTubes: List<BloodTube>,
    onNavigate: (ScreenRoute) -> Unit,
    onTestClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val downloadState by mainViewModel.downloadState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = "LabMaster Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LabMaster X",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "VIP Medical Intelligence Companion",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigate(ScreenRoute.SETTINGS) },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global Search Bar Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) {
                        mainViewModel.updateGlobalSearchQuery(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_input"),
                placeholder = { Text("بحث في التحاليل، الأنابيب، القيم، الكتب والمراجع...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )

            // Dynamic Asset Downloader Status Banner if running or available
            if (downloadState.isDownloading || downloadState.downloadedFiles > 0) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (downloadState.isDownloading) Icons.Default.CloudDownload else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (downloadState.isDownloading) "جاري تنزيل مكتبة الصور وقاعدة البيانات..." else "حزمة صور التطبيق جاهزة دون إنترنت",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (downloadState.isDownloading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { downloadState.downloadedFiles.toFloat() / downloadState.totalFiles.coerceAtLeast(1) },
                                modifier = Modifier.fillMaxWidth().clip(CircleShape)
                            )
                            Text(
                                text = downloadState.statusMessage,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // AI Scientific Assistant VIP Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(ScreenRoute.AI_RESEARCH) }
                    .testTag("ai_assistant_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(MedicalNavy800, MedicalTeal700)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MedicalTeal400.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "AI MEDICAL RESEARCH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MedicalCyan400,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "مساعد البحث العلمي بالذكاء الاصطناعي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "استفسر عن الفحوصات، المبادئ الكيميائية، ومقارنة الأجهزة مع التوثيق المرجعي",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = MedicalGold400,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }

            // 3D Three.js Interactive Medical Atlas Section
            Column {
                Text(
                    text = "الأطلس التفاعلي ثلاثي الأبعاد 3D (Three.js)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: 3D Blood Types Visualizer
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.BLOOD_TYPES_3D) }
                            .testTag("blood_types_3d_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "THREE.JS 3D",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "فصائل الدم 3D",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "نموذج 3D لكريات الدم والمستضدات ومصفوفة التبرع والمستقبل",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }

                    // Card 2: 3D Blood Tubes & Tests Visualizer
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.TUBES_3D) }
                            .testTag("tubes_3d_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "THREE.JS 3D",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "أنابيب السحب 3D",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "معاينة 3D واقعية للأنابيب وخصائصها والفحوصات المرتبطة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Quick Access Navigation Grid Shortcuts
            Text(
                text = "الأقسام والمكتبات الرئيسية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "موسوعة الفحوصات",
                    subtitle = "100+ فحص طبي",
                    icon = Icons.Default.Science,
                    color = MedicalTeal500,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_tests",
                    onClick = { onNavigate(ScreenRoute.TESTS_ENCYCLOPEDIA) }
                )
                QuickNavCard(
                    title = "أنابيب سحب الدم",
                    subtitle = "ترتيب السحب المعتمد",
                    icon = Icons.Default.WaterDrop,
                    color = TubePurple,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_tubes",
                    onClick = { onNavigate(ScreenRoute.TUBES_GUIDE) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "القيم المرجعية",
                    subtitle = "الحاسبة والمفسر",
                    icon = Icons.Default.Calculate,
                    color = MedicalCyan700,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_ranges",
                    onClick = { onNavigate(ScreenRoute.REFERENCE_RANGES) }
                )
                QuickNavCard(
                    title = "مكتبة الكتب PDF",
                    subtitle = "قارئ واستيراد الكتب",
                    icon = Icons.Default.MenuBook,
                    color = MedicalGold400,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_pdf",
                    onClick = { onNavigate(ScreenRoute.PDF_LIBRARY) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "المقاطع والمجهر",
                    subtitle = "مكتبة الصور والشرائح",
                    icon = Icons.Default.PhotoLibrary,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    testTag = "nav_images",
                    onClick = { onNavigate(ScreenRoute.IMAGE_LIBRARY) }
                )
                QuickNavCard(
                    title = "المراجع العالمية",
                    subtitle = "WHO, CDC, CLSI",
                    icon = Icons.Default.Language,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    testTag = "nav_web",
                    onClick = { onNavigate(ScreenRoute.GLOBAL_MEDICAL_LIBRARY) }
                )
            }

            // Order of Draw Quick Tube Carousel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "دليل سحب الدم (Order of Draw)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(ScreenRoute.TUBES_GUIDE) }) {
                    Text("عرض الكل")
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bloodTubes) { tube ->
                    BloodTubeChip(tube = tube, onClick = { onNavigate(ScreenRoute.TUBES_GUIDE) })
                }
            }

            // Featured Laboratory Tests Section
            Text(
                text = "الفحوصات الطبية المعتمدة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                labTests.take(4).forEach { test ->
                    LabTestHomeCard(test = test, onClick = { onTestClick(test.id) })
                }
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
