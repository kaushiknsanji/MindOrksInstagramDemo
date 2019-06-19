package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the "Like-Post API".
 *
 * @author Kaushik N Sanji
 */
data class LikePostRequest(
    @Expose
    @SerializedName("postId")
    val postId: String
)