package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
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
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.SharedProgressTextViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts.PostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.DIRECTORY_TEMP
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.IMAGE_MAX_HEIGHT_SCALE
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.JPEG_COMPRESSION_QUALITY
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
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
    ): HomeViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(HomeViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        HomeViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository
        )
    }).get(HomeViewModel::class.java)

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
    ): PhotoViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(PhotoViewModel::class) {
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
    }).get(PhotoViewModel::class.java)

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
    ): ProfileViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(ProfileViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        ProfileViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository, postRepository)
    }).get(ProfileViewModel::class.java)

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
        ViewModelProviders.of(fragment.requireActivity(), ViewModelProviderFactory(MainSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
        }).get(MainSharedViewModel::class.java)

    /**
     * Provides instance of [ProfilePostsAdapter]
     */
    @Provides
    fun provideProfilePostsAdapter() =
        ProfilePostsAdapter(fragment.lifecycle, (fragment as? ProfilePostsAdapter.Listener))

    /**
     * Provides instance of [SharedProgressTextViewModel]
     */
    @Provides
    fun provideSharedProgressTextViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedProgressTextViewModel = ViewModelProviders.of(fragment.requireActivity(),
        ViewModelProviderFactory(SharedProgressTextViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedProgressTextViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        }).get(SharedProgressTextViewModel::class.java)

}