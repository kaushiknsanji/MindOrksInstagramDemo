package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Data class used as a wrapper to the [content] representing the [Event]s emitted by a [LiveData].
 *
 * @param T The type of [content] wrapped in an [Event]
 * @property content The wrapped private data of an [Event]
 * @constructor Creates an Instance of [Event]
 *
 * @author Kaushik N Sanji
 */
data class Event<out T>(private val content: T) {

    // Keeps track of whether the [content] has been consumed or not
    private var isContentConsumed = AtomicBoolean(false)

    /**
     * Returns the [content] when it has not been consumed previously as determined by the state of [isContentConsumed].
     * This prevents the repeated consumption of the content.
     */
    fun getContentIfNotConsumed(): T? = if (isContentConsumed.getAndSet(true)) {
        // Returning Null when the content has already been consumed once
        null
    } else {
        // Returning the Content when it has never been consumed
        content
    }

    /**
     * Returns the [content], whether or not the [content] has been previously consumed.
     */
    fun peekContent(): T = content

}