package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.SharedConfirmOptionDialogViewModel
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
    ): SharedProgressTextViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(SharedProgressTextViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedProgressTextViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[SharedProgressTextViewModel::class.java]

    /**
     * Provides instance of [SharedPhotoOptionsViewModel]
     */
    @Provides
    fun provideSharedPhotoOptionsViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedPhotoOptionsViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(SharedPhotoOptionsViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedPhotoOptionsViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[SharedPhotoOptionsViewModel::class.java]

    /**
     * Provides instance of [SharedConfirmOptionDialogViewModel]
     */
    @Provides
    fun provideSharedConfirmOptionDialogViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): SharedConfirmOptionDialogViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(SharedConfirmOptionDialogViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            SharedConfirmOptionDialogViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[SharedConfirmOptionDialogViewModel::class.java]

}