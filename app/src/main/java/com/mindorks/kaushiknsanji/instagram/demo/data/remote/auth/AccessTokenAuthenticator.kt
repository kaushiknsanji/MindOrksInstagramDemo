package com.mindorks.kaushiknsanji.instagram.demo.data.remote.auth

import com.google.gson.GsonBuilder
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.GeneralResponse
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import okhttp3.*
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * [Authenticator] implementation to re-authenticate unauthorized requests
 * after renewing the expired token.
 *
 * @property accessTokenProvider [AccessTokenProvider] instance for access tokens, provided by Dagger.
 * @constructor Instance of [AccessTokenAuthenticator] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class AccessTokenAuthenticator @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider
) : Authenticator {

    /**
     * Returns a request that includes a credential to satisfy an authentication challenge in
     * [response]. Returns null if the challenge cannot be satisfied.
     *
     * The route is best effort, it currently may not always be provided even when logically
     * available. It may also not be provided when an authenticator is re-used manually in an
     * application interceptor, such as when implementing client-specific retries.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // Get current access token from the provider
        // If null, then return null
        val token = accessTokenProvider.token() ?: return null

        if (isTokenExpired(response) && hasAccessTokenHeader(response.request)) {
            // Proceed if the response says the Token is expired and this Token is present
            // as part of a Header

            // Do the following operations in a lock to avoid concurrency issues
            // since OkHttp can have multiple active threads
            ReentrantLock().withLock {
                // Get the re-authentication retry count
                var retryCount = retryCount(response.request)

                // Read the token again from the provider to see if it has been updated
                // already in another thread
                val newToken = accessTokenProvider.token()

                // Check if this token is different
                if (newToken != null && newToken != token) {
                    // If new token is already generated, then re-authenticate request
                    // with this new token and return the same
                    return reAuthenticateRequestWithNewToken(response.request, newToken, retryCount)
                }

                // Increment retry count for the next re-authentication
                retryCount++

                if (retryCount > RETRY_COUNT_LIMIT) {
                    // When we have reached the retry limit, log the problem and
                    // do not try to authenticate anymore
                    Logger.w(TAG, "Retry count exceeded!")
                    // Return null
                    return null
                }

                // When we have some retries left..
                Logger.d(TAG, "Attempting to renew the expired token..")

                // Renew the Token using the provider (in blocking manner) and
                // re-authenticate request with this new token and return the same
                return accessTokenProvider.refreshToken()?.let { updatedToken ->
                    Logger.d(TAG, "Re-authenticating after token renewal..")
                    reAuthenticateRequestWithNewToken(response.request, updatedToken, retryCount)
                }
            }
        }

        // On all else, log the problem and return null
        Logger.w(TAG, "Unable to re-authenticate!")
        return null
    }

    /**
     * Validates if this unauthorized request is for Token expiry issue.
     * @param response [Response] to read its `statusCode` and `message` for validation.
     *
     * @return A [Boolean] which will be `true` when this unauthorized request is
     * for Token expiry issue; `false` otherwise.
     */
    private fun isTokenExpired(response: Response): Boolean =
        response.body?.let { responseBody: ResponseBody ->
            GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson(responseBody.string(), GeneralResponse::class.java)
                ?.let { generalResponse: GeneralResponse ->
                    // Check statusCode for "invalidAccessToken" and message for "Token is expired"
                    generalResponse.statusCode == STATUS_CODE_INVALID_ACCESS_TOKEN
                            && generalResponse.message == MESSAGE_TOKEN_EXPIRED
                } ?: false
        } ?: false

    /**
     * Validates if this unauthorized [request] has access token in one of its Headers.
     *
     * @return A [Boolean] which will be `true` when this unauthorized [request]
     * has "x-access-token" Header for Access Token; `false` otherwise.
     */
    private fun hasAccessTokenHeader(request: Request): Boolean =
        request.header(Networking.HEADER_ACCESS_TOKEN) != null

    /**
     * Retrieves re-authentication retry count from one of the Headers in the [request].
     *
     * @return [Int] value of the retry count read from the Header "x-retry-count" if present; 0 otherwise.
     */
    private fun retryCount(request: Request): Int =
        request.header(HEADER_RETRY_COUNT)?.toInt() ?: 0

    /**
     * Re-authenticates this unauthorized [request] by rebuilding it with [newToken] and [retryCount].
     *
     * @return New [Request] built from the unauthorized [request] with [newToken] and [retryCount]
     * for re-authenticating failed [request].
     */
    private fun reAuthenticateRequestWithNewToken(
        request: Request,
        newToken: String,
        retryCount: Int
    ): Request =
        request.newBuilder()
            .header(Networking.HEADER_ACCESS_TOKEN, newToken)
            .header(HEADER_RETRY_COUNT, retryCount.toString())
            .build()

    companion object {
        // Constant used for logs
        private const val TAG = "AccessTokenAuthenticator"

        // Header constant for saving retry count
        private const val HEADER_RETRY_COUNT = "x-retry-count"

        // Status Code constant for Invalid Access Token
        private const val STATUS_CODE_INVALID_ACCESS_TOKEN = "invalidAccessToken"

        // Message constant for Token expiry
        private const val MESSAGE_TOKEN_EXPIRED = "Token is expired"

        // Constant for the number of times the Expired Access Token can be tried
        // for renewal and re-authentication of request
        private const val RETRY_COUNT_LIMIT = 3
    }
}