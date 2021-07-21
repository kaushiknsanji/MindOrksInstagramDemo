/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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