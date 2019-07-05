package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.ImageUploadResponse
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.TYPE_IMAGE
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            RequestBody.create( // Constructing RequestBody
                MediaType.parse(TYPE_IMAGE), // Media type for Images
                imageFile                          // Image File
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