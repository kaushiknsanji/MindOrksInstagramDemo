package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass common for all the Bottom Navigation Fragments of the [MainActivity] to communicate
 * with their [MainActivity].
 *
 * @author Kaushik N Sanji
 */
class MainSharedViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Post created and uploaded
    val postPublished: MutableLiveData<Event<Post>> = MutableLiveData()
    // LiveData for redirecting to HomeFragment
    val redirectHome: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called by the [com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoFragment]
     * when a new Instagram Post is created.
     *
     * Publishes the [newPost] to the [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment]
     * to show and also navigates to the fragment.
     *
     * @param newPost [Post] instance containing the information of the new Post created.
     */
    fun onPostCreated(newPost: Post) {
        // Publish the Post for the [HomeFragment] to display
        postPublished.postValue(Event(newPost))
        // Redirect to [HomeFragment]
        redirectHome.postValue(Event(true))
    }

}