package com.mindorks.kaushiknsanji.instagram.demo.ui.detail

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.model.calculateImageScaleFactor
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo.ImmersivePhotoActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.TimeUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ScreenUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.Serializable

/**
 * [BaseViewModel] subclass for [PostDetailActivity].
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 *
 * @author Kaushik N Sanji
 */
class PostDetailViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Post data loading progress indication
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the Delete progress indication
    val deleteProgress: MutableLiveData<Resource<Int>> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Get the Screen dimensions for calculating the required Image dimensions
    private val screenWidth = ScreenUtils.getScreenWidth()
    private val screenHeight = ScreenUtils.getScreenHeight()

    // LiveData for the Post Details of the Post requested
    private val postData: MutableLiveData<Post> = MutableLiveData()

    // Transform the [postData] to get the Name of the Post Creator
    val postCreatorName: LiveData<String> = Transformations.map(postData) { post -> post.creator.name }
    // Transform the [postData] to get the Creation Time of the Post (in words via the TimeUtils)
    val postCreationTime: LiveData<String> =
        Transformations.map(postData) { post -> TimeUtils.getTimeAgo(post.createdAt) }
    // Transform the [postData] to get the number of Likes on the Post
    val likesCount: LiveData<Int> = Transformations.map(postData) { post -> post.likedBy?.size ?: 0 }
    // Transform the [likesCount] to find if any user has liked the Post
    val postHasLikes: LiveData<Boolean> = Transformations.map(likesCount) { countOfLikes: Int -> countOfLikes > 0 }
    // Transform the [postData] to find if the logged-in user had liked the Post
    val hasUserLiked: LiveData<Boolean> = Transformations.map(postData) { post ->
        post.likedBy?.any { postUser: Post.User -> postUser.id == user.id }
    }
    // Transform the [postData] to get the list of Likes on the Post
    val postLikes: LiveData<List<Post.User>?> = Transformations.map(postData) { post -> post.getLikesCopy() }
    // Transform the [postData] to check if the Post was created by the logged-in user
    val postByUser: LiveData<Boolean> = Transformations.map(postData) { post -> post.creator.id == user.id }
    // Transform the [postData] to get the [Image] model of the Post Creator's Profile Picture
    val profileImage: LiveData<Image> = Transformations.map(postData) { post ->
        post.creator.profilePicUrl?.run { Image(url = this, headers = headers) }
    }
    // Transform the [postData] to get the [Image] model of the Post Creator's Photo
    val postImage: LiveData<Image> = Transformations.map(postData) { post ->
        Image(
            url = post.imageUrl,
            headers = headers,
            placeHolderWidth = screenWidth, // Image will take the entire width of the screen
            placeHolderHeight = post.imageHeight?.let { imageHeight ->
                // If the source Image height is available, then it will be scaled to the Image width on the screen
                (post.calculateImageScaleFactor(screenWidth.toFloat()) * imageHeight).toInt()
            } ?: run {
                // If the source Image height is unavailable, then it will be fixed to 1/3rd of the screen height
                screenHeight / 3
            }
        )
    }

    // LiveData for Delete Post confirmation request events
    val launchDeletePostConfirm: MutableLiveData<Resource<Int>> = MutableLiveData()
    // LiveData for ImmersivePhotoActivity launch events
    val launchImmersivePhoto: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()
    // LiveData for navigating back to the Parent with the status of logged-in User's Like on the Post
    val navigateParentWithLikeStatus: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()
    // LiveData for navigating back to the Parent with the Result of Successful Post Delete action
    val navigateParentWithDeleteSuccess: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called by the activity when we have the [Post.id] of the [Post] whose details are to be downloaded.
     *
     * Downloads complete information of the corresponding [Post] into the [postData] LiveData.
     */
    fun onLoadPostDetails(postId: String) {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, start downloading the Post details

            // Start the [loadingProgress] indication
            loadingProgress.postValue(true)

            // Make the Remote API Call and save the resulting disposable
            compositeDisposable.add(
                postRepository.getPostDetail(postId, user)
                    .subscribeOn(schedulerProvider.io()) //Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { post: Post ->
                            // Update the LiveData with the Post Details
                            // (This triggers LiveData transformations for field values)
                            postData.postValue(post)
                            // Stop the [loadingProgress] indication
                            loadingProgress.postValue(false)
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Stop the [loadingProgress] indication
                            loadingProgress.postValue(false)
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )
        }
    }

    /**
     * Called when the "Heart" menu item is clicked, to like/unlike the Post. Can also be called when the
     * user clicks on the "No Likes Heart Image".
     *
     * Communicates with the Remote API to like/unlike the Post.
     */
    fun onLikeAction() {
        // Making changes on the current Post
        postData.value?.run {
            if (checkInternetConnectionWithMessage()) {
                // When we have the network connectivity, do the API call

                // Decide and call the API based on the current status of logged-in user's like on the Post
                val api = if (hasUserLiked.value == true) {
                    // Call Unlike Post API if already liked by the logged-in user
                    postRepository.unLikePost(this, user)
                } else {
                    // Call Like Post API if the Post has not been liked by the logged-in user
                    postRepository.likePost(this, user)
                }

                // Complete the API call and save the resulting disposable
                compositeDisposable.add(
                    api.subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                        .subscribe(
                            // OnSuccess
                            { responsePost: Post ->
                                if (responsePost.id == this.id) {
                                    // Ensure that the response is for the same post
                                    // and then update the corresponding LiveData to reflect the user's like action
                                    postData.postValue(responsePost)
                                }
                            },
                            // OnError
                            { throwable: Throwable? ->
                                // Handle and display the appropriate error
                                handleNetworkError(throwable)
                            }
                        )
                )
            }
        }
    }

    /**
     * Called when the Delete menu item is clicked, to Delete the Post shown.
     *
     * Triggers an event to confirm the action with the user before proceeding.
     */
    fun onDeleteRequest() {
        // Trigger the event and pass the title of the dialog to be shown
        launchDeletePostConfirm.postValue(Resource.success(R.string.dialog_confirm_title_delete_post))
    }

    /**
     * Called when the Post Delete action is confirmed by the User to delete the Post shown.
     *
     * Communicates with the Remote API to delete the Post shown.
     */
    fun onDeleteConfirm() {
        postData.value?.run {
            if (checkInternetConnectionWithMessage()) {
                // When we have the network connectivity, initiate Post Deletion

                // Start the [deleteProgress] indication
                deleteProgress.postValue(Resource.loading(R.string.progress_post_delete))

                // Make the Remote API Call and save the resulting disposable
                compositeDisposable.add(
                    postRepository.deletePost(this, user)
                        .subscribeOn(schedulerProvider.io()) //Operate on IO Thread
                        .subscribe(
                            // OnSuccess
                            { resource: Resource<String> ->
                                // Stop the [deleteProgress] indication
                                deleteProgress.postValue(Resource.success())
                                if (resource.status == Status.SUCCESS) {
                                    // When Resource status is Success, we have deleted the Post successfully
                                    // Hence trigger an event to go back to the parent with Successful Delete results
                                    navigateParentWithDeleteSuccess.postValue(
                                        Event(ArrayMap<String, Serializable>().apply {
                                            put(PostDetailActivity.EXTRA_RESULT_DELETED_POST_ID, this@run.id)
                                            put(PostDetailActivity.EXTRA_RESULT_DELETE_POST_SUCCESS, resource.getData())
                                        })
                                    )
                                } else {
                                    // When Resource status is other than Success, Post deletion might have
                                    // failed for other unknown reasons. Hence display the Resource message and
                                    // do not close the activity.
                                    messageString.postValue(resource)
                                }
                            },
                            // OnError
                            { throwable: Throwable? ->
                                // Stop the [deleteProgress] indication
                                deleteProgress.postValue(Resource.success())
                                // Handle and display the appropriate error
                                handleNetworkError(throwable)
                            }
                        )
                )
            }
        }
    }

    /**
     * Called when the user clicks on the Post's Photo.
     *
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo.ImmersivePhotoActivity]
     * with the Photo metadata, to allow the user to view the complete Photo.
     */
    fun onLaunchPhoto() {
        // When the Post's Photo is available
        postImage.value?.let { image: Image ->
            // Launch the Activity passing in the Photo's metadata, which will be loaded as its Intent Extras
            launchImmersivePhoto.postValue(
                Event(ArrayMap<String, Serializable>().apply {
                    // Load the Image URL and placeholder dimensions computed
                    put(ImmersivePhotoActivity.EXTRA_IMAGE_URL, image.url)
                    put(ImmersivePhotoActivity.EXTRA_IMAGE_PLACEHOLDER_WIDTH, image.placeHolderWidth)
                    put(ImmersivePhotoActivity.EXTRA_IMAGE_PLACEHOLDER_HEIGHT, image.placeHolderHeight)
                })
            )
        }
    }

    /**
     * Called by the activity when the user clicks on the Toolbar Up button or Back Key.
     *
     * Triggers an Event to navigate back to the Parent Activity, with the status of logged-in User's Like on the Post.
     */
    fun onNavigateUp() = navigateParentWithLikeStatus.postValue(
        Event(ArrayMap<String, Serializable>().apply {
            put(PostDetailActivity.EXTRA_RESULT_LIKE_POST_ID, postData.value?.id)
            put(PostDetailActivity.EXTRA_RESULT_LIKE_POST_STATE, hasUserLiked.value)
        })
    )

}