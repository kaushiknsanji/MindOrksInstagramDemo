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

package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.di.FragmentScope
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts.PostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.DIRECTORY_TEMP
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.IMAGE_MAX_HEIGHT_SCALE
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.JPEG_COMPRESSION_QUALITY
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.factory.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File

/**
 * Dagger Module for creating and exposing dependencies, tied to the Fragment Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    /**
     * Provides instance of fragment [Context]
     */
    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.requireContext()

    /**
     * Provides instance of Vertical [LinearLayoutManager]
     */
    @Provides
    fun provideLinearLayoutManager(@ActivityContext context: Context): LinearLayoutManager =
        LinearLayoutManager(context)

    /**
     * Provides instance of Vertical [GridLayoutManager] with [GridLayoutManager.DEFAULT_SPAN_COUNT] span count
     */
    @Provides
    fun provideVerticalGridLayoutManager(@ActivityContext context: Context): GridLayoutManager =
        GridLayoutManager(context, GridLayoutManager.DEFAULT_SPAN_COUNT, RecyclerView.VERTICAL, false)

    /**
     * Provides instance of [DialogManager]
     */
    @FragmentScope
    @Provides
    fun provideDialogManager(): DialogManager = DialogManager(fragment.childFragmentManager)

    /**
     * Provides instance of [HomeViewModel]
     */
    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): HomeViewModel = ViewModelProvider(fragment, ViewModelProviderFactory(HomeViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        HomeViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository
        )
    })[HomeViewModel::class.java]

    /**
     * Provides instance of [PhotoViewModel]
     */
    @Provides
    fun providePhotoViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        @TempDirectory tempDirectory: File,
        userRepository: UserRepository,
        postRepository: PostRepository,
        photoRepository: PhotoRepository
    ): PhotoViewModel =
        ViewModelProvider(fragment, ViewModelProviderFactory(PhotoViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            PhotoViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                tempDirectory,
                userRepository,
                postRepository,
                photoRepository
            )
        })[PhotoViewModel::class.java]

    /**
     * Provides instance of [ProfileViewModel]
     */
    @Provides
    fun provideProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): ProfileViewModel =
        ViewModelProvider(fragment, ViewModelProviderFactory(ProfileViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository,
                postRepository
            )
        })[ProfileViewModel::class.java]

    /**
     * Provides instance of [PostsAdapter]
     */
    @Provides
    fun providePostsAdapter() = PostsAdapter(fragment.lifecycle, (fragment as? PostsAdapter.Listener))

    /**
     * Provides instance of [Camera] for taking a photo
     */
    @Provides
    fun provideParaCamera(): Camera = Camera.Builder()
        .resetToCorrectOrientation(true) // Corrects the orientation of the Bitmap based on EXIF metadata
        .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
        .setDirectory(DIRECTORY_TEMP) // Directory for saving the Temporary Image, placed in the app storage
        .setName("TMP_IMG_CAPTURE") // Fixed name of the Temporary Image file
        .setImageFormat(Camera.IMAGE_JPG) // JPG format
        .setCompression(JPEG_COMPRESSION_QUALITY) // JPEG Image compression/quality
        .setImageHeight(IMAGE_MAX_HEIGHT_SCALE) // Max height to which the Image will be scaled/downsized while respecting its aspect ratio
        .build(fragment)

    /**
     * Provides instance of [MainSharedViewModel]
     */
    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel =
        ViewModelProvider(
            fragment.requireActivity(),
            ViewModelProviderFactory(MainSharedViewModel::class) {
                // [creator] lambda that creates and returns the ViewModel instance
                MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
            })[MainSharedViewModel::class.java]

    /**
     * Provides instance of [ProfilePostsAdapter]
     */
    @Provides
    fun provideProfilePostsAdapter() =
        ProfilePostsAdapter(fragment.lifecycle, (fragment as? ProfilePostsAdapter.Listener))

    /**
     * Provides instance of [ProgressTextDialogSharedViewModel]
     */
    @Provides
    fun provideProgressTextDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): ProgressTextDialogSharedViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(ProgressTextDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ProgressTextDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[ProgressTextDialogSharedViewModel::class.java]

    /**
     * Provides instance of [ConfirmOptionDialogSharedViewModel]
     */
    @Provides
    fun provideConfirmOptionDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): ConfirmOptionDialogSharedViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(ConfirmOptionDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ConfirmOptionDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[ConfirmOptionDialogSharedViewModel::class.java]

}