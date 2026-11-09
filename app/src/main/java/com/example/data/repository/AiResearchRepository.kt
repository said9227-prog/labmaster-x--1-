package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.AiQueryRecord
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiResearchRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userPrefs = UserPreferencesRepository(context)
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val queryHistory: Flow<List<AiQueryRecord>> = db.aiQueryDao().getAllQueries()

    suspend fun performScientificResearch(prompt: String): String = withContext(Dispatchers.IO) {
        val customKey = userPrefs.getGeminiApiKey()

        val liveResponse = if (customKey.isNotBlank()) {
            performDirectGeminiApiCall(prompt, customKey)
        } else {
            try {
                val aiModel = Firebase.ai.generativeModel("gemini-2.5-flash")
                val systemInstruction = """
                    أنت مساعد بحث علمي ومستشار مختبرات طبية خبير لمبنى LabMaster X.
                    قدم إجابات طبية دقيقة وموثوقة، مع ذكر المصادر العلمية (WHO, CDC, CLSI, Mayo Clinic Labs, ADLM).
                """.trimIndent()
                val result = aiModel.generateContent("$systemInstruction\n\nالسؤال: $prompt")
                result.text?.let { "⚡ **إجابة مباشرة من Gemini AI:**\n\n$it" }
            } catch (e: Exception) {
                null
            }
        }

        val finalMarkdown = liveResponse ?: generateOfflineAiResearchResponse(prompt)

        // Save to query history
        db.aiQueryDao().insertQuery(
            AiQueryRecord(
                prompt = prompt,
                responseMarkdown = finalMarkdown,
                sourcesJson = "[\"CLSI H18-A4\", \"CDC DPDx\", \"Mayo Clinic Labs\", \"WHO Manual\"]"
            )
        )

        finalMarkdown
    }

    private suspend fun performDirectGeminiApiCall(prompt: String, apiKey: String): String? {
        val models = listOf("gemini-2.5-flash", "gemini-1.5-flash", "gemini-2.0-flash", "gemini-1.5-pro")
        var lastErrorMessage: String? = null

        for (model in models) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val parts = JSONArray().apply {
                                val systemText = "أنت مستشار بحث علمي ومختبرات طبية خبير لـ LabMaster X. إجابات موثوقة ومفصلة باحترافية وتنسيق واضع."
                                put(JSONObject().put("text", "$systemText\n\nالسؤال: $prompt"))
                            }
                            put("parts", parts)
                        }
                        put(contentObj)
                    }
                    put("contents", contents)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""
                    if (response.isSuccessful && bodyString.isNotBlank()) {
                        val resJson = JSONObject(bodyString)
                        val candidates = resJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                val generatedText = parts.getJSONObject(0).optString("text")
                                if (generatedText.isNotBlank()) {
                                    return "⚡ **إجابة حية فائقة السرعة عبر Gemini AI (نموذج $model):**\n\n$generatedText"
                                }
                            }
                        }
                    } else {
                        if (bodyString.contains("API_KEY_INVALID")) {
                            return "❌ **مفتاح Gemini API غير صالح:**\n\nالمفتاح المدخل في صفحة الإعدادات تم رفضه بواسطة سيرفرات Google.\nيرجى التأكد من نسخ المفتاح بشكل صحيح من Google AI Studio والإبقاء عليه خاليًا من الفراغات."
                        }
                        lastErrorMessage = "HTTP ${response.code}: ${response.message}"
                    }
                }
            } catch (e: Exception) {
                lastErrorMessage = e.localizedMessage ?: "Network error"
            }
        }

        return if (lastErrorMessage != null) {
            "⚠️ **تعذر الاتصال المباشر بـ Gemini AI بالمفتاح المدخل:** ($lastErrorMessage)\n\nجاري إظهار المحتوى التجميعي المحقق:\n\n" + generateOfflineAiResearchResponse(prompt)
        } else null
    }

    private suspend fun generateOfflineAiResearchResponse(prompt: String): String {
        val lower = prompt.lowercase()
        val matchedTest = when {
            lower.contains("cbc") || lower.contains("دم") || lower.contains("عد") -> "cbc"
            lower.contains("fbg") || lower.contains("سكر") || lower.contains("جلوكوز") -> "fbg"
            lower.contains("creatinine") || lower.contains("كلى") || lower.contains("كرياتينين") -> "creatinine"
            lower.contains("alt") || lower.contains("كبد") -> "alt"
            lower.contains("tsh") || lower.contains("درقية") || lower.contains("هرمون") -> "tsh"
            lower.contains("pt") || lower.contains("inr") || lower.contains("تجلط") -> "pt_inr"
            lower.contains("بول") || lower.contains("urine") -> "urinalysis"
            lower.contains("براز") || lower.contains("stool") || lower.contains("طفيل") -> "stool_analysis"
            else -> null
        }

        val testObj = matchedTest?.let { db.labTestDao().getTestById(it) }

        return if (testObj != null) {
            """
                ### 🔬 نتيجة البحث العلمي الموثق: ${testObj.nameAr} (${testObj.nameEn} - ${testObj.abbreviation})
                
                **الهدف والداعي الطبي:**
                ${testObj.purposeAr}
                
                **المبدأ العلمي والتحليلي:**
                ${testObj.scientificPrincipleAr}
                
                **إرشادات عينة السحب:**
                - **نوع العينة:** ${testObj.sampleType}
                - **الأنبوب المناسب:** ${testObj.tubeColor} (${testObj.tubeAdditive})
                - **الحفظ والاستقرار:** ${testObj.sampleStability} عند 4°C
                - **أسباب رفض العينة:** ${testObj.rejectionCriteria}
                
                **التفسير السريري والقيم المرجعية:**
                ${testObj.referenceRangesSummary}
                
                - ⬆️ **الارتفاع (High):** ${testObj.highInterpretationAr}
                - ⬇️ **الانخفاض (Low):** ${testObj.lowInterpretationAr}
                
                ---
                **المصادر العلمية الموثوقة:**
                - CLSI Clinical Laboratory Standards Institute
                - CDC DPDx Medical Diagnostic Bench Aids
                - Mayo Clinic Laboratories Test Catalog
                
                *ملاحظة: هذا ملخص علمي موثوق مستخرج من قاعدة بيانات المحرك المحلي المحققة.*
            """.trimIndent()
        } else {
            """
                ### 🤖 محرك البحث العلمي الذكي - LabMaster X AI Assistant
                
                **تحليل الاستفسار:** "$prompt"
                
                **الملخص العلمي والتوجيهات المختبرية:**
                - يعتبر فحص المختبر المؤشر الأول لتقييم الوظائف البيولوجية.
                - يجب مراعاة مرحلة ما قبل التحليل (Pre-analytical Phase) كاختيار الأنبوب الصحيح، سرعة الفصل، ودرجة حرارة الحفظ لمنع 80% من أخطاء المختبر.
                - يرجى مراجعة قسم **أنابيب سحب الدم** أو **القيم المرجعية** أو البحث باسم الفحص بـ (CBC, FBG, TSH, ALT, PT, Urine) لعرض البيانات التفصيلية.
                
                **المصادر الطبية المرجعية:**
                1. WHO Laboratory Biosafety Manual
                2. CDC DPDx Diagnostic Bench Aids
                3. ADLM Association for Diagnostics & Laboratory Medicine
            """.trimIndent()
        }
    }

    suspend fun clearHistory() {
        db.aiQueryDao().clearHistory()
    }
}
