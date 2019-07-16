package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.SharedPhotoOptionsViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.SharedProgressTextViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Dagger Module for creating and exposing dependencies, tied to the DialogFragment Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class DialogFragmentModule(private val fragment: BaseDialogFragment<*>) {

    /**
     * Provides instance of fragment [Context]
     */
    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.requireContext()

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

    /**
     * Provides instance of [SharedPhotoOptionsViewModel]
     */
    @Provides
    fun provideSharedPhotoOptionsViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedPhotoOptionsViewModel = ViewModelProviders.of(fragment.requireActivity(),
        ViewModelProviderFactory(SharedPhotoOptionsViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedPhotoOptionsViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        }).get(SharedPhotoOptionsViewModel::class.java)

}