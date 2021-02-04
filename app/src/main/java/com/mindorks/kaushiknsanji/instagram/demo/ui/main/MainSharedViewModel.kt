package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.Serializable

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

    // LiveData for redirecting to HomeFragment
    val redirectHome: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for launching EditProfileActivity
    val launchEditProfile: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    // LiveData for launching PostDetailActivity
    val launchPostDetail: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()

    // LiveData for launching PostLikeActivity
    val launchPostLike: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()

    // LiveData triggered when the Post is created and uploaded
    private val postPublished: MutableLiveData<Post> = MutableLiveData()

    // Transforms [postPublished] to trigger an event with the new Post for [HomeFragment]
    val postPublishUpdateToHome: LiveData<Event<Post>> =
        postPublished.map { newPost: Post -> Event(newPost) }

    // Transforms [postPublished] to trigger an event with the new Post for [ProfileFragment]
    val postPublishUpdateToProfile: LiveData<Event<Post>> =
        postPublished.map { newPost: Post -> Event(newPost) }

    // LiveData triggered when the User Profile information is updated
    private val userProfileInfoChanged: MutableLiveData<Boolean> = MutableLiveData()

    // Transforms [userProfileInfoChanged] to trigger the profile info update event for [HomeFragment]
    val userProfileInfoUpdateToHome: LiveData<Event<Boolean>> =
        userProfileInfoChanged.map { reload: Boolean -> Event(reload) }

    // Transforms [userProfileInfoChanged] to trigger the profile info update event for [ProfileFragment]
    val userProfileInfoUpdateToProfile: LiveData<Event<Boolean>> =
        userProfileInfoChanged.map { reload: Boolean -> Event(reload) }

    // LiveData triggered when the Post is deleted
    private val postDeleted: MutableLiveData<String> = MutableLiveData()

    // Transforms [postDeleted] to trigger an event to remove Post from [HomeFragment]
    val postDeletedEventToHome: LiveData<Event<String>> = postDeleted.map { postId: String ->
        Event(postId)
    }

    // Transforms [postDeleted] to trigger an event to remove Post from [ProfileFragment]
    val postDeletedEventToProfile: LiveData<Event<String>> = postDeleted.map { postId: String ->
        Event(postId)
    }

    // LiveData for Post Like Update events to [HomeFragment]
    val postLikeUpdateToHome: MutableLiveData<Event<Pair<String, Boolean>>> = MutableLiveData()

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

    /**
     * Called when the user clicks on the Post Item displayed in either
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment] or
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment].
     *
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity]
     * supplying the required [post] details as Intent Extras.
     */
    fun onPostItemClick(post: Post) {
        launchPostDetail.postValue(
            Event(ArrayMap<String, Serializable>().apply {
                put(PostDetailActivity.EXTRA_POST_ID, post.id)
            })
        )
    }

    /**
     * Called when the user clicks on the number of Likes on the Post Item displayed in
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment].
     *
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity]
     * supplying the required [post] details as Intent Extras.
     */
    fun onPostLikesCountClick(post: Post) {
        launchPostLike.postValue(
            Event(ArrayMap<String, Serializable>().apply {
                put(PostLikeActivity.EXTRA_POST_ID, post.id)
            })
        )
    }

    /**
     * Called when a Post Item is deleted successfully, either from
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment] or
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity].
     *
     * Triggers an event for the [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment]
     * and [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment] to reload the List of Posts after
     * removing the Post with [postId].
     *
     * @param postId The Id of the [Post] that was deleted.
     */
    fun onPostItemDeleted(postId: String) {
        // Delete the Post shown in [HomeFragment] and [ProfileFragment]
        postDeleted.postValue(postId)
    }

    /**
     * Called after the launch and completion of [PostLikeActivity] and [PostDetailActivity] for the Post with [postId].
     * Triggers an event for the [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment] to reload
     * the Post with [postId] with the new status of logged-in User's Like on the [Post].
     *
     * @param postId The Id of the [Post] that was launched in the activities.
     * @param likeStatus The new status of logged-in User's Like on the [Post].
     * `true` if User has liked; `false` otherwise. There may or may not be any change in the User's Like status,
     * but an update will always be triggered to keep the status current.
     */
    fun onPostLikeUpdate(postId: String, likeStatus: Boolean) {
        postLikeUpdateToHome.postValue(Event(postId to likeStatus))
    }

}