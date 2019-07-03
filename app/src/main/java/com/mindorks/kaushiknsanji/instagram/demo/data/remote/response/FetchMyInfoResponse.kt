package com.mindorks.kaushiknsanji.instagram.demo.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response Data Class for the "Fetch My Info API".
 *
 * @author Kaushik N Sanji
 */
data class FetchMyInfoResponse(
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
    val user: User
) {

    /**
     * Embedded Data class for User information
     *
     * @author Kaushik N Sanji
     */
    data class User(
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

}