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