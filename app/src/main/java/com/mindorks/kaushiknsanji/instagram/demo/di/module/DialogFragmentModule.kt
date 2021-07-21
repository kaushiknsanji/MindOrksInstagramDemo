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
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.factory.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

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
     * Provides instance of [PhotoOptionsDialogSharedViewModel]
     */
    @Provides
    fun providePhotoOptionsDialogSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): PhotoOptionsDialogSharedViewModel = ViewModelProvider(fragment.requireActivity(),
        ViewModelProviderFactory(PhotoOptionsDialogSharedViewModel::class) {
            // [creator] lambda that creates and returns the ViewModel instance
            PhotoOptionsDialogSharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper
            )
        })[PhotoOptionsDialogSharedViewModel::class.java]

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