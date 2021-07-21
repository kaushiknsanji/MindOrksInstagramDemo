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

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.BitStateTracker
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseViewModel] abstract subclass for providing abstraction to common tasks of
 * [androidx.fragment.app.DialogFragment].
 *
 * @author Kaushik N Sanji
 */
abstract class BaseDialogViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    companion object {
        // Constant used as keys for tracking buttons initialized
        private const val KEY_BUTTON_POSITIVE = "PositiveButton"
        private const val KEY_BUTTON_NEGATIVE = "NegativeButton"
        private const val KEY_BUTTON_NEUTRAL = "NeutralButton"

        // Getter for the array of all the Buttons to be tracked for initialized state
        private val buttonsTrackedForInitState
            get() = arrayOf(
                KEY_BUTTON_POSITIVE,
                KEY_BUTTON_NEGATIVE,
                KEY_BUTTON_NEUTRAL
            )
    }

    // LiveData for the Dialog Title
    val titleDialogId: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the Dialog Message
    val messageDialogId: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the Dialog's Positive Button Name
    val positiveButtonTextId: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the Dialog's Negative Button Name
    val negativeButtonTextId: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the Dialog's Neutral Button Name
    val neutralButtonTextId: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for Positive Button action events
    val actionPositiveButton: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for Negative Button action events
    val actionNegativeButton: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for Neutral Button action events
    val actionNeutralButton: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // Function to convert Resource to Boolean based on whether there is Data in the Resource or not
    private val resourceDataNotNull: (resourceWrapper: Resource<Int>) -> Boolean =
        { resourceWrapper: Resource<Int> ->
            resourceWrapper.peekData() != null
        }

    // Transforms [positiveButtonTextId] to see if the Positive Button has been set or not
    private val hasPositiveButton: LiveData<Boolean> = positiveButtonTextId.map(resourceDataNotNull)

    // Transforms [negativeButtonTextId] to see if the Negative Button has been set or not
    private val hasNegativeButton: LiveData<Boolean> = negativeButtonTextId.map(resourceDataNotNull)

    // Transforms [neutralButtonTextId] to see if the Neutral Button has been set or not
    private val hasNeutralButton: LiveData<Boolean> = neutralButtonTextId.map(resourceDataNotNull)

    // BitStateTracker to help with merging any button's initialized state to button panel's visibility
    private val buttonInitStateMerger = BitStateTracker(
        buttonsTrackedForInitState, // Buttons to track
        Int::or, // Bitwise operation for True state
        { bitState: Int, bitKey: Int ->
            // Bitwise operation for False state
            bitState and bitKey.inv()
        },
        { bitState: Int ->
            // BitState transformation to visibility boolean
            bitState != 0
        }
    )

    // MediatorLiveData to see if any Buttons have been initialized in the Dialog,
    // for updating the Button Panel visibility accordingly
    val hasAnyButtons: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // Add sources for the tracked Buttons
        addSource(hasPositiveButton) { newValue: Boolean ->
            postValue(buttonInitStateMerger.updateState(KEY_BUTTON_POSITIVE, newValue))
        }
        addSource(hasNegativeButton) { newValue: Boolean ->
            postValue(buttonInitStateMerger.updateState(KEY_BUTTON_NEGATIVE, newValue))
        }
        addSource(hasNeutralButton) { newValue: Boolean ->
            postValue(buttonInitStateMerger.updateState(KEY_BUTTON_NEUTRAL, newValue))
        }
    }

    /**
     * Called when the Dialog Title needs to be set/changed to [titleId]
     */
    private fun onDialogTitleTextChange(titleId: Resource<Int>) {
        titleDialogId.postValue(titleId)
    }

    /**
     * Called when the Dialog Message needs to be set/changed to [messageId]
     */
    private fun onDialogMessageTextChange(messageId: Resource<Int>) {
        messageDialogId.postValue(messageId)
    }

    /**
     * Called when the Dialog's Positive Button Name needs to be set/changed to [nameId]
     */
    private fun onDialogPositiveButtonTextChange(nameId: Resource<Int>) {
        positiveButtonTextId.postValue(nameId)
    }

    /**
     * Called when the Dialog's Negative Button Name needs to be set/changed to [nameId]
     */
    private fun onDialogNegativeButtonTextChange(nameId: Resource<Int>) {
        negativeButtonTextId.postValue(nameId)
    }

    /**
     * Called when the Dialog's Neutral Button Name needs to be set/changed to [nameId]
     */
    private fun onDialogNeutralButtonTextChange(nameId: Resource<Int>) {
        neutralButtonTextId.postValue(nameId)
    }

    /**
     * Called when the Dialog's Positive Button is clicked.
     * Triggers an event to [actionPositiveButton] LiveData.
     */
    fun onDialogPositiveButtonClicked() = actionPositiveButton.postValue(Event(true))

    /**
     * Called when the Dialog's Negative Button is clicked.
     * Triggers an event to [actionNegativeButton] LiveData.
     */
    fun onDialogNegativeButtonClicked() = actionNegativeButton.postValue(Event(true))

    /**
     * Called when the Dialog's Neutral Button is clicked.
     * Triggers an event to [actionNeutralButton] LiveData.
     */
    fun onDialogNeutralButtonClicked() = actionNeutralButton.postValue(Event(true))

    /**
     * Called when the Dialog's elements needs to be set/changed according to [metadata].
     * Can be overridden by subclasses to set their custom [metadata].
     */
    @CallSuper
    open fun onDialogMetadataChange(metadata: BaseDialogMetadata) {
        metadata.run {
            onDialogTitleTextChange(titleDialogId)
            onDialogMessageTextChange(messageDialogId)
            onDialogPositiveButtonTextChange(positiveButtonTextId)
            onDialogNegativeButtonTextChange(negativeButtonTextId)
            onDialogNeutralButtonTextChange(neutralButtonTextId)
        }
    }
}