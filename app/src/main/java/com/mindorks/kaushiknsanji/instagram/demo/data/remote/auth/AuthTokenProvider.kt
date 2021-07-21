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

/**
 * Provider Interface to provide an Access Token or Renewed Token for Request Authorization.
 *
 * @author Kaushik N Sanji
 */
interface AuthTokenProvider {
    /**
     * Provides an Access token if any.
     *
     * @return [String] value of an Access token if any.
     * Can be `null` when there is no token available yet.
     */
    fun token(): String?

    /**
     * Provides a New Access token after renewing the expired one. This will be a blocking call.
     *
     * @return [String] value of a New Access token generated.
     * Can be `null` when the token could not be renewed for some reason.
     */
    fun refreshToken(): String?
}