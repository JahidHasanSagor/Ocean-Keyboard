package com.example.oceankeyboard

interface TranslationCallback {
    fun onTranslationSuccess(translatedText: String)
    fun onTranslationFailure(error: String)
}

interface TranslationService {
    fun translate(text: String, sourceLang: String, targetLang: String, callback: TranslationCallback)
}
