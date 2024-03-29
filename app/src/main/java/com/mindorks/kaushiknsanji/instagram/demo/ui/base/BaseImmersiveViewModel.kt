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

package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseViewModel] abstract subclass for providing abstraction to common tasks
 * of Fullscreen [BaseImmersiveActivity].
 *
 * @author Kaushik N Sanji
 */
abstract class BaseImmersiveViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for Fullscreen Toggle events
    val toggleFullscreen: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for System bars visibility state change events which can be used
    // to show/hide any other views accordingly and can also be used to toggle fullscreen
    val systemBarsVisibilityState: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Called by the [BaseImmersiveActivity] when the User gestures with a tap on a view.
     *
     * Triggers an event to toggle the Fullscreen Immersive mode based on
     * the current [systemBarsVisibilityState].
     */
    fun onToggleFullscreen() {
        systemBarsVisibilityState.value?.let {
            toggleFullscreen.postValue(
                Event(
                    it.peekContent()
                )
            )
        }
    }

    /**
     * Called by the [BaseImmersiveActivity] when there is a change in the visibility state
     * of System bars.
     *
     * Triggers the state change event to [systemBarsVisibilityState] LiveData.
     *
     * @param isVisible When `true`, System bars is currently visible; `false` otherwise.
     */
    fun onUpdateSystemBarsVisibilityState(isVisible: Boolean) {
        systemBarsVisibilityState.postValue(Event(isVisible))
    }

}