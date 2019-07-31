package com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.WindowUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass for [ImmersivePhotoActivity].
 *
 * @param userRepository [UserRepository] instance for [User] data.
 *
 * @author Kaushik N Sanji
 */
class ImmersivePhotoViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Image loading progress indication
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // LiveData of the Post Creator's Photo
    val postImage: MutableLiveData<Image> = MutableLiveData()

    // LiveData for Fullscreen Toggle events
    val toggleFullscreen: MutableLiveData<Event<Boolean>> = MutableLiveData()
    // Transform [toggleFullscreen] to get the current Immersive Mode state
    val isImmersiveMode: LiveData<Boolean> = Transformations.map(toggleFullscreen) { event: Event<Boolean>? ->
        event?.peekContent()?.takeIf { it }?.run {
            // On Toggle, read the Immersive Mode state
            WindowUtils.isImmersiveModeEnabled()
        } ?: false // Otherwise, return false
    }

    // LiveData for closing the Activity
    val closeAction: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called by the activity when we have the Image Url and its placeholder dimensions, of the Post Photo to be shown.
     * Triggers the corresponding LiveData with the [Image] details of the Photo to be downloaded and shown.
     *
     * @param imageUrl [String] containing the URL of the Post Photo to be downloaded.
     * @param placeHolderWidth [Int] value of the Image Placeholder width.
     * @param placeHolderHeight [Int] value of the Image Placeholder height.
     */
    fun onLoadImage(imageUrl: String, placeHolderWidth: Int, placeHolderHeight: Int) {
        postImage.postValue(
            Image(
                url = imageUrl,
                headers = headers,
                placeHolderWidth = placeHolderWidth,
                placeHolderHeight = placeHolderHeight
            )
        )
    }

    /**
     * Called by the activity when the User taps and interacts on the Photo shown.
     * Triggers an event to toggle the Fullscreen visibility.
     */
    fun onToggleFullscreen() {
        toggleFullscreen.postValue(Event(true))
    }

    /**
     * Called when the User clicks on the "Close" Image.
     * Triggers an event to finish the Activity.
     */
    fun onClose() = closeAction.postValue(Event(true))

}