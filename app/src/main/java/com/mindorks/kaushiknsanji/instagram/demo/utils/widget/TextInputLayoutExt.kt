package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import com.google.android.material.textfield.TextInputLayout
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status

/**
 * Kotlin file for extension functions on `TextInputLayout`
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [TextInputLayout] that sets the error [errorMessage]
 * when the [status] of the message is [Status.ERROR]
 */
fun TextInputLayout.setErrorStatus(status: Status, errorMessage: String?) {
    when (status) {
        // On Error, set the error on the field
        Status.ERROR -> error = errorMessage
        // When there is no Error, disable the error on the field
        else -> isErrorEnabled = false
    }
}