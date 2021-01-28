package com.mindorks.kaushiknsanji.instagram.demo.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Application-wide Data Model of any User's Post, embedded in some of the Remote API Responses.
 *
 * @author Kaushik N Sanji
 */
data class Post(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("imgUrl")
    val imageUrl: String,

    @Expose
    @SerializedName("imgWidth")
    val imageWidth: Int?,

    @Expose
    @SerializedName("imgHeight")
    val imageHeight: Int?,

    @Expose
    @SerializedName("createdAt")
    val createdAt: Date,

    @Expose
    @SerializedName("user")
    val creator: User,

    @Expose
    @SerializedName("likedBy")
    val likedBy: MutableList<User>?
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
        val profilePicUrl: String?
    )

    /**
     * Creates and returns a copy of [Post] with the shallow copy of [likedBy] list.
     */
    fun shallowCopy(): Post = copy(likedBy = getLikesCopy())

    /**
     * Creates and returns a shallow copy of [likedBy] list.
     */
    fun getLikesCopy(): MutableList<User>? = likedBy?.map { it }?.toMutableList()
}

/**
 * Extension function utility on [Post] that calculates the width ratio of the target screen/window to the source image,
 * and then scales the source Image height to fit the width of the target screen/window, while respecting the aspect ratio
 * of the source Image.
 *
 * @param targetWidth [Int] value of the target screen/window width
 * @param unavailableImageHeight Higher order function to provide a default height to be used
 * when [Post.imageHeight] of the source image is unavailable.
 * @return [Int] value of the scaled height when [Post.imageHeight] of the source image is available.
 * Otherwise the value provided by the function [unavailableImageHeight] is returned.
 */
fun Post.scaleImageHeightToTargetWidth(targetWidth: Int, unavailableImageHeight: () -> Int): Int =
    this.imageHeight?.let { imageHeight ->
        (this.imageWidth?.let { imageWidth -> targetWidth.toFloat() / imageWidth.toFloat() }
            ?: 1f) * imageHeight.toFloat()
    }?.toInt() ?: unavailableImageHeight.invoke()