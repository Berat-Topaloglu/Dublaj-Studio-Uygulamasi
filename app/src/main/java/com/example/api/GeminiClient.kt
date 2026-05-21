package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(val text: String? = null)

@JsonClass(generateAdapter = true)
data class GeminiContent(val parts: List<GeminiPart>)

@JsonClass(generateAdapter = true)
data class ResponseFormatText(val mimeType: String)

@JsonClass(generateAdapter = true)
data class ResponseFormat(val text: ResponseFormatText)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GenerationConfig? = null
)

data class GeminiTranslationResult(
    val transcript: String,
    val translation: String
)

object GeminiClient {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val moshiByReflect = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun translateVideoContent(
        title: String,
        srcLang: String,
        destLang: String
    ): GeminiTranslationResult? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Esek geçilirse yerel simülatör kullanılacak
            return null
        }

        try {
            val promptText = """
                You are a precise video dubbing, transcription, and translation system. 
                The user has submitted a video titled "$title". 
                Source Language: $srcLang 
                Target Language: $destLang

                Please generate:
                1) A realistic, coherent, and detailed original transcript of around 3-4 sentences in $srcLang as if spoken in the video.
                2) A professional, natural, and matching translated transcript in $destLang representing the original perfectly.

                Output format: You MUST return a single JSON object with EXACTLY two fields:
                - "transcript": containing the original transcript in $srcLang.
                - "translation": containing the translated transcript in $destLang.
                Do not include markdown tags like ```json or any other text. Plain JSON only.
            """.trimIndent()

            val requestObj = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = promptText)))),
                generationConfig = GenerationConfig(
                    responseFormat = ResponseFormat(ResponseFormatText("application/json")),
                    temperature = 0.2f
                )
            )

            val adapter = moshiByReflect.adapter(GeminiRequest::class.java)
            val jsonBody = adapter.toJson(requestObj)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return null
                }
                val rawBody = response.body?.string() ?: return null
                val jsonResponse = JSONObject(rawBody)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return null
                val firstCandidate = candidates.optJSONObject(0) ?: return null
                val contentObj = firstCandidate.optJSONObject("content") ?: return null
                val partsArr = contentObj.optJSONArray("parts") ?: return null
                val firstPart = partsArr.optJSONObject(0) ?: return null
                val textResponse = firstPart.optString("text") ?: return null

                // Parse standard JSON result returned by Gemini
                val parsedObj = JSONObject(textResponse)
                val transcript = parsedObj.optString("transcript", "")
                val translation = parsedObj.optString("translation", "")

                if (transcript.isNotEmpty() && translation.isNotEmpty()) {
                    return GeminiTranslationResult(transcript, translation)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
