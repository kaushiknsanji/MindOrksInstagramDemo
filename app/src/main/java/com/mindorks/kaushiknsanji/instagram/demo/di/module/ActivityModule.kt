package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityScope
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.SharedConfirmOptionDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.SharedPhotoOptionsViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.SharedProgressTextViewModel
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
    ): SplashViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(SplashViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(SplashViewModel::class.java)


    /**
     * Provides instance of [LoginViewModel]
     */
    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): LoginViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(LoginViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        LoginViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(LoginViewModel::class.java)

    /**
     * Provides instance of [SignUpViewModel]
     */
    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SignUpViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(SignUpViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        SignUpViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(SignUpViewModel::class.java)

    /**
     * Provides instance of [MainViewModel]
     */
    @Provides
    fun provideMainViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(MainViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        MainViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(MainViewModel::class.java)

    /**
     * Provides instance of [MainSharedViewModel]
     */
    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(MainSharedViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(MainSharedViewModel::class.java)

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
    ): EditProfileViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(EditProfileViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        EditProfileViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            tempDirectory,
            userRepository,
            photoRepository
        )
    }).get(EditProfileViewModel::class.java)

    /**
     * Provides instance of [SharedProgressTextViewModel]
     */
    @Provides
    fun provideSharedProgressTextViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedProgressTextViewModel = ViewModelProviders.of(activity,
        ViewModelProviderFactory(SharedProgressTextViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedProgressTextViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        }).get(SharedProgressTextViewModel::class.java)

    /**
     * Provides instance of [SharedPhotoOptionsViewModel]
     */
    @Provides
    fun provideSharedPhotoOptionsViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedPhotoOptionsViewModel = ViewModelProviders.of(activity,
        ViewModelProviderFactory(SharedPhotoOptionsViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedPhotoOptionsViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        }).get(SharedPhotoOptionsViewModel::class.java)

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
    fun providePostLikesAdapter() = PostLikesAdapter(activity.lifecycle)

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
    ): PostLikeViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(PostLikeViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        PostLikeViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository
        )
    }).get(PostLikeViewModel::class.java)

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
    ): PostDetailViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(PostDetailViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        PostDetailViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository
        )
    }).get(PostDetailViewModel::class.java)

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
        ViewModelProviders.of(activity, ViewModelProviderFactory(ImmersivePhotoViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            ImmersivePhotoViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository
            )
        }).get(ImmersivePhotoViewModel::class.java)

    /**
     * Provides instance of [SharedConfirmOptionDialogViewModel]
     */
    @Provides
    fun provideSharedConfirmOptionDialogViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedConfirmOptionDialogViewModel = ViewModelProviders.of(activity,
        ViewModelProviderFactory(SharedConfirmOptionDialogViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedConfirmOptionDialogViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        }).get(SharedConfirmOptionDialogViewModel::class.java)
}