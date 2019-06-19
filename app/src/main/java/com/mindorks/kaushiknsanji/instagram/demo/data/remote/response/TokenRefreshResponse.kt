package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response Data class for the "Token Refresh API".
 *
 * @author Kaushik N Sanji
 */
data class TokenRefreshResponse(
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
    @SerializedName("accessToken")
    val accessToken: String,

    @Expose
    @SerializedName("refreshToken")
    val refreshToken: String
)