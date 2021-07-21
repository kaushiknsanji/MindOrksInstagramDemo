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

import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.MyPostItem
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.*
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.toMyPostItem
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.TestConstants
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import java.net.HttpURLConnection

/**
 * Fake Retrofit API implementation for testing Instagram API.
 *
 * @author Kaushik N Sanji
 */
class FakeNetworkService : NetworkService {

    companion object {
        // Constant representing the "statusCode" value for all Responses
        private const val STATUS_CODE = "statusCode"

        // Standard Fake response message for most of the API calls
        const val RESPONSE_SUCCESS = "Success"

        // Fake response message for Logout API call
        const val RESPONSE_LOGOUT = "Logout success"

        // Fake URL of the Image uploaded via Image Upload API
        const val IMAGE_UPLOAD_RESULT_URL = "DummyUploadedImageUrl"

        // Fake response message for Post Delete API call
        const val RESPONSE_POST_DELETED = "Post deleted successfully"

        // Fake response message for Update User Info API call
        const val RESPONSE_INFO_UPDATED = "User info updated successfully"
    }

    init {
        // Defaulting API Key with a Fake for testing
        Networking.API_KEY = TestConstants.API_KEY
    }

    /**
     * API method to Register a New User.
     */
    override fun doSignUpCall(requestBody: SignUpRequest, apiKey: String): Single<SignUpResponse> =
        with(DataModelObjectProvider.signedInUser) {
            // Create and return a SingleSource of SignUpResponse for User "User_10"
            Single.just(
                SignUpResponse(
                    STATUS_CODE,
                    HttpURLConnection.HTTP_OK,
                    RESPONSE_SUCCESS,
                    accessToken = this.accessToken,
                    refreshToken = this.refreshToken,
                    userId = this.id,
                    userName = this.name,
                    userEmail = this.email
                )
            )
        }

    /**
     * API method to Login a registered User.
     */
    override fun doLoginCall(requestBody: LoginRequest, apiKey: String): Single<LoginResponse> =
        with(DataModelObjectProvider.signedInUser) {
            // Create and return a SingleSource of LoginResponse for User "User_10"
            Single.just(
                LoginResponse(
                    STATUS_CODE,
                    HttpURLConnection.HTTP_OK,
                    RESPONSE_SUCCESS,
                    accessToken = this.accessToken,
                    refreshToken = this.refreshToken,
                    userId = this.id,
                    userName = this.name,
                    userEmail = this.email
                )
            )
        }

    /**
     * API method to Logout the User.
     */
    override fun doLogoutCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> = Single.just(
        // Create and return a SingleSource of GeneralResponse
        GeneralResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_LOGOUT)
    )

    /**
     * API method to fetch the information of the logged-in User.
     */
    override fun doFetchMyInfoCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<FetchMyInfoResponse> = with(
        // Create a User for the given User Id
        DataModelObjectProvider.createUser(userId)
    ) {
        // Create and return a SingleSource of FetchMyInfoResponse for the given User
        Single.just(
            FetchMyInfoResponse(
                STATUS_CODE,
                HttpURLConnection.HTTP_OK,
                RESPONSE_SUCCESS,
                user = FetchMyInfoResponse.User(
                    id = this.id,
                    name = this.name,
                    profilePicUrl = this.profilePicUrl,
                    tagline = this.tagline
                )
            )
        )
    }

    /**
     * API method to update the information of the logged-in User.
     */
    override fun doUpdateMyInfoCall(
        requestBody: UpdateMyInfoRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> = Single.just(
        // Create and return a SingleSource of GeneralResponse
        GeneralResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_INFO_UPDATED)
    )

    /**
     * API method to upload an Image using a [retrofit2.http.Multipart].
     */
    override fun doImageUploadCall(
        imagePart: MultipartBody.Part,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<ImageUploadResponse> = Single.just(
        // Create and return a SingleSource of ImageUploadResponse
        ImageUploadResponse(
            STATUS_CODE,
            HttpURLConnection.HTTP_OK,
            RESPONSE_SUCCESS,
            ImageUploadResponse.ImageData(IMAGE_UPLOAD_RESULT_URL)
        )
    )

    /**
     * API method to create an Instagram Post.
     */
    override fun doPostCreationCall(
        requestBody: PostCreationRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostCreationResponse> = with(
        // Create a User for the given User Id
        DataModelObjectProvider.createUser(userId)
    ) {
        // Create and return a SingleSource of PostCreationResponse
        Single.just(
            PostCreationResponse(
                STATUS_CODE,
                HttpURLConnection.HTTP_OK,
                RESPONSE_SUCCESS,
                // Get a random Post created by the given User
                post = DataModelObjectProvider.getSingleRandomPostOfUser(this).toMyPostItem()
            )
        )
    }

    /**
     * API method to get the details of an Instagram Post.
     */
    override fun doPostDetailCall(
        postId: String,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostDetailResponse> = with(
        // Create a User for the given User Id
        DataModelObjectProvider.createUser(userId)
    ) {
        // Create and return a SingleSource of PostDetailResponse for the Post of given Post Id
        Single.just(
            PostDetailResponse(
                STATUS_CODE,
                HttpURLConnection.HTTP_OK,
                RESPONSE_SUCCESS,
                // Create Post for the given Post Id and User
                post = DataModelObjectProvider.createPostOfUser(postId, this).apply {
                    // Get some 5 random users to like this Post
                    DataModelObjectProvider.addUsersToLikedByListOfPosts(
                        DataModelObjectProvider.getRandomUsers(5),
                        // Post to be liked by users, which is 'this'
                        listOf(this),
                        // User to be excluded from liking this post, which is the given user
                        // (Same user can be used for testing like/unlike action on the Post later)
                        listOf(this@with)
                    )
                }
            )
        )
    }

    /**
     * API method to delete an Instagram Post.
     */
    override fun doDeletePostCall(
        postId: String,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> = Single.just(
        // Create and return a SingleSource of GeneralResponse
        GeneralResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_POST_DELETED)
    )

    /**
     * API method to get all the Instagram Posts of the logged-in User.
     * Only 9 recent Posts will be returned.
     */
    override fun doMyPostsListCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<MyPostsListResponse> = with(
        // Create a User for the given User Id
        DataModelObjectProvider.createUser(userId)
    ) {
        // Get 9 random Posts created by the given User
        val userPosts: List<MyPostItem> = DataModelObjectProvider.getRandomPostsOfUser(this, 9)
            .map { post: Post -> post.toMyPostItem() }

        // Create and return a SingleSource of MyPostsListResponse
        Single.just(
            MyPostsListResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_SUCCESS, userPosts)
        )
    }

    /**
     * API method to get all the Instagram Posts in the Feed.
     */
    override fun doAllPostsListCall(
        firstPostId: String?,
        lastPostId: String?,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<AllPostsListResponse> {
        // Create User "User_10"
        val user10 = DataModelObjectProvider.createUser("10")

        // Create User "User_20"
        val user20 = DataModelObjectProvider.createUser("20")

        // Get 5 random Posts created by User "User_10"
        val allPosts = DataModelObjectProvider.getRandomPostsOfUser(user10, 5).toMutableList()

        // Get 5 random Posts created by User "User_20"
        val postsOfUser20 = DataModelObjectProvider.getRandomPostsOfUser(user20, 5)

        // Load all Posts into a single list
        allPosts.addAll(postsOfUser20)

        // Get all Posts to be liked by some 5 random Users
        DataModelObjectProvider.addUsersToLikedByListOfPosts(
            DataModelObjectProvider.getRandomUsers(5),
            allPosts,
            // Ensures that the Post creators "User_10" and "User_20" do not
            // like the Posts provided (can be used for testing like/unlike action later)
            listOf(user10, user20)
        )

        // Create and return a SingleSource of AllPostsListResponse
        return Single.just(
            AllPostsListResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_SUCCESS, allPosts)
        )
    }

    /**
     * API method to Like an Instagram Post.
     */
    override fun doLikePostCall(
        requestBody: LikePostRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> = Single.just(
        // Create and return a SingleSource of GeneralResponse
        GeneralResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_SUCCESS)
    )

    /**
     * API method to Unlike an Instagram Post.
     */
    override fun doUnlikePostCall(
        requestBody: UnlikePostRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> = Single.just(
        // Create and return a SingleSource of GeneralResponse
        GeneralResponse(STATUS_CODE, HttpURLConnection.HTTP_OK, RESPONSE_SUCCESS)
    )
}