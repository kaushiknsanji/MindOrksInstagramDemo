package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs.UserPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.LoginRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.SignUpRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.LoginResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.SignUpResponse
import io.reactivex.Single
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
    }

    /**
     * Removes logged-in user information from [userPreferences]
     */
    fun removeCurrentUser() {
        userPreferences.removeUserId()
        userPreferences.removeUserName()
        userPreferences.removeUserEmail()
        userPreferences.removeAccessToken()
    }

    /**
     * Retrieves the logged-in user information from [userPreferences] if present.
     */
    fun getCurrentUser(): User? {
        // Reading from userPreferences
        val userId = userPreferences.getUserId()
        val userName = userPreferences.getUserName()
        val userEmail = userPreferences.getUserEmail()
        val accessToken = userPreferences.getAccessToken()

        // Returning user information if present, else null
        return if (userId !== null && userName != null && userEmail != null && accessToken != null)
            User(userId, userName, userEmail, accessToken)
        else
            null
    }

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
                    accessToken = response.accessToken
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
                    accessToken = response.accessToken
                )
            }

}