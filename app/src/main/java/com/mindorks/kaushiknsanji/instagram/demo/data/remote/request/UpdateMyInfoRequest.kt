package com.mindorks.kaushiknsanji.instagram.demo.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request Data Class for the "Update My Info API".
 *
 * @author Kaushik N Sanji
 */
data class UpdateMyInfoRequest(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("profilePicUrl")
    val profilePicUrl: String?,

    @Expose
    @SerializedName("tagline")
    val tagline: String?
)