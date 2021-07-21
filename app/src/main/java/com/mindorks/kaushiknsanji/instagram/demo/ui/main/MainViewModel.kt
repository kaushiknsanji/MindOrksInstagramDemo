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

package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass for [MainActivity]
 *
 * @author Kaushik N Sanji
 */
class MainViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for navigating to HomeFragment
    val navigateHome: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for navigating to PhotoFragment
    val navigatePhoto: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for navigating to ProfileFragment
    val navigateProfile: MutableLiveData<Event<Boolean>> = MutableLiveData()

    init {
        // When this ViewModel is first initialized..

        // Load HomeFragment by default
        onHomeSelected()
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when the Home button of the Bottom Navigation Menu is clicked.
     */
    fun onHomeSelected() {
        navigateHome.postValue(Event(true))
    }

    /**
     * Called when the "Add Photos" button of the Bottom Navigation Menu is clicked.
     */
    fun onPhotoSelected() {
        navigatePhoto.postValue(Event(true))
    }

    /**
     * Called when the Profile button of the Bottom Navigation Menu is clicked.
     */
    fun onProfileSelected() {
        navigateProfile.postValue(Event(true))
    }

}