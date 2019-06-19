package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response Data Class common to General APIs
 * like "Logout", "Update My Info", "Delete Post", "Like Post", "Unlike Post".
 *
 * @author Kaushik N Sanji
 */
data class GeneralResponse(
    @Expose
    @SerializedName("statusCode")
    val statusCode: String,

    @Expose
    @SerializedName("status")
    val status: Int,

    @Expose
    @SerializedName("message")
    val message: String
)