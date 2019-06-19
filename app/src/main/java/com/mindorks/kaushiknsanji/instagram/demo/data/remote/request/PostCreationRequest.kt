package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the "Post Creation API".
 *
 * @author Kaushik N Sanji
 */
data class PostCreationRequest(
    @Expose
    @SerializedName("imgUrl")
    val imageUrl: String,

    @Expose
    @SerializedName("imgWidth")
    val imageWidth: Int,

    @Expose
    @SerializedName("imgHeight")
    val imageHeight: Int
)