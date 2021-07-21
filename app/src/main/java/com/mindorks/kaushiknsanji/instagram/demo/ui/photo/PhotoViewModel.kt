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

package com.mindorks.kaushiknsanji.instagram.demo.ui.photo

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.IMAGE_MAX_HEIGHT_SCALE
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import java.io.InputStream

/**
 * [BaseViewModel] subclass for [PhotoFragment]
 *
 * @property directory [File] directory for saving the optimized images for Upload.
 * @param userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 * @property photoRepository [PhotoRepository] instance for uploading a Photo.
 *
 * @author Kaushik N Sanji
 */
class PhotoViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val directory: File,
    userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the photo and post creation loading progress indication
    val loadingProgress: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the Post created and uploaded
    val postPublished: MutableLiveData<Event<Post>> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when an Image was picked by the user from the Gallery while creating a Post.
     *
     * @param inputStream [InputStream] instance to the URI of the Image Picked.
     */
    fun onGalleryImageSelected(inputStream: InputStream) {
        // Start the [loadingProgress] indication
        loadingProgress.postValue(Resource.Loading(R.string.progress_photo_uploading_image))
        // Construct a SingleSource for saving the [inputStream] to an Image File, and save its resulting disposable
        compositeDisposable.add(
            // Create a SingleSource to save the [inputStream] to an Image File, so that the operation can be
            // done in the background
            Single.fromCallable {
                ImageUtils.saveImageToFile(
                    inputStream,
                    directory, // Directory to save the resulting Image File
                    "TMP_IMG_PICKED", // Name of the resulting Image File
                    IMAGE_MAX_HEIGHT_SCALE
                )
            }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { imageFile: File? ->
                        imageFile?.run {
                            // When we have the resulting Image File, get its bounds, upload the Photo and create a Post
                            ImageUtils.getImageSize(this)?.let { imageBounds: Pair<Int, Int> ->
                                uploadPhotoAndCreatePost(this, imageBounds)
                            } ?: run {
                                // When decoding of the Image Bounds failed, display an error to the user requesting to retry
                                loadingProgress.postValue(Resource.Success()) // Stop the [loadingProgress] indication
                                messageStringId.postValue(Resource.Error(R.string.error_retry))
                            }
                        } ?: run {
                            // When we do not have the resulting Image File, display an error to the user requesting to retry
                            loadingProgress.postValue(Resource.Success()) // Stop the [loadingProgress] indication
                            messageStringId.postValue(Resource.Error(R.string.error_retry))
                        }

                    },
                    // OnError
                    {
                        // When the Image save process failed, display an error to the user requesting to retry
                        loadingProgress.postValue(Resource.Success()) // Stop the [loadingProgress] indication
                        messageStringId.postValue(Resource.Error(R.string.error_retry))
                    }
                )
        )

    }

    /**
     * Called when a Photo was clicked by the user while creating a Post.
     *
     * @param cameraImageProcessor An action lambda that optimizes the Image captured and returns its file path.
     */
    fun onPhotoSnapped(cameraImageProcessor: () -> String) {
        // Start the [loadingProgress] indication
        loadingProgress.postValue(Resource.Loading(R.string.progress_photo_uploading_image))
        // Construct a SingleSource for the lambda, and save its resulting disposable
        compositeDisposable.add(
            // Create a SingleSource to the lambda [cameraImageProcessor], so that the operation can be
            // done in the background
            Single.fromCallable {
                cameraImageProcessor()
            }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { imagePath: String ->
                        File(imagePath).apply {
                            // Get its bounds, upload the Photo and create a Post
                            ImageUtils.getImageSize(this)?.let { imageBounds: Pair<Int, Int> ->
                                uploadPhotoAndCreatePost(this, imageBounds)
                            } ?: run {
                                // When decoding of the Image Bounds failed, display an error to the user
                                // requesting to retry
                                loadingProgress.postValue(Resource.Success()) // Stop the [loadingProgress] indication
                                messageStringId.postValue(Resource.Error(R.string.error_retry))
                            }
                        }
                    },
                    // OnError
                    {
                        // When the lambda [cameraImageProcessor] process failed, display an error to the user
                        // requesting to retry
                        loadingProgress.postValue(Resource.Success()) // Stop the [loadingProgress] indication
                        messageStringId.postValue(Resource.Error(R.string.error_retry))
                    }
                )
        )
    }

    /**
     * Uploads the Photo pointed to by the [imageFile] and then creates an Instagram Post.
     *
     * @param imageFile [File] pointing to the Image to be uploaded.
     * @param imageBounds [Pair] of Image's width to height
     */
    private fun uploadPhotoAndCreatePost(imageFile: File, imageBounds: Pair<Int, Int>) {
        compositeDisposable.add(
            // Make the API Call for uploading the Photo and save the resulting disposable
            photoRepository
                .uploadPhoto(imageFile, user)
                .flatMap { imageUrl: String ->
                    // Make the API Call to create a Post using the Image URL obtained from the previous call
                    postRepository.createPost(imageUrl, imageBounds.first, imageBounds.second, user)
                }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { newPost: Post ->
                        // Publish the newly created Post
                        postPublished.postValue(Event(newPost))
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(Resource.Success())
                    },
                    // OnError
                    { throwable: Throwable? ->
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(Resource.Success())
                        // Handle and display the appropriate error
                        handleNetworkError(throwable)
                    }
                )
        )
    }

}