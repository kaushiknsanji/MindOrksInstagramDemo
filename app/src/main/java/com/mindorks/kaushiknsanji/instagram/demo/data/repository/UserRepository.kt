package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs.UserPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import javax.inject.Inject

/**
 * Repository for User data management.
 *
 * @property networkService Instance of Retrofit API Service [NetworkService] provided by Dagger.
 * @property databaseService Instance of RoomDatabase [DatabaseService] provided by Dagger.
 * @property userPreferences Instance of [UserPreferences] which is a SharedPreferences wrapper for User data, provided by Dagger.
 * @constructor Creates an Instance of [UserRepository]
 *
 * @author Kaushik N Sanji
 */
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

}