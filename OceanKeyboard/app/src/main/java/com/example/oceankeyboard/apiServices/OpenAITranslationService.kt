package com.example.oceankeyboard.apiServices

import android.util.Log
import com.example.oceankeyboard.TranslationCallback
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException



class OpenAITranslationService {

    private val TAG = "Hello Sagor OpenAITranslationService"
    private val client = OkHttpClient()
    private val apiKey = "sk-proj-bPP3trwTOqYf7cS8WuuCT3BlbkFJq9SiAtfyKtGh4YvBnxYP"  // Replace with your actual OpenAI API key

    fun translate(text: String, sourceLang: String, targetLang: String, callback: TranslationCallback) {
        val url = "https://api.openai.com/v1/engines/gpt-3.5-turbo-instruct/completions"
        val prompt = "Translate the following text from $sourceLang to $targetLang: \"$text\""

        val json = JSONObject().apply {
            put("prompt", prompt)
            put("max_tokens", 60)
        }

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onTranslationFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.d(TAG, "Response failed")
                    callback.onTranslationFailure(response.message ?: "Unknown error")
                    return
                }

                response.body?.let {
                    Log.d(TAG, "Response okay")

                    val responseBody = it.string()
                    val jsonResponse = JSONObject(responseBody)
                    val choices = jsonResponse.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val translatedText = choices.getJSONObject(0).getString("text").trim()
                        callback.onTranslationSuccess(translatedText)
                    } else {
                        callback.onTranslationFailure("No translation found")
                    }
                } ?: callback.onTranslationFailure("Empty response")
            }
        })
    }


}
