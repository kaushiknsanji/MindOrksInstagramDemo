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

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

/**
 * Utility object for working with [com.bumptech.glide.Glide]
 *
 * @author Kaushik N Sanji
 */
object GlideHelper {

    /**
     * Prepares and returns a [GlideUrl] for the Image [url] request that needs [headers]
     */
    fun getProtectedUrl(url: String, headers: Map<String, String>): GlideUrl =
        GlideUrl(url, LazyHeaders.Builder().apply {
            // Building the headers to be included in the Glide request
            headers.forEach { entry: Map.Entry<String, String> ->
                // Loading each [headers] map entry as a LazyHeader
                addHeader(entry.key, entry.value)
            }
        }.build())


    /**
     * Prepares and returns an appropriate [GlideRequests] instance based on the [context] provided.
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun getAppropriateGlideRequests(
        context: Context
    ): GlideRequests = when (context) {
        is FragmentActivity -> GlideApp.with(context)
        is Fragment -> GlideApp.with(context as Fragment)
        is View -> GlideApp.with(context as View)
        else -> GlideApp.with(context)
    }

}