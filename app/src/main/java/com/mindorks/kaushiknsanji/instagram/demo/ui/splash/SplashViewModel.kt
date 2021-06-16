package com.mindorks.kaushiknsanji.instagram.demo.ui.splash

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

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

    // LiveData for launching MainActivity
    // (Map is used to store and pass the required data for launching the Activity)
    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    // LiveData for launching LoginActivity
    val launchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        // Create a Single and save the resulting disposable
        compositeDisposable.add(
            // Create a Single from a timer of 1 second, for the splash to be seen
            Single.timer(1, TimeUnit.SECONDS, schedulerProvider.computation())
                .map {
                    // Map the timer value to the logged-in User information
                    // retrieved from the repository
                    userRepository.getCurrentUserOrNull()
                }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { user: User? ->
                        user?.let {
                            // When the logged-in user information is already saved,
                            // launch MainActivity
                            launchMain.postValue(Event(emptyMap()))
                        } ?: run {
                            // When there is NO logged-in user information,
                            // launch LoginActivity
                            launchLogin.postValue(Event(emptyMap()))
                        }
                    },
                    // OnError
                    {
                        // When there is an error, do not show any error. Just launch LoginActivity
                        // to re-login the user
                        launchLogin.postValue(Event(emptyMap()))
                    }
                )
        )
    }

}