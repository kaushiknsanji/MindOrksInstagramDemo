package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.TimeUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ScreenUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * [BaseItemViewModel] subclass for [PostItemViewHolder]
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 * @constructor Instance of [PostItemViewModel] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class PostItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseItemViewModel<Post>(schedulerProvider, compositeDisposable, networkHelper) {

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Get the Screen dimensions for calculating the required Image dimensions
    private val screenWidth = ScreenUtils.getScreenWidth()
    private val screenHeight = ScreenUtils.getScreenHeight()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transform the [itemData] to get the Name of the Post Creator
    val postCreatorName: LiveData<String> = Transformations.map(itemData) { post -> post.creator.name }
    // Transform the [itemData] to get the Creation Time of the Post (in words via the TimeUtils)
    val postCreationTime: LiveData<String> =
        Transformations.map(itemData) { post -> TimeUtils.getTimeAgo(post.createdAt) }
    // Transform the [itemData] to get the number of Likes on the Post
    val likesCount: LiveData<Int> = Transformations.map(itemData) { post -> post.likedBy?.size ?: 0 }
    // Transform the [itemData] to find if the logged-in user had liked the Post
    val hasUserLiked: LiveData<Boolean> = Transformations.map(itemData) { post ->
        post.likedBy?.any { postUser: Post.User -> postUser.id == user.id }
    }
    // Transform the [itemData] to get the [Image] model of the Post Creator's Profile Picture
    val profileImage: LiveData<Image> = Transformations.map(itemData) { post ->
        post.creator.profilePicUrl?.run { Image(url = this, headers = headers) }
    }
    // Transform the [itemData] to get the [Image] model of the Post Creator's Photo
    val postImage: LiveData<Image> = Transformations.map(itemData) { post ->
        Image(
            url = post.imageUrl,
            headers = headers,
            placeHolderWidth = screenWidth, // Image will take the entire width of the screen
            placeHolderHeight = post.imageHeight?.let { imageHeight ->
                // If the source Image height is available, then it will be scaled to the Image width on the screen
                (calculateImageScaleFactor(post) * imageHeight).toInt()
            } ?: (screenHeight / 3
                    // If the source Image height is unavailable, then it will be fixed to 1/3rd of the screen height
                    )
        )
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Calculates the width ratio of the target screen to the source image, in order to scale the source Image height
     * to fit the width of the target screen maintaining the aspect ratio of the source Image.
     *
     * @return [Float] value of the Image scaling factor for scaling the source Image height. Can be a value of 1
     * if the source Image width is unavailable.
     */
    private fun calculateImageScaleFactor(post: Post): Float =
        post.imageWidth?.let { imageWidth -> screenWidth.toFloat() / imageWidth.toFloat() } ?: 1f

    /**
     * Called when the "Heart" image on the Post is clicked, to like/unlike the Post.
     */
    fun onLikeClick() {
        // Making changes on the current Post
        itemData.value?.run {
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
                                    // and then update the ViewModel's data to reflect the user's like action
                                    updateItemData(responsePost)
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

}