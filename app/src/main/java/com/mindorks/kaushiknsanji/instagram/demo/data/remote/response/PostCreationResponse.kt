package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.MyPostItem

/**
 * Response Data class for the "Post Creation API".
 *
 * @author Kaushik N Sanji
 */
data class PostCreationResponse(
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
    val post: MyPostItem
)