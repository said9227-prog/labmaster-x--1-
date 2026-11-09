package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ScientificSection(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSectionsScreen(
    onSectionClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val sections = listOf(
        ScientificSection("Hematology", "أمراض الدم", "Hematology", "CBC, Anemia, Smears, Leukemia, Coagulation", Icons.Default.WaterDrop, TubeRed),
        ScientificSection("Clinical Chemistry", "الكيمياء السريرية", "Clinical Chemistry", "Glucose, Renal, Liver, Lipids, Cardiac", Icons.Default.Science, MedicalTeal500),
        ScientificSection("Microbiology", "الأحياء الدقيقة", "Microbiology", "Bacteriology, Gram Stain, Culture, Antibiotics", Icons.Default.Biotech, Color(0xFF8B5CF6)),
        ScientificSection("Parasitology", "علم الطفيليات", "Parasitology", "Blood & Intestinal Parasites, Microscopy", Icons.Default.BugReport, Color(0xFFD97706)),
        ScientificSection("Immunology", "المناعة والأمصال", "Immunology & Serology", "Antigens, Antibodies, Autoimmune, Serology", Icons.Default.Shield, MedicalCyan700),
        ScientificSection("Hormones", "الهرمونات", "Hormones", "Thyroid, Reproductive, Adrenal, Pituitary", Icons.Default.Thermostat, Color(0xFFEC4899)),
        ScientificSection("Urinalysis", "تحليل البول الشامل", "Urinalysis", "Physical, Chemical, Urine Sediment, Crystals", Icons.Default.Opacity, Color(0xFFF59E0B)),
        ScientificSection("Stool Analysis", "تحليل البراز", "Stool Analysis", "Microscopic, Occult Blood, Parasites", Icons.Default.CleaningServices, Color(0xFF78350F)),
        ScientificSection("Blood Bank", "بنك الدم ونقل الدم", "Blood Bank", "ABO, Rh, Crossmatch, Components Safety", Icons.Default.LocalHospital, Color(0xFFDC2626)),
        ScientificSection("Equipment", "أجهزة ومعدات المختبر", "Lab Equipment", "Analyzers, Centrifuge, Microscope, Autoclave", Icons.Default.PrecisionManufacturing, Color(0xFF2563EB)),
        ScientificSection("Quality Control", "ضبط الجودة والسلامة", "Quality & Safety", "Westgard Rules, Biosafety, Analytical Errors", Icons.Default.VerifiedUser, MedicalEmerald400)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الأقسام العلمية والمختبرية", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sections) { section ->
                    Card(
                        modifier = Modifier
                            .height(150.dp)
                            .clickable { onSectionClick(section.id) }
                            .testTag("section_card_${section.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Surface(
                                color = section.color.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = section.icon,
                                    contentDescription = null,
                                    tint = section.color,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = section.titleAr,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = section.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
