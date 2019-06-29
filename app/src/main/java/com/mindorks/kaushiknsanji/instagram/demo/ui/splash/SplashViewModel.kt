package com.mindorks.kaushiknsanji.instagram.demo.ui.splash

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass for the [SplashActivity]
 *
 * @property userRepository [UserRepository] instance for User data.
 *
 * @author Kaushik N Sanji
 */
class SplashViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData Event Wrapper to launch MainActivity
    // (Map is used to store and pass the required data for launching the Activity)
    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    // LiveData Event Wrapper to launch LoginActivity
    val launchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        if (userRepository.getCurrentUser() != null) {
            // When the logged-in user information is already saved, launch MainActivity
            launchMain.postValue(Event(emptyMap()))
        } else {
            // When there is NO logged-in user information, launch LoginActivity
            launchLogin.postValue(Event(emptyMap()))
        }
    }
}