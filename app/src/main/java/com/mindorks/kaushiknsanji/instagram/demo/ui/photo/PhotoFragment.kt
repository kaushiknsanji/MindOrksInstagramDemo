package com.mindorks.kaushiknsanji.instagram.demo.ui.photo


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.FragmentPhotoBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.TYPE_IMAGE
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.paracamera.Camera
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

/**
 * [BaseFragment] subclass that inflates the layout 'R.layout.fragment_photo' to allow the logged-in user
 * to add photo and create a Post.
 * [PhotoViewModel] is the primary [androidx.lifecycle.ViewModel] of this Fragment.
 *
 * @author Kaushik N Sanji
 */
class PhotoFragment : BaseFragment<PhotoViewModel>() {

    // Instance of ParaCamera provided by Dagger
    @Inject
    lateinit var camera: Camera

    // Instance of the ViewModel shared with the MainActivity provided by Dagger
    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    // ProgressTextDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var progressTextDialogSharedViewModel: ProgressTextDialogSharedViewModel

    // ViewBinding instance for this Fragment
    private val binding by viewBinding(FragmentPhotoBinding::bind)

    /**
     * Injects dependencies exposed by [FragmentComponent] into Fragment.
     */
    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Fragment.
     */
    override fun provideLayoutId(): Int = R.layout.fragment_photo

    /**
     * Initializes the Layout of the Fragment.
     */
    override fun setupView(view: View, savedInstanceState: Bundle?) {

        // Initialize Toolbar
        binding.toolbarPhoto.apply {
            // Set Title
            title = getString(R.string.title_photo_toolbar)
        }

        // Register click listener on "Camera" option
        (binding.includePhotoOptions?.viewPhotoOptionCameraBackground
            ?: binding.viewPhotoOptionCameraBackground!!).setOnClickListener {
            try {
                // Launches the Camera activity with the ACTION_IMAGE_CAPTURE Intent, that saves the captured image
                // to a temporary file
                camera.takePicture()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Register click listener on "Gallery" option
        (binding.includePhotoOptions?.viewPhotoOptionGalleryBackground
            ?: binding.viewPhotoOptionGalleryBackground!!).setOnClickListener {
            Intent(Intent.ACTION_OPEN_DOCUMENT).run {
                // With the Intent that can open any document..
                // Filter results that can be streamed like files (this excludes stuff like timezones and contacts)
                addCategory(Intent.CATEGORY_OPENABLE)
                // Filter only for Images
                type = TYPE_IMAGE
                // Start the Image Picker/Gallery Activity with the request code for results
                startActivityForResult(this, REQUEST_IMAGE_PICK)
            }
        }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the photo and post creation loading progress to show/hide the Progress Dialog
        viewModel.loadingProgress.observe(this) { resourceWrapper ->
            // Show/hide the Progress Dialog based on the Resource Status
            when (resourceWrapper.status) {
                Status.LOADING -> {
                    // When Loading, show the Progress Dialog immediately and update the Status Text via its ViewModel
                    dialogManager.showDialogNow(
                        ProgressTextDialogFragment::class.java,
                        ProgressTextDialogFragment.Companion::newInstance
                    )
                    // Update the Status Text
                    progressTextDialogSharedViewModel.onProgressStatusChange(resourceWrapper)
                }
                else -> {
                    // When not loading, dismiss any active dialog
                    dialogManager.dismissActiveDialog()
                }
            }
        }

        // Register an observer on the Post creation LiveData to pass the new Post Details
        // to the Shared ViewModel to handle
        viewModel.postPublished.observeEvent(this) { newPost: Post ->
            mainSharedViewModel.onPostCreated(newPost)
        }

    }

    /**
     * Receive the result from a previous call to [startActivityForResult].
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode The integer result code returned by the child activity
     * through its setResult().
     * @param intent An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            // When we have a success result from the Activity started

            // Taking action based on the Request code
            when (requestCode) {
                // For Gallery Image Pick
                REQUEST_IMAGE_PICK -> {
                    try {
                        intent?.data?.let { uri: Uri ->
                            // When we have the URI to the Image picked, open the input stream to the URI and pass it
                            // to the ViewModel to handle
                            activity?.contentResolver?.openInputStream(uri)?.let { inputStream: InputStream ->
                                viewModel.onGalleryImageSelected(inputStream)
                            }
                        }
                            ?: showMessage(R.string.error_retry) // Ask to retry when we do not have the URI to the Image picked
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        // Ask to retry in case of failure while opening the input stream to the URI
                        showMessage(R.string.error_retry)
                    }
                }

                // For Image Capture
                Camera.REQUEST_TAKE_PHOTO -> {
                    // Delegate to the ViewModel to handle, passing the path to the Photo's Bitmap
                    viewModel.onPhotoSnapped { camera.cameraBitmapPath }
                }
            }
        }
    }

    companion object {

        // Constant used as Fragment Tag and also for logs
        const val TAG = "PhotoFragment"

        // Request code for Image Pick
        const val REQUEST_IMAGE_PICK = 1001

        /**
         * Factory method to create a new instance of this fragment.
         *
         * @return A new instance of fragment [PhotoFragment].
         */
        @JvmStatic
        fun newInstance() =
            PhotoFragment().apply {
                arguments = Bundle()
            }
    }
}
