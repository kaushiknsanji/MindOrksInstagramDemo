package com.mindorks.kaushiknsanji.instagram.demo.ui.like

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.Serializable

/**
 * [BaseViewModel] subclass for [PostLikeActivity].
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 *
 * @author Kaushik N Sanji
 */
class PostLikeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Post data loading progress indication
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // LiveData for the Post Details of the Post requested
    private val postData: MutableLiveData<Post> = MutableLiveData()

    // Transform the [postData] to get the number of Likes on the Post
    val likesCount: LiveData<Int> = Transformations.map(postData) { post -> post.likedBy?.size ?: 0 }
    // Transform the [likesCount] to find if any user has liked the Post
    val postHasLikes: LiveData<Boolean> = Transformations.map(likesCount) { countOfLikes: Int -> countOfLikes > 0 }
    // Transform the [postData] to find if the logged-in user had liked the Post
    val hasUserLiked: LiveData<Boolean> = Transformations.map(postData) { post ->
        post.likedBy?.any { postUser: Post.User -> postUser.id == user.id }
    }
    // Transform the [postData] to get the list of Likes on the Post
    val postLikes: LiveData<List<Post.User>?> = Transformations.map(postData) { post -> post.likedBy }

    // LiveData for closing the Activity with the status of logged-in User's Like on the Post
    val closeWithLikeStatus: MutableLiveData<Event<Map<String, Serializable>>> = MutableLiveData()

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
     * Called when the close button in the toolbar or Back Key is pressed.
     *
     * Triggers an event to finish the Activity, with the status of logged-in User's Like on the Post,
     * dispatched to the Calling Activity.
     */
    fun onClose() = closeWithLikeStatus.postValue(
        Event(ArrayMap<String, Serializable>().apply {
            put(PostLikeActivity.EXTRA_RESULT_LIKE_POST_ID, postData.value?.id)
            put(PostLikeActivity.EXTRA_RESULT_LIKE_POST_STATE, hasUserLiked.value)
        })
    )

}