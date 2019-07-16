package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    // LiveData triggered when the Post is created and uploaded
    private val postPublished: MutableLiveData<Post> = MutableLiveData()

    // LiveData for redirecting to HomeFragment
    val redirectHome: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for launching EditProfileActivity
    val launchEditProfile: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    // LiveData triggered when the User Profile information is updated
    private val userProfileInfoChanged: MutableLiveData<Boolean> = MutableLiveData()

    // Transform the [postPublished] to trigger an event with the new Post for [HomeFragment]
    val postPublishUpdateToHome: LiveData<Event<Post>> = Transformations.map(postPublished) { newPost: Post ->
        Event(newPost)
    }

    // Transform the [postPublished] to trigger an event with the new Post for [ProfileFragment]
    val postPublishUpdateToProfile: LiveData<Event<Post>> = Transformations.map(postPublished) { newPost: Post ->
        Event(newPost)
    }

    // Transform the [userProfileInfoChanged] to trigger the profile info update event for [HomeFragment]
    val userProfileInfoUpdateToHome: LiveData<Event<Boolean>> =
        Transformations.map(userProfileInfoChanged) { reload: Boolean ->
            Event(reload)
        }

    // Transform the [userProfileInfoChanged] to trigger the profile info update event for [ProfileFragment]
    val userProfileInfoUpdateToProfile: LiveData<Event<Boolean>> =
        Transformations.map(userProfileInfoChanged) { reload: Boolean ->
            Event(reload)
        }

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
     * and [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment] to show,
     * and also navigates to the [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment].
     *
     * @param newPost [Post] instance containing the information of the new Post created.
     */
    fun onPostCreated(newPost: Post) {
        // Publish the Post for the [HomeFragment] and [ProfileFragment] to display
        postPublished.postValue(newPost)
        // Redirect to [HomeFragment]
        redirectHome.postValue(Event(true))
    }

    /**
     * Called when the user clicks on "Edit Profile" button,
     * in [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity].
     *
     * Triggers an event to launch the Activity with the request code for results.
     */
    fun onEditProfileRequest() {
        launchEditProfile.postValue(Event(emptyMap()))
    }

    /**
     * Called when the user has successfully updated the Profile information
     * through [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity].
     *
     * Triggers an event for the [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment]
     * and [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment] to reload Profile information.
     */
    fun onEditProfileSuccess() {
        // Update [userProfileInfoChanged] to trigger an event to reload information
        userProfileInfoChanged.postValue(true)
    }

}