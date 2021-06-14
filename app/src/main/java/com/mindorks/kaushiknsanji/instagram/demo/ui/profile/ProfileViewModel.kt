package com.mindorks.kaushiknsanji.instagram.demo.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass for [ProfileFragment].
 *
 * @property userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 *
 * @author Kaushik N Sanji
 */
class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    companion object {
        // Constant for the Dialog Type of Logout Confirmation request
        const val CONFIRM_OPTION_DIALOG_TYPE_LOGOUT = "Logout"
    }

    // LiveData for the posts loading and logout progress indication
    val liveProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the User's List of Posts
    val userPosts: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    // LiveData for the complete information of logged-in User that includes profile picture and tagline
    private val userInfo: MutableLiveData<User> = MutableLiveData()

    // Stores the logged-in [User] information read from preferences
    private val user: User = userRepository.getCurrentUser()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transforms [userInfo] to get the Name of the logged-in User
    val userName: LiveData<String> = userInfo.map { user -> user.name }

    // Transforms [userInfo] to get the Tag-line of the logged-in User
    val userTagline: LiveData<String> = userInfo.map { user -> user.tagline ?: "" }

    // Transforms [userInfo] to get the [Image] model of the logged-in User's Profile Picture
    val userImage: LiveData<Image?> = userInfo.map { user ->
        user.profilePicUrl?.run { Image(url = this, headers = headers) }
    }

    // Transforms [userPosts] to get the count of Posts created by the logged-in User
    val userPostsCount: LiveData<Int> =
        userPosts.map { resourceWrapper -> resourceWrapper.peekData()?.size ?: 0 }

    // Transforms [userPostsCount] to get the presence of Posts created by the logged-in User
    val userPostsEmpty: LiveData<Boolean> = userPostsCount.map { postCount -> postCount == 0 }

    // LiveData for launching EditProfileActivity
    val launchEditProfile: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    // LiveData for launching LoginActivity
    val launchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    // LiveData for Logout confirmation request events
    val launchLogoutConfirm: MutableLiveData<Event<ConfirmOptionDialogMetadata>> = MutableLiveData()

    init {
        // When this ViewModel is first initialized..

        // Load complete information of the logged-in user
        loadUserInfo()
        // Load the logged-in user's list of posts
        loadUserPosts()
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Downloads complete information of the logged-in user into the [userInfo] LiveData.
     */
    private fun loadUserInfo() {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, start downloading complete user information

            // Make the Remote API Call and save the resulting disposable
            compositeDisposable.add(
                userRepository.doFetchUserInfo(user)
                    .subscribeOn(schedulerProvider.io()) //Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { updatedUserInfo: User ->
                            // Update the LiveData with the user information
                            // (This triggers LiveData transformations for field values)
                            userInfo.postValue(updatedUserInfo)
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )

        } else {
            // Reuse existing information from preferences when there is an issue with network connectivity
            userInfo.postValue(user)
        }
    }

    /**
     * Loads a List of [Post]s created by the logged-in [user] into the [userPosts] LiveData.
     */
    private fun loadUserPosts() {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, start downloading the User's List of Posts

            // Start the [liveProgress] indication
            liveProgress.postValue(true)

            // Make the Remote API Call and save the resulting disposable
            compositeDisposable.add(
                postRepository.getUserPostsList(user)
                    .subscribeOn(schedulerProvider.io()) //Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { posts: List<Post> ->
                            // Update the LiveData with the User's List of Posts
                            // (This triggers LiveData transformations for field values)
                            userPosts.postValue(Resource.Success(posts))
                            // Stop the [liveProgress] indication
                            liveProgress.postValue(false)
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Stop the [liveProgress] indication
                            liveProgress.postValue(false)
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )
        }
    }

    /**
     * Called when the user clicks on "Edit Profile" button.
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity]
     */
    fun onEditProfile() {
        launchEditProfile.postValue(Event(emptyMap()))
    }

    /**
     * Called when the user clicks on the "Logout" button.
     *
     * Triggers an event to confirm the action with the user before proceeding.
     */
    fun onLogoutRequest() {
        // Trigger the event and pass the information for the dialog to be shown
        launchLogoutConfirm.postValue(
            Event(
                ConfirmOptionDialogMetadata { base: BaseDialogMetadata.BaseCompanion ->
                    arrayOf(
                        base.KEY_MESSAGE to Resource.Success(R.string.message_dialog_confirm_profile_logout),
                        base.KEY_BUTTON_POSITIVE to Resource.Success(R.string.dialog_confirm_profile_logout_button_positive),
                        base.KEY_BUTTON_NEGATIVE to Resource.Success(R.string.dialog_confirm_profile_logout_button_negative),
                        KEY_DIALOG_TYPE to CONFIRM_OPTION_DIALOG_TYPE_LOGOUT
                    )
                }
            )
        )
    }

    /**
     * Called when the "Logout" action is confirmed by the user.
     *
     * Communicates with the Remote API to logout the [user].
     */
    fun onLogoutConfirm() {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, start the logout process

            // Start the [liveProgress] indication
            liveProgress.postValue(true)

            // Make the Remote API Call and save the resulting disposable
            compositeDisposable.add(
                userRepository.doUserLogout(user)
                    .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { resource: Resource<String> ->
                            // Stop the [liveProgress] indication
                            liveProgress.postValue(false)
                            // Display the Resource message
                            messageString.postValue(resource)
                            // When Resource status is Success, we have logged out the
                            // user successfully. Hence trigger an event to launch LoginActivity
                            if (resource.status == Status.SUCCESS) {
                                launchLogin.postValue(Event(emptyMap()))
                            }
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Stop the [liveProgress] indication
                            liveProgress.postValue(false)
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )
        }
    }

    /**
     * Called when a new Instagram Post is created by the User, to load the [newPost] to the Top of All User Posts shown.
     *
     * @param newPost [Post] instance containing the information of the new Post created.
     */
    fun onNewPost(newPost: Post) {
        // ProfileFragment may or may not be launched by the User before creating a Post.
        // Hence we need to ensure that the [newPost] is not already present in the list of user posts before updating.

        // Get the current list of Posts
        val currentPosts: List<Post>? = userPosts.value?.peekData()

        // Check if the list contains the New Post before adding
        currentPosts?.takeIf { it.isNotEmpty() }?.none { post: Post -> post.id == newPost.id }
            ?.let { isAbsent: Boolean ->
                if (isAbsent) {
                    // When it does not contain the [newPost], add and update the User's List of Posts LiveData
                    userPosts.postValue(Resource.Success(currentPosts.toMutableList().apply {
                        add(0, newPost)
                    }))
                }
            }

    }

    /**
     * Called when the User Profile information has been updated
     * successfully through [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity].
     *
     * Refreshes information of the logged-in user into the [userInfo] LiveData.
     */
    fun onRefreshUserInfo() {
        // Get updated user information from preferences
        userInfo.postValue(userRepository.getCurrentUser())
    }

    /**
     * Called when the User Deletes the Post from [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity]
     * or [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment].
     *
     * Removes the Post with the [postId] from the [userPosts] and reloads the list.
     */
    fun onPostDeleted(postId: String) =
        userPosts.postValue(Resource.Success(userPosts.value?.peekData()?.toMutableList()?.apply {
            removeAll { post: Post -> post.id == postId }
        }))

}