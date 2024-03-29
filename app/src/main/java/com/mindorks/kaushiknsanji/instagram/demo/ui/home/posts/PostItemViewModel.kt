/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.TimeUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ScreenUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
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
    private val user: User = userRepository.getCurrentUser()

    // Get the Screen dimensions for calculating the required Image dimensions
    private val screenWidth = ScreenUtils.getScreenWidth()
    private val screenHeight = ScreenUtils.getScreenHeight()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transforms [itemData] to get the Name of the Post Creator
    val postCreatorName: LiveData<String> = itemData.map { post -> post.creator.name }

    // Transforms [itemData] to get the Creation Time of the Post (in words via the TimeUtils)
    val postCreationTime: LiveData<String> =
        itemData.map { post -> TimeUtils.getTimeAgo(post.createdAt) }

    // Transforms [itemData] to get the number of Likes on the Post
    val likesCount: LiveData<Int> = itemData.map { post -> post.likedBy?.size ?: 0 }

    // Transforms [itemData] to find if the logged-in user had liked the Post
    val hasUserLiked: LiveData<Boolean> = itemData.map { post ->
        post.likedBy?.any { likedByUser: Post.User -> likedByUser.id == user.id } ?: false
    }

    // Transforms [itemData] to get the [Image] model of the Post Creator's Profile Picture
    val profileImage: LiveData<Image?> = itemData.map { post ->
        post.creator.profilePicUrl?.run { Image(url = this, headers = headers) }
    }

    // Transforms [itemData] to get the [Image] model of the Post Creator's Photo
    val postImage: LiveData<Image> = itemData.map { post ->
        Image(
            url = post.imageUrl,
            headers = headers,
            placeHolderWidth = screenWidth, // Image will take the entire width of the screen
            /**
             * If the source Image height is available, then it will be scaled to the Image width on the screen.
             * Otherwise, it will be fixed to 1/3rd of the screen height
             */
            placeHolderHeight = ImageUtils.scaleImageHeightToTargetWidth(
                post.imageWidth,
                post.imageHeight,
                screenWidth
            ) {
                screenHeight / 3
            }
        )
    }

    // LiveData for ItemView Click event
    val actionItemClick: MutableLiveData<Event<Post>> = MutableLiveData()

    // LiveData for Likes Count TextView Click event
    val actionLikesCountClick: MutableLiveData<Event<Post>> = MutableLiveData()

    // LiveData for Like/Unlike action event
    val actionLikeUnlike: MutableLiveData<Event<Post>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when the "Heart" image on the Post is clicked, to like/unlike the Post. Also, called when the
     * user double-taps on the Post Photo.
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
                                    // Trigger the Like/Unlike event with the updated Post data
                                    actionLikeUnlike.postValue(Event(responsePost))
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
     * Called when the User clicks on the Post.
     * Triggers an event to delegate it to the Host Listeners to handle.
     */
    fun onItemClick() {
        // Triggering the event when we have the Post Item data
        itemData.value?.let {
            actionItemClick.postValue(Event(it))
        }
    }

    /**
     * Called when the User clicks on the TextView that displays the number of Likes on the Post.
     * Triggers an event to delegate it to the Host Listeners to handle.
     */
    fun onLikeCountClick() {
        // Triggering the event when we have the Post Item data
        itemData.value?.let {
            actionLikesCountClick.postValue(Event(it))
        }
    }

}