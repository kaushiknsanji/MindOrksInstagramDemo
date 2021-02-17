package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityEditProfileBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showAndEnableWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
import com.mindorks.paracamera.Camera
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_edit_profile' to allow
 * the logged-in user to edit their information and profile picture.
 * [EditProfileViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class EditProfileActivity : BaseActivity<EditProfileViewModel>() {

    // PhotoOptionsDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var photoOptionsDialogSharedViewModel: PhotoOptionsDialogSharedViewModel

    // ProgressTextDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var progressTextDialogSharedViewModel: ProgressTextDialogSharedViewModel

    // Instance of ParaCamera provided by Dagger
    @Inject
    lateinit var camera: Camera

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityEditProfileBinding::inflate)

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the [Root View][View] for the Activity
     * inflated using `Android ViewBinding`.
     */
    override fun provideContentView(): View = binding.root

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Initialize Toolbar
        binding.toolbarEditProfile.apply {
            // Set Title
            title = getString(R.string.title_edit_profile_toolbar)
            // Set Close Icon as the Navigation Icon
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_close)
            // Register click listener on close button of the toolbar
            setNavigationOnClickListener { viewModel.onClose() }

            // Inflate the Toolbar menu
            inflateMenu(R.menu.toolbar_menu_edit_profile)
            // Set click listener on Menu items
            setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    // For "Save changes" button
                    R.id.action_edit_profile_save -> {
                        viewModel.onSave()
                        return@setOnMenuItemClickListener true
                    }
                    // For "Reset to defaults" button
                    R.id.action_edit_profile_reset -> {
                        viewModel.onReset()
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
        }

        // Register text change listener on Name field for validations
        binding.editProfileName.doOnTextChanged { text, _, _, _ ->
            // When Name text changes, delegate to the EditProfileViewModel
            viewModel.onNameChange(text.toString())
        }

        // Register text change listener on Bio field for validations
        binding.editProfileBio.doOnTextChanged { text, _, _, _ ->
            // When Bio text changes, delegate to the EditProfileViewModel
            viewModel.onBioChange(text.toString())
        }

        // Register click listener on "Change Photo" Button
        binding.textButtonEditProfileChangePic.setOnClickListener { viewModel.onChangePhoto() }

        // Register click listener on "Change Photo" Image
        binding.imageEditProfileChangePic.setOnClickListener { viewModel.onChangePhoto() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on loading/upload/save progress LiveData to show/hide the Progress Dialog
        viewModel.liveProgress.observe(this) { resourceWrapper ->
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

        // Register an observer on Name field LiveData to set the new value on change
        viewModel.nameField.observe(this) { nameValue ->
            binding.editProfileName.setTextOnChange(nameValue)
        }

        // Register an observer on Bio field LiveData to set the new value on change
        viewModel.bioField.observe(this) { bioValue ->
            binding.editProfileBio.setTextOnChange(bioValue)
        }

        // Register an observer on Email field LiveData to set its value
        viewModel.userEmail.observe(this) { emailValue ->
            binding.editProfileEmail.setText(emailValue)
        }

        // Register an observer on Name field validation result to show the error if any
        viewModel.nameValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputEditProfileName.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on the LiveData that detects changes to user information in order to allow
        // the user to reset using the menu button
        viewModel.hasAnyInfoChanged.observe(this) { hasChanged: Boolean ->
            // Lookup the Menu item for reset
            binding.toolbarEditProfile.menu.findItem(R.id.action_edit_profile_reset)
                ?.let { menuItem: MenuItem ->
                    menuItem.showAndEnableWhen(hasChanged)
                }
        }

        // Register an observer on the User's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.userImage.observe(this) { image: Image? ->
            ImageUtils.loadImage(
                this,
                binding.imageEditProfileChangePic,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.circleCropTransform(),
                    RequestOptions.placeholderOf(R.drawable.ic_profile_add_pic)
                ),
                defaultImageRes = R.drawable.ic_profile_add_pic,
                defaultImageRequestOptions = listOf(
                    RequestOptions.circleCropTransform()
                )
            )
        }

        // Register an observer on the chosen Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.chosenProfileImageFile.observe(this) { input: File? ->
            ImageUtils.loadImage(
                this,
                binding.imageEditProfileChangePic,
                imageFile = input,
                requestOptions = listOf(
                    RequestOptions.circleCropTransform(),
                    RequestOptions.placeholderOf(R.drawable.ic_profile_add_pic)
                ),
                defaultImageRes = R.drawable.ic_profile_add_pic,
                defaultImageRequestOptions = listOf(
                    RequestOptions.circleCropTransform()
                )
            )
        }

        // Register an observer for PhotoOptionsDialogFragment launch events
        viewModel.launchPhotoOptions.observeEvent(this) {
            // Show the Dialog for Photo Options if not shown
            dialogManager.showDialog(
                PhotoOptionsDialogFragment::class.java,
                PhotoOptionsDialogFragment.Companion::newInstance
            )
        }

        // Register an observer for close action events, to close this Activity
        viewModel.actionClose.observeEvent(this) { close: Boolean ->
            if (close) {
                // Terminate the Activity on [close]
                finish()
            }
        }

        // Register an observer for finish events with Success result, to close and update the Calling Activity
        viewModel.finishWithSuccess.observeEvent(this) { responseMessage: String ->
            // Set the Result
            setResult(
                RESULT_EDIT_PROFILE_SUCCESS,
                Intent().apply {
                    putExtra(EXTRA_RESULT_EDIT_SUCCESS, responseMessage)
                }
            )
            // Terminate the Activity
            finish()
        }

        // Register an observer for finish events with No Action result, to close and update the Calling Activity
        viewModel.finishWithNoAction.observeEvent(this) { close: Boolean ->
            if (close) {
                // Set the Result on [close]
                setResult(RESULT_EDIT_PROFILE_NO_ACTION)
                // Terminate the Activity
                finish()
            }
        }

        // Register an observer for System Camera launch events
        photoOptionsDialogSharedViewModel.launchCamera.observeEvent(this) {
            try {
                // Launches the Camera activity with the ACTION_IMAGE_CAPTURE Intent, that saves the captured image
                // to a temporary file
                camera.takePicture()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Dismiss the dialog
                dialogManager.dismissActiveDialog()
            }
        }

        // Register an observer for System Gallery launch events
        photoOptionsDialogSharedViewModel.launchGallery.observeEvent(this) {
            Intent(Intent.ACTION_OPEN_DOCUMENT).run {
                // With the Intent that can open any document..
                // Filter results that can be streamed like files (this excludes stuff like timezones and contacts)
                addCategory(Intent.CATEGORY_OPENABLE)
                // Filter only for Images
                type = Constants.TYPE_IMAGE
                // Start the Image Picker/Gallery Activity with the request code for results
                startActivityForResult(this, PhotoOptionsDialogFragment.REQUEST_IMAGE_PICK)
                // Dismiss the dialog
                dialogManager.dismissActiveDialog()
            }
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

        if (resultCode == RESULT_OK) {
            // When we have a success result from the Activity started

            // Taking action based on the Request code
            when (requestCode) {
                // For Gallery Image Pick
                PhotoOptionsDialogFragment.REQUEST_IMAGE_PICK -> {
                    try {
                        intent?.data?.let { uri: Uri ->
                            // When we have the URI to the Image picked, open the input stream to the URI and pass it
                            // to the ViewModel to handle
                            contentResolver?.openInputStream(uri)?.let { inputStream: InputStream ->
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

        // Request code used by the activity that calls this activity for result
        const val REQUEST_EDIT_PROFILE = 200 //201, 202 is reserved for the result of this request

        // Custom Result code for Successful Edit operation
        const val RESULT_EDIT_PROFILE_SUCCESS = REQUEST_EDIT_PROFILE + Activity.RESULT_FIRST_USER

        // Custom Result code for Edit operation with No Action taken
        const val RESULT_EDIT_PROFILE_NO_ACTION = RESULT_EDIT_PROFILE_SUCCESS + Activity.RESULT_FIRST_USER

        // Intent Extra constant for passing the Response message of the Successful edit operation
        @JvmField
        val EXTRA_RESULT_EDIT_SUCCESS =
            EditProfileActivity::class.java.`package`?.toString() + "extra.EDIT_SUCCESS"

    }

}