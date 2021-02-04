package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ScreenUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * [BaseItemViewModel] subclass for [ProfilePostItemViewHolder].
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @constructor Instance of [ProfilePostItemViewModel] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class ProfilePostItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository
) : BaseItemViewModel<Post>(schedulerProvider, compositeDisposable, networkHelper) {

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Get the Screen dimensions for calculating the required Image dimensions
    private val screenWidth = ScreenUtils.getScreenWidth()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transforms [itemData] to get the [Image] model of the User's Post Photo
    val postImage: LiveData<Image> = itemData.map { post ->
        Image(
            url = post.imageUrl,
            headers = headers,
            placeHolderWidth = screenWidth, // Will take the entire width of the screen
            placeHolderHeight = screenWidth // Will be the same as screen width to make its aspect ratio 1:1
        )
    }

    // LiveData for ItemView Click event
    val actionItemClick: MutableLiveData<Event<Post>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
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

}