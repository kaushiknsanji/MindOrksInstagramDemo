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

package com.mindorks.kaushiknsanji.instagram.demo.data.remote.auth

import com.mindorks.kaushiknsanji.instagram.demo.data.repository.TokenRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides access tokens for authenticating requests.
 *
 * @property tokenRepository Instance of [TokenRepository] for token management, provided by Dagger.
 * @constructor Instance of [AccessTokenProvider] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class AccessTokenProvider @Inject constructor(
    private val tokenRepository: TokenRepository
) : AuthTokenProvider {

    /**
     * Provides an Access token if any.
     *
     * @return [String] value of an Access token if any.
     * Can be `null` when there is no token available yet.
     */
    override fun token(): String? = tokenRepository.getAccessToken()

    /**
     * Provides a New Access token after renewing the expired one. This will be a blocking call.
     *
     * @return [String] value of a New Access token generated.
     * Can be `null` when the token could not be renewed for some reason.
     */
    override fun refreshToken(): String? = tokenRepository.doTokenRenewal()

}