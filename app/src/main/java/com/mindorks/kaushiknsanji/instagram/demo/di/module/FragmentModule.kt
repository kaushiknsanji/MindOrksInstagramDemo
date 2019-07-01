package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Dagger Module for creating and exposing dependencies, tied to the Fragment Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.requireContext()

    /**
     * Provides instance of [HomeViewModel]
     */
    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): HomeViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(HomeViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        HomeViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(HomeViewModel::class.java)

    /**
     * Provides instance of [PhotoViewModel]
     */
    @Provides
    fun providePhotoViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): PhotoViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(PhotoViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        PhotoViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(PhotoViewModel::class.java)

    /**
     * Provides instance of [ProfileViewModel]
     */
    @Provides
    fun provideProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): ProfileViewModel = ViewModelProviders.of(fragment, ViewModelProviderFactory(ProfileViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        ProfileViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(ProfileViewModel::class.java)

}