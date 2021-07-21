/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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