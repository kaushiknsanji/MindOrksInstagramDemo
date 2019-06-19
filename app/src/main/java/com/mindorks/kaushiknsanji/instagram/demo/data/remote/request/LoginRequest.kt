package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data class for the Login API.
 *
 * @author Kaushik N Sanji
 */
data class LoginRequest(
    @Expose
    @SerializedName("email")
    val email: String,

    @Expose
    @SerializedName("password")
    val password: String
)