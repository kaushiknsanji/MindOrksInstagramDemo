package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import android.view.View
import android.widget.ProgressBar

/**
 * Kotlin file for extension functions on `ProgressBar`
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [ProgressBar] to change its visibility based on [show].
 * Displays the [ProgressBar] when [show] is `true`; hides otherwise.
 */
fun ProgressBar.setVisibility(show: Boolean) {
    // Show the ProgressBar when [show] is true
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}