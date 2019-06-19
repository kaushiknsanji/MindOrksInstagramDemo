package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the "Unlike-Post API".
 *
 * @author Kaushik N Sanji
 */
data class UnlikePostRequest(
    @Expose
    @SerializedName("postId")
    val postId: String
)