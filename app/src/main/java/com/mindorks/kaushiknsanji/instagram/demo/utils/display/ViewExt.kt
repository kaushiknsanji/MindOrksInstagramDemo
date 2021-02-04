package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.view.View

/**
 * Kotlin file for extension functions on android `View`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [View] to make the [View] visible.
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Extension function on [View] to hide the [View].
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Extension function on [View] to show/hide the [View] based on the provided [visibility condition][visibilityCondition].
 * When [visibilityCondition] evaluates to `true`, then this [View] will be shown; otherwise hidden.
 */
fun View.showWhen(visibilityCondition: Boolean) {
    if (visibilityCondition) {
        show()
    } else {
        hide()
    }
}