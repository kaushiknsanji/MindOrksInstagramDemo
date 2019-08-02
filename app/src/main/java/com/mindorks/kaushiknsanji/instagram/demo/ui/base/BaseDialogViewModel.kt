package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

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

    /**
     * Called when the Dialog Title needs to be set/changed to [titleId]
     */
    fun onDialogTitleTextChange(titleId: Resource<Int>) {
        titleDialogId.postValue(titleId)
    }

    /**
     * Called when the Dialog Message needs to be set/changed to [messageId]
     */
    fun onDialogMessageTextChange(messageId: Resource<Int>) {
        messageDialogId.postValue(messageId)
    }

    /**
     * Called when the Dialog's Positive Button Name needs to be set/changed to [nameId]
     */
    fun onDialogPositiveButtonTextChange(nameId: Resource<Int>) {
        positiveButtonTextId.postValue(nameId)
    }

    /**
     * Called when the Dialog's Negative Button Name needs to be set/changed to [nameId]
     */
    fun onDialogNegativeButtonTextChange(nameId: Resource<Int>) {
        negativeButtonTextId.postValue(nameId)
    }

    /**
     * Called when the Dialog's Neutral Button Name needs to be set/changed to [nameId]
     */
    fun onDialogNeutralButtonTextChange(nameId: Resource<Int>) {
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
}