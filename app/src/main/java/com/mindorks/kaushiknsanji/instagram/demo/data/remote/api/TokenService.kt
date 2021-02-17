package com.mindorks.kaushiknsanji.instagram.demo.data.remote.api

import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.TokenRefreshRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.TokenRefreshResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API Interface for Instagram Access Tokens.
 *
 * @author Kaushik N Sanji
 */
interface TokenService {

    /**
     * API method to Refresh User Access Token when it expires.
     * Executes the call synchronously in a blocking manner.
     */
    @POST(Endpoints.TOKEN_REFRESH)
    fun doTokenRefreshCall(
        @Body requestBody: TokenRefreshRequest,
        @Header(Networking.HEADER_USER_ID) userId: String,
        @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Call<TokenRefreshResponse>

}