package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class LabRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)

    val allTests: Flow<List<LabTest>> = db.labTestDao().getAllTests()
    val allTubes: Flow<List<BloodTube>> = db.bloodTubeDao().getAllTubes()
    val allRanges: Flow<List<ReferenceRange>> = db.referenceRangeDao().getAllRanges()
    val allImages: Flow<List<MedicalImageItem>> = db.medicalImageDao().getAllImages()
    val allWebsites: Flow<List<MedicalWebsiteItem>> = db.medicalWebsiteDao().getAllWebsites()
    val favoriteTests: Flow<List<LabTest>> = db.labTestDao().getFavoriteTests()

    suspend fun getTestById(id: String): LabTest? = db.labTestDao().getTestById(id)
    suspend fun getTubeById(id: String): BloodTube? = db.bloodTubeDao().getTubeById(id)

    fun getTestsByCategory(category: String): Flow<List<LabTest>> =
        db.labTestDao().getTestsByCategory(category)

    fun searchTests(query: String): Flow<List<LabTest>> =
        db.labTestDao().searchTests(query)

    fun searchTubes(query: String): Flow<List<BloodTube>> =
        db.bloodTubeDao().searchTubes(query)

    fun searchImages(query: String): Flow<List<MedicalImageItem>> =
        db.medicalImageDao().searchImages(query)

    suspend fun getRangesForTest(testId: String): List<ReferenceRange> =
        db.referenceRangeDao().getRangesForTest(testId)

    suspend fun toggleFavorite(test: LabTest) {
        val updated = test.copy(isFavorite = !test.isFavorite)
        db.labTestDao().updateTest(updated)
    }

    suspend fun updateImage(image: MedicalImageItem) {
        db.medicalImageDao().updateImage(image)
    }

    suspend fun updateWebsite(website: MedicalWebsiteItem) {
        db.medicalWebsiteDao().updateWebsite(website)
    }

    // Interactive Lab Reference Calculator
    data class CalculationResult(
        val isCompatible: Boolean,
        val flag: String, // "NORMAL", "HIGH", "LOW", "CRITICAL_HIGH", "CRITICAL_LOW"
        val referenceMin: Double,
        val referenceMax: Double,
        val unit: String,
        val clinicalNoteAr: String,
        val clinicalNoteEn: String,
        val source: String
    )

    suspend fun calculateResultFlag(
        testId: String,
        userValue: Double,
        unit: String,
        gender: String,
        ageYears: Int
    ): CalculationResult {
        val ranges = db.referenceRangeDao().getRangesForTest(testId)
        if (ranges.isEmpty()) {
            return CalculationResult(
                isCompatible = false,
                flag = "UNKNOWN",
                referenceMin = 0.0,
                referenceMax = 0.0,
                unit = unit,
                clinicalNoteAr = "لم يتم العثور على مجال مرجعي محدد لهذا الفحص.",
                clinicalNoteEn = "No specific reference range found for this test.",
                source = "LabMaster X Registry"
            )
        }

        val ageGroup = when {
            ageYears < 1 -> "Neonate"
            ageYears < 18 -> "Child"
            ageYears > 65 -> "Elderly"
            else -> "Adult"
        }

        val matched = ranges.firstOrNull { r ->
            (r.gender == "Both" || r.gender.equals(gender, ignoreCase = true)) &&
            (r.ageGroup == "Adult" || r.ageGroup.equals(ageGroup, ignoreCase = true))
        } ?: ranges.first()

        val flag = when {
            userValue < matched.minVal * 0.5 -> "CRITICAL_LOW"
            userValue < matched.minVal -> "LOW"
            userValue > matched.maxVal * 2.0 -> "CRITICAL_HIGH"
            userValue > matched.maxVal -> "HIGH"
            else -> "NORMAL"
        }

        val noteAr = when (flag) {
            "NORMAL" -> "النتيجة ضمن المجال المرجعي الطبيعي ($userValue ${matched.unit})."
            "LOW", "CRITICAL_LOW" -> "النتيجة منخفضة عن المجال الطبيعي (${matched.minVal} - ${matched.maxVal} ${matched.unit}). يوصى بالتقييم السريري."
            "HIGH", "CRITICAL_HIGH" -> "النتيجة مرتفعة عن المجال الطبيعي (${matched.minVal} - ${matched.maxVal} ${matched.unit}). يوصى بمراجعة الطبيب المعالج."
            else -> "نتائج مرجعية عامة."
        }

        val noteEn = when (flag) {
            "NORMAL" -> "Result is within normal reference interval ($userValue ${matched.unit})."
            "LOW", "CRITICAL_LOW" -> "Result is below reference interval (${matched.minVal} - ${matched.maxVal} ${matched.unit}). Clinical correlation suggested."
            "HIGH", "CRITICAL_HIGH" -> "Result is above reference interval (${matched.minVal} - ${matched.maxVal} ${matched.unit}). Physician consultation advised."
            else -> "General reference values."
        }

        return CalculationResult(
            isCompatible = true,
            flag = flag,
            referenceMin = matched.minVal,
            referenceMax = matched.maxVal,
            unit = matched.unit,
            clinicalNoteAr = noteAr,
            clinicalNoteEn = noteEn,
            source = matched.source
        )
    }
}
