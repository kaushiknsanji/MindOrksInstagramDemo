package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.SharedPhotoOptionsViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.SharedProgressTextViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_edit_profile.*
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

    // SharedPhotoOptionsViewModel instance provided by Dagger
    @Inject
    lateinit var sharedPhotoOptionsViewModel: SharedPhotoOptionsViewModel

    // SharedProgressTextViewModel instance provided by Dagger
    @Inject
    lateinit var sharedProgressTextViewModel: SharedProgressTextViewModel

    // Instance of ParaCamera provided by Dagger
    @Inject
    lateinit var camera: Camera

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_edit_profile

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Initialize Toolbar
        toolbar_edit_profile.apply {
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
        edit_profile_name.addTextChangedListener(object : TextWatcher {
            /**
             * This method is called to notify you that, somewhere within
             * [s], the text has been changed.
             * It is legitimate to make further changes to [s] from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [android.text.Spannable.setSpan] in [onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(s: Editable?) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * are about to be replaced by new text with length [after].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * have just replaced old text that had length [before].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When the Name text changes, delegate to the EditProfileViewModel
                viewModel.onNameChange(s.toString())
            }

        })

        // Register text change listener on Bio field for validations
        edit_profile_bio.addTextChangedListener(object : TextWatcher {
            /**
             * This method is called to notify you that, somewhere within
             * [s], the text has been changed.
             * It is legitimate to make further changes to [s] from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [android.text.Spannable.setSpan] in [onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(s: Editable?) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * are about to be replaced by new text with length [after].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * have just replaced old text that had length [before].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When the Bio text changes, delegate to the EditProfileViewModel
                viewModel.onBioChange(s.toString())
            }

        })

        // Register click listener on "Change Photo" Button
        text_button_edit_profile_change_pic.setOnClickListener { viewModel.onChangePhoto() }

        // Register click listener on "Change Photo" Image
        image_edit_profile_change_pic.setOnClickListener { viewModel.onChangePhoto() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on loading/upload/save progress LiveData to show/hide the Progress Dialog
        viewModel.liveProgress.observe(this, Observer { resourceWrapper ->
            // Show/hide the Progress Dialog based on the Resource Status
            when (resourceWrapper.status) {
                Status.LOADING -> {
                    // When Loading, show the Progress Dialog immediately and update the Status Text via its ViewModel
                    dialogManager.showDialogNow(
                        ProgressTextDialogFragment::class.java,
                        ProgressTextDialogFragment.Companion::newInstance
                    )
                    // Update the Status Text
                    sharedProgressTextViewModel.onProgressStatusChange(resourceWrapper)
                }
                else -> {
                    // When not loading, dismiss any active dialog
                    dialogManager.dismissActiveDialog()
                }
            }
        })

        // Register an observer on Name field LiveData to set the new value on change
        viewModel.nameField.observe(this, Observer { nameValue ->
            edit_profile_name.setTextOnChange(nameValue)
        })

        // Register an observer on Bio field LiveData to set the new value on change
        viewModel.bioField.observe(this, Observer { bioValue ->
            edit_profile_bio.setTextOnChange(bioValue)
        })

        // Register an observer on Email field LiveData to set its value
        viewModel.userEmail.observe(this, Observer { emailValue ->
            edit_profile_email.setText(emailValue)
        })

        // Register an observer on Name field validation result to show the error if any
        viewModel.nameValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_edit_profile_name.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on the LiveData that detects changes to user information in order to allow
        // the user to reset using the menu button
        viewModel.hasAnyInfoChanged.observe(this, Observer { hasChanged: Boolean ->
            // Lookup the Menu item for reset
            toolbar_edit_profile.menu.findItem(R.id.action_edit_profile_reset)
                ?.let { menuItem: MenuItem ->
                    menuItem.showAndEnableWhen(hasChanged)
                }
        }

        // Register an observer on the User's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.userImage.observe(this, Observer { image: Image? -> loadProfileImage(imageData = image) })

        // Register an observer on the chosen Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.chosenProfileImageFile.observe(this, Observer { input: File? ->
            loadProfileImage(imageFile = input)
        })

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
        sharedPhotoOptionsViewModel.launchCamera.observeEvent(this) {
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
        sharedPhotoOptionsViewModel.launchGallery.observeEvent(this) {
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

    /**
     * Method that loads a Profile Picture into the corresponding ImageView.
     *
     * @param imageData [Image] instance to be provided when the Image is to be downloaded from the Remote.
     * @param imageFile [File] instance pointing to an Image in the local storage, to be provided if the Image
     * needs to be loaded from local.
     */
    private fun loadProfileImage(imageData: Image? = null, imageFile: File? = null) {
        if (imageData != null || imageFile != null) {
            // Configuring Glide with Image URL/File and other relevant customizations
            val glideRequest = GlideApp
                .with(this) // Loading with Activity reference
                .load(imageData?.let {
                    // Loading the GlideUrl with Headers when we have [imageData]
                    GlideHelper.getProtectedUrl(it.url, it.headers)
                } ?: /* else, Load the File */ imageFile)
                .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_add_pic)) // Loading with PlaceHolder Image

            imageData?.run {
                // Applies when we have [imageData]
                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    image_edit_profile_change_pic.run {
                        val params = layoutParams as ViewGroup.LayoutParams
                        params.width = placeHolderWidth
                        params.height = placeHolderHeight
                        layoutParams = params
                    }

                    // Override the dimensions of the Image (downloaded) to placeholder dimensions
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeHolderWidth, placeHolderHeight))
                }
            }

            // Start and load the image into the corresponding ImageView
            glideRequest.into(image_edit_profile_change_pic)

        } else {
            // Set default photo when there is no profile picture

            // Configuring Glide with relevant customizations and then setting the default photo
            GlideApp
                .with(this) // Loading with Activity reference
                .load(ContextCompat.getDrawable(this, R.drawable.ic_profile_add_pic)) // Loading the default Image
                .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                .into(image_edit_profile_change_pic) // Load into the corresponding ImageView
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