package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the "Token Refresh API".
 *
 * @author Kaushik N Sanji
 */
data class TokenRefreshRequest(
    @Expose
    @SerializedName("refreshToken")
    val refreshToken: String
)