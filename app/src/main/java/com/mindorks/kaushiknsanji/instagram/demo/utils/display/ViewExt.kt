package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

/**
 * Kotlin file for extension functions on android `View` and `ViewGroup`.
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
    // Delegate to the property to handle
    isVisible = visibilityCondition
}

/**
 * Casts the receiver [ViewGroup] to [RecyclerView] and returns the same.
 *
 * @throws [ClassCastException] if [ViewGroup] is NOT a [RecyclerView]
 */
@Throws(ClassCastException::class)
fun ViewGroup.toRecyclerView() = this as RecyclerView