package com.mindorks.kaushiknsanji.instagram.demo.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Data Class of any User's Post, embedded in some of the Remote API Responses.
 *
 * @author Kaushik N Sanji
 */
data class FeedPostItem(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("imgUrl")
    val imageUrl: String,

    @Expose
    @SerializedName("imgWidth")
    val imageWidth: Int,

    @Expose
    @SerializedName("imgHeight")
    val imageHeight: Int,

    @Expose
    @SerializedName("createdAt")
    val createdAt: Date,

    @Expose
    @SerializedName("user")
    val creator: UserItem,

    @Expose
    @SerializedName("likedBy")
    val likedBy: MutableList<UserItem>?
)