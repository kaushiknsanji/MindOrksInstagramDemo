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