package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the "Image Upload API".
 *
 * @author Kaushik N Sanji
 */
data class ImageUploadRequest(
    // TODO: Need to check the type of image later
    @Expose
    @SerializedName("image")
    val image: String
)