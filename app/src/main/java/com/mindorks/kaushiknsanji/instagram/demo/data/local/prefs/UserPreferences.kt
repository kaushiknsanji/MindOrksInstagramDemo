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

package com.mindorks.kaushiknsanji.instagram.demo.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * [SharedPreferences] wrapper for logged-in [com.mindorks.kaushiknsanji.instagram.demo.data.model.User] information.
 *
 * @property prefs Instance of [SharedPreferences] provided by Dagger.
 * @constructor Creates an Instance of [UserPreferences]
 *
 * @author Kaushik N Sanji
 */
class UserPreferences @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        // Constants for Preference Keys
        const val KEY_USER_ID = "PREF_KEY_USER_ID"
        const val KEY_USER_NAME = "PREF_KEY_USER_NAME"
        const val KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"
        const val KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        const val KEY_REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN"
        const val KEY_PROFILE_PIC_URL = "PREF_KEY_PROFILE_PIC_URL"
        const val KEY_TAGLINE = "PREF_KEY_TAGLINE"
    }

    /**
     * Getter for [KEY_USER_ID]
     */
    fun getUserId(): String? =
        prefs.getString(KEY_USER_ID, null)

    /**
     * Saves [userId] in [KEY_USER_ID]
     */
    fun setUserId(userId: String) =
        prefs.edit().putString(KEY_USER_ID, userId).apply()

    /**
     * Deletes [KEY_USER_ID]
     */
    fun removeUserId() =
        prefs.edit().remove(KEY_USER_ID).apply()

    /**
     * Getter for [KEY_USER_NAME]
     */
    fun getUserName(): String? =
        prefs.getString(KEY_USER_NAME, null)

    /**
     * Saves [userName] in [KEY_USER_NAME]
     */
    fun setUserName(userName: String) =
        prefs.edit().putString(KEY_USER_NAME, userName).apply()

    /**
     * Deletes [KEY_USER_NAME]
     */
    fun removeUserName() =
        prefs.edit().remove(KEY_USER_NAME).apply()

    /**
     * Getter for [KEY_USER_EMAIL]
     */
    fun getUserEmail(): String? =
        prefs.getString(KEY_USER_EMAIL, null)

    /**
     * Saves [email] in [KEY_USER_EMAIL]
     */
    fun setUserEmail(email: String) =
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()

    /**
     * Deletes [KEY_USER_EMAIL]
     */
    fun removeUserEmail() =
        prefs.edit().remove(KEY_USER_EMAIL).apply()

    /**
     * Getter for [KEY_ACCESS_TOKEN]
     */
    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS_TOKEN, null)

    /**
     * Getter for [KEY_REFRESH_TOKEN]
     */
    fun getRefreshToken(): String? =
        prefs.getString(KEY_REFRESH_TOKEN, null)

    /**
     * Saves [token] in [KEY_ACCESS_TOKEN]
     */
    fun setAccessToken(token: String) =
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()

    /**
     * Saves [refreshToken] in [KEY_REFRESH_TOKEN]
     */
    fun setRefreshToken(refreshToken: String) =
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()

    /**
     * Deletes [KEY_ACCESS_TOKEN]
     */
    fun removeAccessToken() =
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()

    /**
     * Deletes [KEY_REFRESH_TOKEN]
     */
    fun removeRefreshToken() =
        prefs.edit().remove(KEY_REFRESH_TOKEN).apply()

    /**
     * Getter for [KEY_PROFILE_PIC_URL]
     */
    fun getProfilePicUrl(): String? =
        prefs.getString(KEY_PROFILE_PIC_URL, null)

    /**
     * Saves Profile Picture [url] in [KEY_PROFILE_PIC_URL]
     */
    fun setProfilePicUrl(url: String?) =
        prefs.edit().putString(KEY_PROFILE_PIC_URL, url).apply()

    /**
     * Deletes [KEY_PROFILE_PIC_URL]
     */
    fun removeProfilePicUrl() =
        prefs.edit().remove(KEY_PROFILE_PIC_URL).apply()

    /**
     * Getter for [KEY_TAGLINE]
     */
    fun getTagline(): String? =
        prefs.getString(KEY_TAGLINE, null)

    /**
     * Saves [tagline] in [KEY_TAGLINE]
     */
    fun setTagline(tagline: String?) =
        prefs.edit().putString(KEY_TAGLINE, tagline).apply()

    /**
     * Deletes [KEY_TAGLINE]
     */
    fun removeTagline() =
        prefs.edit().remove(KEY_TAGLINE).apply()

}