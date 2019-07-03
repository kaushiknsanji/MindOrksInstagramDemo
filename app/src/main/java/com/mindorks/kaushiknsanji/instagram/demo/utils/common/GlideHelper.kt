package com.mindorks.kaushiknsanji.instagram.demo.utils.common

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

}