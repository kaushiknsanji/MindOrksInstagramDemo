package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseDialogViewModel] subclass for [PhotoOptionsDialogFragment].
 *
 * @author Kaushik N Sanji
 */
class SharedPhotoOptionsViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseDialogViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for launching System Camera
    val launchCamera: MutableLiveData<Event<Boolean>> = MutableLiveData()
    // LiveData for launching System Gallery
    val launchGallery: MutableLiveData<Event<Boolean>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when the "Camera" Option is clicked. Sends an [Event] to launch the System Camera.
     */
    fun onOptionCamera() = launchCamera.postValue(Event(true))


    /**
     * Called when the "Gallery" Option is clicked. Sends an [Event] to launch the System Gallery.
     */
    fun onOptionGallery() = launchGallery.postValue(Event(true))

}