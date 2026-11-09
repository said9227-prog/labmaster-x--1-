package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.LabMasterXTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LabMasterXTheme {
                val mainViewModel: MainViewModel = viewModel()
                val labViewModel: LabViewModel = viewModel()
                val pdfViewModel: PdfViewModel = viewModel()
                val aiViewModel: AiViewModel = viewModel()

                val currentRoute by mainViewModel.currentRoute.collectAsState()
                val selectedTestId by mainViewModel.selectedTestId.collectAsState()
                val selectedImageId by mainViewModel.selectedImageId.collectAsState()
                val selectedWebUrl by mainViewModel.selectedWebUrl.collectAsState()
                val selectedBookId by mainViewModel.selectedBookId.collectAsState()

                val allTests by labViewModel.allTests.collectAsState()
                val allTubes by labViewModel.allTubes.collectAsState()

                // Backhandler for secondary screens
                if (currentRoute != ScreenRoute.HOME) {
                    BackHandler {
                        mainViewModel.navigateTo(ScreenRoute.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != ScreenRoute.PDF_READER && currentRoute != ScreenRoute.WEBVIEW_VIEWER) {
                            NavigationBar(
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == ScreenRoute.HOME,
                                    onClick = { mainViewModel.navigateTo(ScreenRoute.HOME) },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("الرئيسية") },
                                    modifier = Modifier.testTag("nav_item_home")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == ScreenRoute.TESTS_ENCYCLOPEDIA,
                                    onClick = { mainViewModel.navigateTo(ScreenRoute.TESTS_ENCYCLOPEDIA) },
                                    icon = { Icon(Icons.Default.Science, contentDescription = "Encyclopedia") },
                                    label = { Text("الفحوصات") },
                                    modifier = Modifier.testTag("nav_item_encyclopedia")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == ScreenRoute.TUBES_GUIDE,
                                    onClick = { mainViewModel.navigateTo(ScreenRoute.TUBES_GUIDE) },
                                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Tubes") },
                                    label = { Text("الأنابيب") },
                                    modifier = Modifier.testTag("nav_item_tubes")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == ScreenRoute.AI_RESEARCH,
                                    onClick = { mainViewModel.navigateTo(ScreenRoute.AI_RESEARCH) },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant") },
                                    label = { Text("الذكاء الاصطناعي") },
                                    modifier = Modifier.testTag("nav_item_ai")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == ScreenRoute.SETTINGS,
                                    onClick = { mainViewModel.navigateTo(ScreenRoute.SETTINGS) },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("الإعدادات") },
                                    modifier = Modifier.testTag("nav_item_settings")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentRoute) {
                            ScreenRoute.HOME -> HomeScreen(
                                mainViewModel = mainViewModel,
                                labTests = allTests,
                                bloodTubes = allTubes,
                                onNavigate = { route -> mainViewModel.navigateTo(route) },
                                onTestClick = { id -> mainViewModel.navigateToTestDetail(id) }
                            )

                            ScreenRoute.TESTS_ENCYCLOPEDIA -> LabTestsEncyclopediaScreen(
                                labViewModel = labViewModel,
                                onTestClick = { id -> mainViewModel.navigateToTestDetail(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.TEST_DETAIL -> {
                                val testObj = allTests.firstOrNull { it.id == selectedTestId }
                                LabTestDetailScreen(
                                    test = testObj,
                                    onBack = { mainViewModel.navigateTo(ScreenRoute.TESTS_ENCYCLOPEDIA) },
                                    onOpenCalculator = { id ->
                                        labViewModel.setCalculatorParams(id, "14.0", "g/dL", "Male", "25")
                                        mainViewModel.navigateTo(ScreenRoute.REFERENCE_RANGES)
                                    }
                                )
                            }

                            ScreenRoute.TUBES_GUIDE -> BloodTubesGuideScreen(
                                labViewModel = labViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) },
                                onOpen3dView = { mainViewModel.navigateTo(ScreenRoute.TUBES_3D) }
                            )

                            ScreenRoute.REFERENCE_RANGES, ScreenRoute.CALCULATOR -> ReferenceRangesScreen(
                                labViewModel = labViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.MEDICAL_SECTIONS -> MedicalSectionsScreen(
                                onSectionClick = { cat ->
                                    labViewModel.setSelectedCategory(cat)
                                    mainViewModel.navigateTo(ScreenRoute.TESTS_ENCYCLOPEDIA)
                                },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.IMAGE_LIBRARY -> ImageLibraryScreen(
                                labViewModel = labViewModel,
                                mainViewModel = mainViewModel,
                                onImageClick = { id -> mainViewModel.navigateToImageViewer(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.IMAGE_VIEWER -> ImageViewerScreen(
                                imageId = selectedImageId,
                                labViewModel = labViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.IMAGE_LIBRARY) }
                            )

                            ScreenRoute.GLOBAL_MEDICAL_LIBRARY -> GlobalMedicalLibraryScreen(
                                labViewModel = labViewModel,
                                onOpenWebUrl = { url -> mainViewModel.navigateToWebView(url) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.WEBVIEW_VIEWER -> MedicalWebViewScreen(
                                url = selectedWebUrl,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.GLOBAL_MEDICAL_LIBRARY) }
                            )

                            ScreenRoute.PDF_LIBRARY -> PdfLibraryScreen(
                                pdfViewModel = pdfViewModel,
                                onOpenReader = { id -> mainViewModel.navigateToPdfReader(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.PDF_READER -> PdfReaderScreen(
                                bookId = selectedBookId,
                                pdfViewModel = pdfViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.PDF_LIBRARY) }
                            )

                            ScreenRoute.AI_RESEARCH -> AiResearchAssistantScreen(
                                aiViewModel = aiViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) },
                                onOpenSettings = { mainViewModel.navigateTo(ScreenRoute.SETTINGS) }
                            )

                            ScreenRoute.GLOBAL_SEARCH -> GlobalSearchScreen(
                                mainViewModel = mainViewModel,
                                labViewModel = labViewModel,
                                onTestClick = { id -> mainViewModel.navigateToTestDetail(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.SETTINGS -> SettingsScreen(
                                mainViewModel = mainViewModel,
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.FAVORITES -> FavoritesScreen(
                                labViewModel = labViewModel,
                                onTestClick = { id -> mainViewModel.navigateToTestDetail(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.BLOOD_TYPES_3D -> ThreeJsBloodTypesScreen(
                                onBack = { mainViewModel.navigateTo(ScreenRoute.HOME) }
                            )

                            ScreenRoute.TUBES_3D -> ThreeJsBloodTubesScreen(
                                onTestClick = { id -> mainViewModel.navigateToTestDetail(id) },
                                onBack = { mainViewModel.navigateTo(ScreenRoute.TUBES_GUIDE) }
                            )
                        }
                    }
                }
            }
        }
    }
}
