package com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseImmersiveViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseImmersiveViewModel] subclass for [ImmersivePhotoActivity].
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
) : BaseImmersiveViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // LiveData of the Post Creator's Photo
    val postImage: MutableLiveData<Image> = MutableLiveData()

    // LiveData for closing the Activity
    val actionClose: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called by the activity when we have the Image Url of the Post Photo to be shown.
     * Triggers the corresponding LiveData with the [Image] details of the Photo to be downloaded and shown.
     *
     * @param imageUrl [String] containing the URL of the Post Photo to be downloaded.
     */
    fun onLoadImage(imageUrl: String) {
        postImage.postValue(
            Image(
                url = imageUrl,
                headers = headers
            )
        )
    }

    /**
     * Called when the User clicks on the "Close" Image.
     * Triggers an event to finish the Activity.
     */
    fun onClose() = actionClose.postValue(Event(true))

}