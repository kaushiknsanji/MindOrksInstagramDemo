package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response Data class for the "Image Upload API".
 *
 * @author Kaushik N Sanji
 */
data class ImageUploadResponse(
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
    val data: ImageUrlItem
) {

    /**
     * Image URL Class embedded in the Response.
     *
     * @author Kaushik N Sanji
     */
    data class ImageUrlItem(
        @Expose
        @SerializedName("imageUrl")
        val imageUrl: String
    )

}