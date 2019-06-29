package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import com.google.android.material.textfield.TextInputEditText

/**
 * Kotlin file for extension functions on `TextInputEditText`
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [TextInputEditText] to set the [value] only when it is different from its current value.
 */
fun TextInputEditText.setTextOnChange(value: String) {
    if (text.toString() != value) {
        // When changed, set the new text to the field
        setText(value)
    }
}