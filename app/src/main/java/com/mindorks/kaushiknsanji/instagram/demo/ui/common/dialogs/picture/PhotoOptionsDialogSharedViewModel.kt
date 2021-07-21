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

package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseDialogViewModel] subclass for [PhotoOptionsDialogFragment].
 *
 * @author Kaushik N Sanji
 */
class PhotoOptionsDialogSharedViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseDialogViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for launching System Camera
    val launchCamera: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for launching System Gallery
    val launchGallery: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when the "Camera" Option is clicked. Sends an [Event] to launch the System Camera.
     */
    fun onOptionCamera() = launchCamera.postValue(Event(true))


    /**
     * Called when the "Gallery" Option is clicked. Sends an [Event] to launch the System Gallery.
     */
    fun onOptionGallery() = launchGallery.postValue(Event(true))

}