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

import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.ImageUploadResponse
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.TYPE_IMAGE
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Instagram Post's Photo management.
 *
 * @property networkService Instance of Retrofit API Service [NetworkService] provided by Dagger.
 * @constructor Instance of [PhotoRepository] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class PhotoRepository @Inject constructor(
    private val networkService: NetworkService
) {

    /**
     * Performs the upload of the Image pointed to by the [imageFile] to the server via the Remote API
     * and returns a [Single] of a [String] representing the URL of the Image uploaded.
     *
     * @param imageFile [File] pointing to the Image to be uploaded.
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of a [String] representing the URL of the Image uploaded.
     */
    fun uploadPhoto(imageFile: File, user: User): Single<String> =
        MultipartBody.Part.createFormData( // Creating Multipart Form Data for Image Upload
            "image", // Key for the JSON String in Form Data
            imageFile.name, // Filename of the [imageFile]
            imageFile                          // Image File
                .asRequestBody( // Constructing RequestBody
                    TYPE_IMAGE.toMediaTypeOrNull() // Media type for Images
                )
        ).run {
            // Calling the Remote API to Upload the Image, with the constructed Multipart
            networkService.doImageUploadCall(
                this,
                user.id,
                user.accessToken
            ).map { response: ImageUploadResponse ->
                // Transforming [ImageUploadResponse] to the URL of the Image uploaded
                response.data.imageUrl
            }
        }

}