package com.example.oceankeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import com.example.oceankeyboard.apiServices.DeepLTranslationService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CustomIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var kv: KeyboardView
    private lateinit var keyboard: Keyboard
    private val TAG = "Hello Sagor CustomIME"
    private val translationService = DeepLTranslationService() // Instantiate DeepLTranslationService

    override fun onCreateInputView(): View {
        kv = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.qwerty)
        kv.keyboard = keyboard
        kv.setOnKeyboardActionListener(this)
        return kv
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        when (primaryCode) {
            -3 -> translateAndSend() // Handle SEND key
            else -> {
                val ic = currentInputConnection
                if (ic != null) {
                    ic.commitText(primaryCode.toChar().toString(), 1)
                }
            }
        }
    }

    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}

    private fun translateAndSend() {
        val ic = currentInputConnection
        val extractedTextRequest = ExtractedTextRequest()
        val extractedText = ic?.getExtractedText(extractedTextRequest, 0)
        val typedText = extractedText?.text?.toString() ?: ""

        Log.d(TAG, "Extracted word: $typedText")

        translationService.translate(typedText, "EN", "FI", object : TranslationCallback {
            override fun onTranslationSuccess(translatedText: String) {
                ic.deleteSurroundingText(typedText.length, 0)
                ic.commitText(translatedText, 1)
                // Notify MainActivity or any other listener that translation is complete
                translationCompleteListener?.onTranslationComplete(translatedText)
            }

            override fun onTranslationFailure(error: String) {
                Log.e(TAG, "Translation failed: $error")
            }
        })
    }

    // Interface to notify translation completion
    interface TranslationCompleteListener {
        fun onTranslationComplete(translatedText: String)
    }

    private var translationCompleteListener: TranslationCompleteListener? = null

    fun setTranslationCompleteListener(listener: TranslationCompleteListener) {
        this.translationCompleteListener = listener
    }

}
