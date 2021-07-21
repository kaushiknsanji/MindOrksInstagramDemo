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

package com.mindorks.kaushiknsanji.instagram.demo.data.remote.api

/**
 * Singleton object for managing all the API Endpoints in one place.
 *
 * @author Kaushik N Sanji
 */
object Endpoints {

    // Sign Up
    const val SIGNUP = "signup/mindorks"

    // Login
    const val LOGIN = "login/mindorks"

    // To Refresh Token when token expires
    const val TOKEN_REFRESH = "token/refresh"

    // Logout
    const val LOGOUT = "logout"

    // Get or Update My Info
    const val MY_INFO = "me"

    // To Upload an Image
    const val IMAGE_UPLOAD = "image"

    // Create Instagram Post
    const val POST_CREATION = "instagram/post"

    // Get the Details of a Post
    const val POST_DETAIL = "instagram/post/id/{postId}"

    // Delete a Post
    const val DELETE_POST = "instagram/post/id/{postId}"

    // Get My list of Posts
    const val MY_POSTS_LIST = "instagram/post/my"

    // Get All Posts (supports Query Parameters firstPostId and lastPostId)
    const val ALL_POSTS_LIST = "instagram/post/list"

    // To Like a Post
    const val LIKE_POST = "instagram/post/like"

    // To Unlike a Post
    const val UNLIKE_POST = "instagram/post/unlike"

}