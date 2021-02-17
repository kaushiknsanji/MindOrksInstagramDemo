package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityScope
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo.ImmersivePhotoViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.splash.SplashViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import java.io.File

/**
 * Dagger Module for creating and exposing dependencies, tied to the Activity Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    /**
     * Provides instance of activity [Context]
     */
    @ActivityContext
    @Provides
    fun provideContext(): Context = activity

    /**
     * Provides instance of [DialogManager]
     */
    @ActivityScope
    @Provides
    fun provideDialogManager(): DialogManager = DialogManager(activity.supportFragmentManager)

    /**
     * Provides instance of Vertical [LinearLayoutManager]
     */
    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(activity)

    /**
     * Provides instance of [SplashViewModel]
     */
    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SplashViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(SplashViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
        })[SplashViewModel::class.java]


    /**
     * Provides instance of [LoginViewModel]
     */
    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): LoginViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(LoginViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            LoginViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
        })[LoginViewModel::class.java]

    /**
     * Provides instance of [SignUpViewModel]
     */
    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SignUpViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(SignUpViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SignUpViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
        })[SignUpViewModel::class.java]

    /**
     * Provides instance of [MainViewModel]
     */
    @Provides
    fun provideMainViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainViewModel = ViewModelProvider(activity, ViewModelProviderFactory(MainViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        MainViewModel(schedulerProvider, compositeDisposable, networkHelper)
    })[MainViewModel::class.java]

    /**
     * Provides instance of [MainSharedViewModel]
     */
    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(MainSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
        })[MainSharedViewModel::class.java]

    /**
     * Provides instance of [EditProfileViewModel]
     */
    @Provides
    fun provideEditProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        @TempDirectory tempDirectory: File,
        userRepository: UserRepository,
        photoRepository: PhotoRepository
    ): EditProfileViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(EditProfileViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            EditProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                tempDirectory,
                userRepository,
                photoRepository
            )
        })[EditProfileViewModel::class.java]

    /**
     * Provides instance of [ProgressTextDialogSharedViewModel]
     */
    @Provides
    fun provideProgressTextDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): ProgressTextDialogSharedViewModel = ViewModelProvider(activity,
        ViewModelProviderFactory(ProgressTextDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ProgressTextDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[ProgressTextDialogSharedViewModel::class.java]

    /**
     * Provides instance of [PhotoOptionsDialogSharedViewModel]
     */
    @Provides
    fun providePhotoOptionsDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): PhotoOptionsDialogSharedViewModel = ViewModelProvider(activity,
        ViewModelProviderFactory(PhotoOptionsDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            PhotoOptionsDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[PhotoOptionsDialogSharedViewModel::class.java]

    /**
     * Provides instance of [Camera] for taking a photo
     */
    @Provides
    fun provideParaCamera(): Camera = Camera.Builder()
        .resetToCorrectOrientation(true) // Corrects the orientation of the Bitmap based on EXIF metadata
        .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
        .setDirectory(Constants.DIRECTORY_TEMP) // Directory for saving the Temporary Image, placed in the app storage
        .setName("TMP_IMG_CAPTURE") // Fixed name of the Temporary Image file
        .setImageFormat(Camera.IMAGE_JPG) // JPG format
        .setCompression(Constants.JPEG_COMPRESSION_QUALITY) // JPEG Image compression/quality
        .setImageHeight(Constants.IMAGE_MAX_HEIGHT_SCALE) // Max height to which the Image will be scaled/downsized while respecting its aspect ratio
        .build(activity)

    /**
     * Provides instance of [PostLikesAdapter]
     */
    @Provides
    fun providePostLikesAdapter() = PostLikesAdapter(activity.lifecycle, (activity as? PostLikesAdapter.Listener))

    /**
     * Provides instance of [PostLikeViewModel]
     */
    @Provides
    fun providePostLikeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): PostLikeViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(PostLikeViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            PostLikeViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository,
                postRepository
            )
        })[PostLikeViewModel::class.java]

    /**
     * Provides instance of [PostDetailViewModel]
     */
    @Provides
    fun providePostDetailViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): PostDetailViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(PostDetailViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            PostDetailViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository,
                postRepository
            )
        })[PostDetailViewModel::class.java]

    /**
     * Provides instance of [ImmersivePhotoViewModel]
     */
    @Provides
    fun provideImmersivePhotoViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): ImmersivePhotoViewModel =
        ViewModelProvider(activity, ViewModelProviderFactory(ImmersivePhotoViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ImmersivePhotoViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository
            )
        })[ImmersivePhotoViewModel::class.java]

    /**
     * Provides instance of [ConfirmOptionDialogSharedViewModel]
     */
    @Provides
    fun provideConfirmOptionDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): ConfirmOptionDialogSharedViewModel = ViewModelProvider(activity,
        ViewModelProviderFactory(ConfirmOptionDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ConfirmOptionDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[ConfirmOptionDialogSharedViewModel::class.java]
}