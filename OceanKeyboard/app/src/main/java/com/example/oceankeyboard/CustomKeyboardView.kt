package com.example.oceankeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet

class CustomKeyboardView(context: Context, attrs: AttributeSet?) : KeyboardView(context, attrs) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs)

    init {
        val keyboard = Keyboard(context, R.xml.qwerty)
        setKeyboard(keyboard)

        // Enable key preview popup (optional)
        isPreviewEnabled = true

        setOnKeyboardActionListener(object : OnKeyboardActionListener {
            override fun onPress(primaryCode: Int) {}
            override fun onRelease(primaryCode: Int) {}
            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                // ... (Call the onKey method from your CustomIME class)
            }

            override fun onText(text: CharSequence?) {}
            override fun swipeLeft() {}
            override fun swipeRight() {}
            override fun swipeDown() {}
            override fun swipeUp() {}
        })
    }
}
