package com.mindorks.kaushiknsanji.instagram.demo.data.remote.api

import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.*
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.TestConstants
import io.reactivex.Single
import okhttp3.MultipartBody

/**
 * Fake Retrofit API implementation for testing Instagram API.
 *
 * @author Kaushik N Sanji
 */
class FakeNetworkService : NetworkService {

    init {
        // Defaulting API Key with a Fake for testing
        Networking.API_KEY = TestConstants.API_KEY
    }

    /**
     * API method to Register a New User.
     */
    override fun doSignUpCall(requestBody: SignUpRequest, apiKey: String): Single<SignUpResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to Login a registered User.
     */
    override fun doLoginCall(requestBody: LoginRequest, apiKey: String): Single<LoginResponse> =
        with(DataModelObjectProvider.createUser("10")) {
            // Create and return a SingleSource of LoginResponse for User "User_10"
            Single.just(
                LoginResponse(
                    statusCode = "statusCode",
                    status = 200,
                    message = "Success",
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
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to fetch the information of the logged-in User.
     */
    override fun doFetchMyInfoCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<FetchMyInfoResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to update the information of the logged-in User.
     */
    override fun doUpdateMyInfoCall(
        requestBody: UpdateMyInfoRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to upload an Image using a [retrofit2.http.Multipart].
     */
    override fun doImageUploadCall(
        imagePart: MultipartBody.Part,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<ImageUploadResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to create an Instagram Post.
     */
    override fun doPostCreationCall(
        requestBody: PostCreationRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostCreationResponse> {
        TODO("Not yet implemented")
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
                statusCode = "statusCode",
                status = 200,
                message = "Success",
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
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    /**
     * API method to get all the Instagram Posts of the logged-in User.
     * Only 9 recent Posts will be returned.
     */
    override fun doMyPostsListCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<MyPostsListResponse> {
        TODO("Not yet implemented")
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
            AllPostsListResponse("statusCode", 200, "Success", allPosts)
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
        GeneralResponse("statusCode", 200, "Success")
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
        GeneralResponse("statusCode", 200, "Success")
    )
}