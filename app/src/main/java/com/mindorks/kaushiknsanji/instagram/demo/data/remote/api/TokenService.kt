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