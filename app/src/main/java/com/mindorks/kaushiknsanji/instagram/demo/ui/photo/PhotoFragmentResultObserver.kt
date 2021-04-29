package com.mindorks.kaushiknsanji.instagram.demo.ui.photo

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.LifecycleOwner
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivityResultContracts
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragmentResultObserver
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants
import com.mindorks.paracamera.Camera
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * [PhotoFragment] Lifecycle observer class to receive and handle the activity result in a separate
 * class for the calls being registered via [registry].
 *
 * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks.
 * @property viewModel [PhotoViewModel] instance which is the Primary ViewModel of the [PhotoFragment]
 * to delegate certain actions to it in the callbacks.
 * @property camera Instance of [ParaCamera][Camera] for capturing and processing an Image clicked by user.
 *
 * @constructor Creates an instance of [PhotoFragmentResultObserver] for registering calls, storing
 * and handling their callbacks in a separate class.
 *
 * @author Kaushik N Sanji
 */
class PhotoFragmentResultObserver(
    registry: ActivityResultRegistry,
    private val viewModel: PhotoViewModel,
    private val camera: Camera
) : BaseFragmentResultObserver<PhotoFragment>(registry) {

    // Launcher for Gallery App
    private lateinit var galleryLauncher: ActivityResultLauncher<Array<out String>>

    // Launcher for Camera App
    private lateinit var cameraLauncher: ActivityResultLauncher<Camera>

    /**
     * Called after the [LifecycleOwner]'s `onCreate` method returns.
     *
     * This method registers all the required
     * [androidx.activity.result.ActivityResultLauncher]s for the [PhotoFragment].
     *
     * @param registry [ActivityResultRegistry] that stores all the
     * [androidx.activity.result.ActivityResultCallback]s for all the registered calls.
     * @param owner [LifecycleOwner] of the [PhotoFragment] that makes the call.
     * @param fragment [PhotoFragment] instance derived from [owner].
     */
    override fun initResultLaunchers(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        fragment: PhotoFragment
    ) {
        // Register the call for Gallery
        galleryLauncher = galleryAppLauncher(registry, owner, fragment)

        // Register the call for Camera
        cameraLauncher = cameraAppLauncher(registry, owner)
    }

    /**
     * Registers the call for system Gallery App and stores their
     * [androidx.activity.result.ActivityResultCallback] via the [registry].
     *
     * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks.
     * @param owner [LifecycleOwner] of the [PhotoFragment] that makes the call.
     * @param fragment [PhotoFragment] instance derived from [owner].
     *
     * @return An [ActivityResultLauncher] for system Gallery App to execute the call contract.
     */
    private fun galleryAppLauncher(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        fragment: PhotoFragment
    ): ActivityResultLauncher<Array<out String>> = registry.register(
        reqKeyGenerator(TAG, PhotoFragment.REQUEST_IMAGE_PICK),
        owner,
        BaseActivityResultContracts.OpenableDocument()
    ) { result: Uri? ->
        try {
            result?.let { uri: Uri ->
                // When we have the URI to the Image picked, open the input stream to the URI and pass it
                // to the ViewModel to handle
                fragment.requireActivity().contentResolver?.openInputStream(uri)
                    ?.let { inputStream: InputStream ->
                        viewModel.onGalleryImageSelected(inputStream)
                    }
            }
                ?: fragment.showMessage(R.string.error_retry) // Ask to retry when we do not have the URI to the Image picked
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            // Ask to retry in case of failure while opening the input stream to the URI
            fragment.showMessage(R.string.error_retry)
        }
    }

    /**
     * Registers the call for system Camera App and stores their
     * [androidx.activity.result.ActivityResultCallback] via the [registry].
     *
     * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks.
     * @param owner [LifecycleOwner] of the [PhotoFragment] that makes the call.
     *
     * @return An [ActivityResultLauncher] for system Camera App to execute the call contract.
     */
    private fun cameraAppLauncher(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner
    ): ActivityResultLauncher<Camera> = registry.register(
        reqKeyGenerator(TAG, Camera.REQUEST_TAKE_PHOTO),
        owner,
        BaseActivityResultContracts.ParaCameraTakePicture()
    ) { result: Boolean ->
        // On Success
        if (result) {
            // Delegate to the ViewModel to handle, passing the path to the Photo's Bitmap
            viewModel.onPhotoSnapped { camera.cameraBitmapPath }
        }
    }

    /**
     * Launches system Gallery App to prompt the user to Pick an Image.
     */
    fun pickImage() {
        galleryLauncher.launch(
            // Filter for Images by passing MIME type for images
            arrayOf(Constants.TYPE_IMAGE)
        )
    }

    /**
     * Launches system Camera App to prompt the user to Take a Picture.
     */
    fun takePicture() {
        cameraLauncher.launch(camera)
    }

    companion object {
        // Constant used for logs and also in Request Keys
        const val TAG = "PhotoFragmentResultObserver"
    }
}