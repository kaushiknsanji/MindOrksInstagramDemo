package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.FeedPostItem

/**
 * Response Data class for the "Post Detail API".
 *
 * @author Kaushik N Sanji
 */
data class PostDetailResponse(
    @Expose
    @SerializedName("statusCode")
    val statusCode: String,

    @Expose
    @SerializedName("status")
    val status: Int,

    @Expose
    @SerializedName("message")
    val message: String,

    @Expose
    @SerializedName("data")
    val post: FeedPostItem
)