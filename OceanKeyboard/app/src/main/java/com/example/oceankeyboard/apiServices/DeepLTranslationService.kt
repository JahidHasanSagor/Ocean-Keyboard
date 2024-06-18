package com.example.oceankeyboard.apiServices

import com.example.oceankeyboard.TranslationCallback
import com.example.oceankeyboard.TranslationService
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class DeepLTranslationService : TranslationService {

    private val client = OkHttpClient()
    private val apiKey = "4e62f4fb-db54-4c6f-a45d-7c7ccb6f4e95:fx"

    override fun translate(text: String, sourceLang: String, targetLang: String, callback: TranslationCallback) {
        val url = "https://api-free.deepl.com/v2/translate"
        val formBody = FormBody.Builder()
            .add("auth_key", apiKey)
            .add("text", text)
            .add("source_lang", sourceLang)
            .add("target_lang", targetLang)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onTranslationFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onTranslationFailure(response.message ?: "Unknown error")
                    return
                }

                response.body?.let {
                    try {
                        val responseBody = it.string()
                        val jsonResponse = JSONObject(responseBody)
                        val translations = jsonResponse.getJSONArray("translations")
                        if (translations.length() > 0) {
                            val translatedText = translations.getJSONObject(0).getString("text").trim()
                            callback.onTranslationSuccess(translatedText)
                        } else {
                            callback.onTranslationFailure("No translation found")
                        }
                    } catch (e: JSONException) {
                        callback.onTranslationFailure("Error parsing JSON: ${e.message}")
                    }
                } ?: callback.onTranslationFailure("Empty response")
            }
        })
    }
}
