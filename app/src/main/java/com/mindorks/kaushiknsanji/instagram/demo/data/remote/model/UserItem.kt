package com.mindorks.kaushiknsanji.instagram.demo.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * User Data Class embedded in some of the Remote API Responses.
 *
 * @author Kaushik N Sanji
 */
data class UserItem(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("profilePicUrl")
    val profilePicUrl: String?,

    @Expose
    @SerializedName("tagline")
    val tagline: String
)