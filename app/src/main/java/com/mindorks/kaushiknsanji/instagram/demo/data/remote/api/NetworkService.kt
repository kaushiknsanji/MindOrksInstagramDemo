package com.mindorks.kaushiknsanji.instagram.demo.data.remote.api

import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.*
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.*
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Retrofit API Interface for the Instagram API.
 *
 * @author Kaushik N Sanji
 */
interface NetworkService {

    /**
     * API method to Register a New User.
     */
    @POST(Endpoints.SIGNUP)
    fun doSignUpCall(
        @Body requestBody: SignUpRequest,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<SignUpResponse>

    /**
     * API method to Login a registered User.
     */
    @POST(Endpoints.LOGIN)
    fun doLoginCall(
        @Body requestBody: LoginRequest,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<LoginResponse>

    /**
     * API method to Logout the User.
     */
    @DELETE(Endpoints.LOGOUT)
    fun doLogoutCall(
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<GeneralResponse>

    /**
     * API method to fetch the information of the logged-in User.
     */
    @GET(Endpoints.MY_INFO)
    fun doFetchMyInfoCall(
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<FetchMyInfoResponse>

    /**
     * API method to update the information of the logged-in User.
     */
    @PUT(Endpoints.MY_INFO)
    fun doUpdateMyInfoCall(
        @Body requestBody: UpdateMyInfoRequest,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<GeneralResponse>

    /**
     * API method to upload an Image using a [Multipart].
     */
    @Multipart
    @POST(Endpoints.IMAGE_UPLOAD)
    fun doImageUploadCall(
        @Part imagePart: MultipartBody.Part,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<ImageUploadResponse>

    /**
     * API method to create an Instagram Post.
     */
    @POST(Endpoints.POST_CREATION)
    fun doPostCreationCall(
        @Body requestBody: PostCreationRequest,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<PostCreationResponse>

    /**
     * API method to get the details of an Instagram Post.
     */
    @GET(Endpoints.POST_DETAIL)
    fun doPostDetailCall(
        @Path("postId") postId: String,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<PostDetailResponse>

    /**
     * API method to delete an Instagram Post.
     */
    @DELETE(Endpoints.DELETE_POST)
    fun doDeletePostCall(
        @Path("postId") postId: String,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<GeneralResponse>

    /**
     * API method to get all the Instagram Posts of the logged-in User.
     * Only 9 recent Posts will be returned.
     */
    @GET(Endpoints.MY_POSTS_LIST)
    fun doMyPostsListCall(
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<MyPostsListResponse>

    /**
     * API method to get all the Instagram Posts in the Feed.
     */
    @GET(Endpoints.ALL_POSTS_LIST)
    fun doAllPostsListCall(
        @Query("firstPostId") firstPostId: String?,
        @Query("lastPostId") lastPostId: String?,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<AllPostsListResponse>

    /**
     * API method to Like an Instagram Post.
     */
    @PUT(Endpoints.LIKE_POST)
    fun doLikePostCall(
        @Body requestBody: LikePostRequest,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<GeneralResponse>

    /**
     * API method to Unlike an Instagram Post.
     */
    @PUT(Endpoints.UNLIKE_POST)
    fun doUnlikePostCall(
        @Body requestBody: UnlikePostRequest,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Single<GeneralResponse>

}