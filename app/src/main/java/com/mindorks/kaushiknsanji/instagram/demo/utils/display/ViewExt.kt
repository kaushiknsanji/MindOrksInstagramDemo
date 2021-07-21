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