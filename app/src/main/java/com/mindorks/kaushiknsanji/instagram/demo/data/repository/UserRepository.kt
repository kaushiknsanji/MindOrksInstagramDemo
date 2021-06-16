package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import androidx.annotation.RestrictTo
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs.UserPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.LoginRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.SignUpRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.UpdateMyInfoRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.FetchMyInfoResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.GeneralResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.LoginResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.SignUpResponse
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import io.reactivex.rxjava3.core.Single
import org.jetbrains.annotations.TestOnly
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for User data management.
 *
 * @property networkService Instance of Retrofit API Service [NetworkService] provided by Dagger.
 * @property databaseService Instance of RoomDatabase [DatabaseService] provided by Dagger.
 * @property userPreferences Instance of [UserPreferences] which is a SharedPreferences wrapper for User data, provided by Dagger.
 * @constructor Instance of [UserRepository] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class UserRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService,
    private val userPreferences: UserPreferences
) {

    /**
     * Saves logged-in [user] information to [userPreferences]
     */
    fun saveCurrentUser(user: User) {
        userPreferences.setUserId(user.id)
        userPreferences.setUserName(user.name)
        userPreferences.setUserEmail(user.email)
        userPreferences.setAccessToken(user.accessToken)
        userPreferences.setRefreshToken(user.refreshToken)
        userPreferences.setProfilePicUrl(user.profilePicUrl)
        userPreferences.setTagline(user.tagline)
    }

    /**
     * Removes logged-in user information from [userPreferences]
     */
    private fun removeCurrentUser() {
        userPreferences.removeUserId()
        userPreferences.removeUserName()
        userPreferences.removeUserEmail()
        userPreferences.removeAccessToken()
        userPreferences.removeRefreshToken()
        userPreferences.removeProfilePicUrl()
        userPreferences.removeTagline()
    }

    /**
     * Removes logged-in Test user information from [userPreferences]
     */
    @TestOnly
    @RestrictTo(value = [RestrictTo.Scope.TESTS])
    fun removeCurrentTestUser() {
        removeCurrentUser()
    }

    /**
     * Retrieves logged-in [User] information from [userPreferences] if present; else `null`.
     */
    fun getCurrentUserOrNull(): User? {
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
            null
    }

    /**
     * Retrieves logged-in [User] information from [userPreferences] if present; else
     * throws [IllegalStateException].
     */
    fun getCurrentUser(): User =
        getCurrentUserOrNull()
            ?: throw IllegalStateException(
                "Preferences does not contain any saved User information."
            )

    /**
     * Performs User Login request with the Remote API and returns a [Single] of [User] from the response.
     *
     * @param email [String] representing the Email entered by the User
     * @param password [String] representing the Password entered by the User
     * @return A [Single] of [User] from the response
     */
    fun doUserLogin(email: String, password: String): Single<User> =
        networkService.doLoginCall(LoginRequest(email, password))
            .map { response: LoginResponse ->
                // Transforming the [LoginResponse] to [User]
                User(
                    id = response.userId,
                    name = response.userName,
                    email = response.userEmail,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }

    /**
     * Performs User SignUp request with the Remote API and returns a [Single] of [User] from the response.
     *
     * @param email [String] representing the Email entered by the User
     * @param password [String] representing the Password entered by the User
     * @param name [String] representing the name entered by the User
     * @return A [Single] of [User] from the response
     */
    fun doUserSignUp(email: String, password: String, name: String): Single<User> =
        networkService.doSignUpCall(SignUpRequest(email, password, name))
            .map { response: SignUpResponse ->
                // Transforming the [SignUpResponse] to [User]
                User(
                    id = response.userId,
                    name = response.userName,
                    email = response.userEmail,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }

    /**
     * Performs [user] Info request with the Remote API and returns a [Single] of [User] from the response.
     *
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of [User] from the response updated with [User.profilePicUrl] and [User.tagline] information.
     */
    fun doFetchUserInfo(user: User): Single<User> =
        networkService.doFetchMyInfoCall(user.id, user.accessToken)
            .map { response: FetchMyInfoResponse ->
                // Transforming the [FetchMyInfoResponse] to [User]
                // Updating profilePicUrl and tagline information as it is not available in preferences initially
                user.copy(
                    profilePicUrl = response.user.profilePicUrl,
                    tagline = response.user.tagline
                ).also {
                    // Also, save the user information to preferences
                    saveCurrentUser(it)
                }
            }

    /**
     * Performs [user] Logout request with the Remote API and returns a [Single] of [Resource] from the response.
     *
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of [Resource] from the response.
     */
    fun doUserLogout(user: User): Single<Resource<String>> =
        networkService.doLogoutCall(user.id, user.accessToken)
            .map { response: GeneralResponse ->
                // Transforming the [GeneralResponse] to [Resource]
                when (response.status) {
                    // On Success
                    HttpURLConnection.HTTP_OK -> {
                        // Clear the current user from the preferences
                        removeCurrentUser()
                        // Return the message with Success status
                        Resource.Success(response.message)
                    }
                    // else, return the message with Unknown status
                    else -> Resource.Unknown(response.message)
                }
            }

    /**
     * Performs [user] Info update with Remote API and returns a [Single] of [Resource] from the response.
     *
     * @param user Instance of logged-in [User] with information to be updated.
     * @return A [Single] of [Resource] from the response.
     */
    fun doUpdateUserInfo(user: User): Single<Resource<String>> =
        networkService.doUpdateMyInfoCall(
            UpdateMyInfoRequest(
                name = user.name,
                profilePicUrl = user.profilePicUrl,
                tagline = user.tagline
            ),
            user.id,
            user.accessToken
        ).map { response: GeneralResponse ->
            // Transforming the [GeneralResponse] to [Resource]
            when (response.status) {
                // On Success
                HttpURLConnection.HTTP_OK -> {
                    // Save the updated user information to preferences
                    saveCurrentUser(user)
                    // Return the message with Success status
                    Resource.Success(response.message)
                }
                // else, return the message with Unknown status
                else -> Resource.Unknown(response.message)
            }
        }
}