package com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * [SharedPreferences] wrapper for the logged-in [com.mindorks.kaushiknsanji.instagram.demo.data.model.User] information.
 *
 * @property prefs Instance of [SharedPreferences] provided by Dagger.
 * @constructor Creates an Instance of [UserPreferences]
 *
 * @author Kaushik N Sanji
 */
class UserPreferences @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        // Constants for the Preference Keys
        const val KEY_USER_ID = "PREF_KEY_USER_ID"
        const val KEY_USER_NAME = "PREF_KEY_USER_NAME"
        const val KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"
        const val KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        const val KEY_PROFILE_PIC_URL = "PREF_KEY_PROFILE_PIC_URL"
        const val KEY_TAGLINE = "PREF_KEY_TAGLINE"
    }

    /**
     * Getter for the [KEY_USER_ID]
     */
    fun getUserId(): String? =
        prefs.getString(KEY_USER_ID, null)

    /**
     * Saves the [userId] in [KEY_USER_ID]
     */
    fun setUserId(userId: String) =
        prefs.edit().putString(KEY_USER_ID, userId).apply()

    /**
     * Deletes the [KEY_USER_ID]
     */
    fun removeUserId() =
        prefs.edit().remove(KEY_USER_ID).apply()

    /**
     * Getter for the [KEY_USER_NAME]
     */
    fun getUserName(): String? =
        prefs.getString(KEY_USER_NAME, null)

    /**
     * Saves the [userName] in [KEY_USER_NAME]
     */
    fun setUserName(userName: String) =
        prefs.edit().putString(KEY_USER_NAME, userName).apply()

    /**
     * Deletes the [KEY_USER_NAME]
     */
    fun removeUserName() =
        prefs.edit().remove(KEY_USER_NAME).apply()

    /**
     * Getter for the [KEY_USER_EMAIL]
     */
    fun getUserEmail(): String? =
        prefs.getString(KEY_USER_EMAIL, null)

    /**
     * Saves the [email] in [KEY_USER_EMAIL]
     */
    fun setUserEmail(email: String) =
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()

    /**
     * Deletes the [KEY_USER_EMAIL]
     */
    fun removeUserEmail() =
        prefs.edit().remove(KEY_USER_EMAIL).apply()

    /**
     * Getter for the [KEY_ACCESS_TOKEN]
     */
    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS_TOKEN, null)

    /**
     * Saves the [token] in [KEY_ACCESS_TOKEN]
     */
    fun setAccessToken(token: String) =
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()

    /**
     * Deletes the [KEY_ACCESS_TOKEN]
     */
    fun removeAccessToken() =
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()

    /**
     * Getter for the [KEY_PROFILE_PIC_URL]
     */
    fun getProfilePicUrl(): String? =
        prefs.getString(KEY_PROFILE_PIC_URL, null)

    /**
     * Saves the Profile Picture [url] in [KEY_PROFILE_PIC_URL]
     */
    fun setProfilePicUrl(url: String?) =
        prefs.edit().putString(KEY_PROFILE_PIC_URL, url).apply()

    /**
     * Deletes the [KEY_PROFILE_PIC_URL]
     */
    fun removeProfilePicUrl() =
        prefs.edit().remove(KEY_PROFILE_PIC_URL).apply()

    /**
     * Getter for the [KEY_TAGLINE]
     */
    fun getTagline(): String? =
        prefs.getString(KEY_TAGLINE, null)

    /**
     * Saves the [tagline] in [KEY_TAGLINE]
     */
    fun setTagline(tagline: String?) =
        prefs.edit().putString(KEY_TAGLINE, tagline).apply()

    /**
     * Deletes the [KEY_TAGLINE]
     */
    fun removeTagline() =
        prefs.edit().remove(KEY_TAGLINE).apply()

}