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

package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs.UserPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.TokenService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.TokenRefreshRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.TokenRefreshResponse
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for User Token management.
 *
 * @property tokenService Instance of Retrofit API Service [TokenService] provided by Dagger.
 * @property userPreferences Instance of [UserPreferences] which is a SharedPreferences wrapper for User data, provided by Dagger.
 * @constructor Instance of [TokenRepository] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class TokenRepository @Inject constructor(
    private val tokenService: TokenService,
    private val userPreferences: UserPreferences
) {

    /**
     * Retrieves logged-in [User] information from [userPreferences] if present; else
     * throws [IllegalStateException].
     */
    private fun getCurrentUser(): User {
        // Reading from userPreferences
        val userId = userPreferences.getUserId()
        val userName = userPreferences.getUserName()
        val userEmail = userPreferences.getUserEmail()
        val accessToken = userPreferences.getAccessToken()
        val refreshToken = userPreferences.getRefreshToken()

        // Returning user information if present, else null
        return if (userId !== null && userName != null && userEmail != null
            && accessToken != null && refreshToken != null
        )
            User(
                id = userId,
                name = userName,
                email = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken,
                profilePicUrl = userPreferences.getProfilePicUrl(),
                tagline = userPreferences.getTagline()
            ) // ( profilePicUrl and tagline can be null, need not necessarily be present at the time of reading )
        else
            throw IllegalStateException(
                "Preferences does not contain any saved User information."
            )
    }

    /**
     * Provides the Access Token of logged-in user from [userPreferences].
     *
     * @return [String] value of the Access Token of logged-in user.
     * Can be `null` if no user has logged-in or no information is present.
     */
    fun getAccessToken(): String? = userPreferences.getAccessToken()

    /**
     * Performs a blocking Token renewal request with the Remote API and returns a
     * renewed Access Token from the [TokenRefreshResponse].
     *
     * @return [String] value of a New Access Token generated. Can be `null` when the
     * token could not be renewed for some reason.
     */
    fun doTokenRenewal(): String? =
        with(getCurrentUser()) {
            // When we have the logged-in user information, generate a new token using "refreshToken"
            tokenService.doTokenRefreshCall(
                TokenRefreshRequest(this.refreshToken),
                this.id,
                this.accessToken
            ).execute().body()?.let { response: TokenRefreshResponse ->
                when (response.status) {
                    // On Success
                    HttpURLConnection.HTTP_OK -> {
                        // Save the new Access Token and Refresh Token to preferences
                        userPreferences.setAccessToken(response.accessToken)
                        userPreferences.setRefreshToken(response.refreshToken)

                        // Return the renewed Access Token
                        response.accessToken
                    }
                    // else, return null
                    else -> null
                }
            }
        }
}