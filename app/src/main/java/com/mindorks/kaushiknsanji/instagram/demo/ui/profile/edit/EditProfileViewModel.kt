package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.InputStream

/**
 * [BaseViewModel] subclass for [EditProfileActivity].
 *
 * @property directory [File] directory for saving the optimized images for Upload.
 * @property userRepository [UserRepository] instance for [User] data.
 * @property photoRepository [PhotoRepository] instance for uploading a Photo.
 *
 * @author Kaushik N Sanji
 */
class EditProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val directory: File,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    companion object {
        // Constant used for logs
        const val TAG = "EditProfileViewModel"

        // Constant used as keys for tracking changes in editable fields
        const val KEY_NAME_FIELD = "Name"
        const val KEY_BIO_FIELD = "Bio"
        const val KEY_PROFILE_PIC_FIELD = "ProfilePic"
    }

    // LiveData for loading, upload and save progress indication
    val liveProgress: MutableLiveData<Resource<Int>> = MutableLiveData()

    // LiveData for the complete information of logged-in User that includes profile picture and tagline
    private val userInfo: MutableLiveData<User> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transform the [userInfo] to get the Name of the logged-in User
    private val userName: LiveData<String> = Transformations.map(userInfo) { user -> user.name }
    // Transform the [userInfo] to get the Tag-line of the logged-in User
    private val userTagline: LiveData<String> = Transformations.map(userInfo) { user -> user.tagline ?: "" }
    // Transform the [userInfo] to get the Email of the logged-in User
    val userEmail: LiveData<String> = Transformations.map(userInfo) { user -> user.email }
    // Transform the [userInfo] to get the [Image] model of the logged-in User's Profile Picture
    val userImage: LiveData<Image> = Transformations.map(userInfo) { user ->
        user.profilePicUrl?.run { Image(url = this, headers = headers) }
    }

    // LiveData for the Editable Field values in the screen
    val nameField: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(userName) { userName -> postValue(userName) }
    }
    val bioField: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(userTagline) { userTagline -> postValue(userTagline) }
    }

    // LiveData for the Validations of the Field values
    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    // LiveData for Name validation results
    val nameValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.NAME)

    // LiveData that saves the chosen Profile Image File which will be uploaded on save
    val chosenProfileImageFile: MutableLiveData<File> = MutableLiveData()

    // Transform the [nameField] to see if the value is different from its original
    private val hasNameChanged: LiveData<Boolean> =
        Transformations.map(nameField) { newName -> newName != userName.value }
    // Transform the [bioField] to see if the value is different from its original
    private val hasBioChanged: LiveData<Boolean> =
        Transformations.map(bioField) { newBio -> newBio != userTagline.value }
    // Transform the [chosenProfileImageFile] to see if the user had changed the Profile Picture
    private val hasProfilePictureChanged: LiveData<Boolean> =
        Transformations.map(chosenProfileImageFile) { imageFile: File? -> imageFile?.exists() ?: false }
    // MediatorLiveData to see if any data has been changed to update to the remote
    val hasAnyInfoChanged: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // Mutable List for tracking fields that have been changed
        val changeTrackingList = mutableListOf<String>()
        // Add sources for the tracked fields
        addSource(hasNameChanged) { newValue ->
            postValue(
                trackAndReportFieldChange(
                    KEY_NAME_FIELD,
                    newValue,
                    changeTrackingList
                )
            )
        }
        addSource(hasBioChanged) { newValue ->
            postValue(
                trackAndReportFieldChange(
                    KEY_BIO_FIELD,
                    newValue,
                    changeTrackingList
                )
            )
        }
        addSource(hasProfilePictureChanged) { newValue ->
            postValue(
                trackAndReportFieldChange(
                    KEY_PROFILE_PIC_FIELD,
                    newValue,
                    changeTrackingList
                )
            )
        }
    }

    /**
     * Tracks changes in the [field][newKey] and updates the same to the [changeTracker]
     * when [newKey] is not present in [changeTracker] and [newValue] is `true` indicating that
     * there was a change in this [field][newKey].
     *
     * When [newValue] is `false`, i.e., when there is no modification, any tracked entry
     * for the [field][newKey] is removed from [changeTracker].
     *
     * @param changeTracker [MutableList] for tracking fields that have been changed.
     *
     * @return `true` when there are some fields currently having modifications; `false` otherwise.
     */
    private fun trackAndReportFieldChange(
        newKey: String,
        newValue: Boolean,
        changeTracker: MutableList<String>
    ): Boolean {
        if (newValue) {
            // When there is a modification
            if (changeTracker.none { key -> key == newKey }) {
                // Add the field to be tracked for changes when not previously tracked
                changeTracker.add(newKey)
            }
        } else {
            // When there is no modification, remove the field from tracking if tracked previously
            changeTracker.removeAll { key -> key == newKey }
        }

        // Return true if any field is currently having modifications
        return changeTracker.isNotEmpty()
    }

    // LiveData for launching PhotoOptionsDialogFragment
    val launchPhotoOptions: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for finishing the Activity with a Success Update Result to the Calling Activity
    val finishWithSuccess: MutableLiveData<Event<String>> = MutableLiveData()

    // LiveData for finishing the Activity with No Action Result to the Calling Activity
    val finishWithNoAction: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // LiveData for just finishing the Activity
    val closeAction: MutableLiveData<Event<Boolean>> = MutableLiveData()

    init {
        // When this ViewModel is first initialized..

        // Load complete information of the logged-in user
        loadUserInfo()
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Downloads complete information of the logged-in user into the [userInfo] LiveData.
     */
    private fun loadUserInfo() {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, start downloading complete user information

            // Make the Remote API Call and save the resulting disposable
            compositeDisposable.add(
                userRepository.doFetchUserInfo(user)
                    .subscribeOn(schedulerProvider.io()) //Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { updatedUserInfo: User ->
                            // Update the LiveData with the user information
                            // (This triggers LiveData transformations for field values)
                            userInfo.postValue(updatedUserInfo)
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )

        } else {
            // Reuse existing information from preferences when there is an issue with network connectivity
            userInfo.postValue(user)
        }
    }

    /**
     * Called when there is a change in the Name field.
     * This method updates the change to [nameField] LiveData
     */
    fun onNameChange(name: String) = nameField.postValue(name)

    /**
     * Called when there is a change in the Bio field.
     * This method updates the change to [bioField] LiveData
     */
    fun onBioChange(bio: String) = bioField.postValue(bio)

    /**
     * Called when the "Change Photo" Button is clicked.
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogFragment].
     */
    fun onChangePhoto() = launchPhotoOptions.postValue(Event(true))

    /**
     * Called when an Image was picked by the user from the Gallery, for changing the Profile picture.
     *
     * @param inputStream [InputStream] instance to the URI of the Image Picked.
     */
    fun onGalleryImageSelected(inputStream: InputStream) {
        // Start the [liveProgress] indication
        liveProgress.postValue(Resource.loading(R.string.progress_edit_profile_loading_image))
        // Construct a SingleSource for saving the [inputStream] to an Image File, and save its resulting disposable
        compositeDisposable.add(
            // Create a SingleSource to save the [inputStream] to an Image File, so that the operation can be
            // done in the background
            Single.fromCallable {
                FileUtils.saveInputStreamToFile(
                    inputStream,
                    directory, // Directory to save the resulting Image File
                    "TMP_IMG_PICKED", // Name of the resulting Image File
                    Constants.IMAGE_MAX_HEIGHT_SCALE
                )
            }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { imageFile: File? ->
                        imageFile?.run {
                            // When we have the resulting Image File, update the LiveData for the File chosen
                            chosenProfileImageFile.postValue(imageFile)
                            liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                        } ?: run {
                            // When we do not have the resulting Image File, display an error to the user requesting to retry
                            liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                            messageStringId.postValue(Resource.error(R.string.error_retry))
                        }
                    },
                    // OnError
                    {
                        // When the Image save process failed, display an error to the user requesting to retry
                        liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                        messageStringId.postValue(Resource.error(R.string.error_retry))
                    }
                )
        )
    }

    /**
     * Called when a Photo was clicked by the user, for changing the Profile picture.
     *
     * @param cameraImageProcessor An action lambda that optimizes the Image captured and returns its file path.
     */
    fun onPhotoSnapped(cameraImageProcessor: () -> String) {
        // Start the [liveProgress] indication
        liveProgress.postValue(Resource.loading(R.string.progress_edit_profile_loading_image))
        // Construct a SingleSource for the lambda, and save its resulting disposable
        compositeDisposable.add(
            // Create a SingleSource to the lambda [cameraImageProcessor], so that the operation can be
            // done in the background
            Single.fromCallable {
                cameraImageProcessor()
            }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { imagePath: String ->
                        File(imagePath).apply {
                            // Test for its bounds, and then update the LiveData for the File chosen
                            FileUtils.getImageSize(this)?.let {
                                chosenProfileImageFile.postValue(this)
                                liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                            } ?: run {
                                // When decoding of the Image Bounds failed, display an error to the user
                                // requesting to retry
                                liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                                messageStringId.postValue(Resource.error(R.string.error_retry))
                            }
                        }
                    },
                    // OnError
                    {
                        // When the lambda [cameraImageProcessor] process failed, display an error to the user
                        // requesting to retry
                        liveProgress.postValue(Resource.success()) // Stop the [liveProgress] indication
                        messageStringId.postValue(Resource.error(R.string.error_retry))
                    }
                )
        )
    }

    /**
     * Called when the "Reset to defaults" button in the toolbar is clicked.
     * Re-posts the same user information to [userInfo] to reset any data changed.
     */
    fun onReset() = userInfo.value?.let { user: User -> userInfo.postValue(user) }

    /**
     * Called when the close button in the toolbar is clicked.
     * Triggers an event to finish the Activity.
     */
    fun onClose() = closeAction.postValue(Event(true))

    /**
     * Called when the tick/save button in the toolbar is clicked.
     */
    fun onSave() {
        if (hasAnyInfoChanged.value == true) {
            // Starting the Save operation only if there has been some change in the existing information

            // Get the values of the fields
            val nameValue: String? = nameField.value
            val taglineValue: String? = bioField.value
            val chosenImageFile: File? = chosenProfileImageFile.value

            // Do the validation
            val validations = Validator.validateEditProfileFields(nameValue)
            // Update the validations LiveData to trigger Name validation results LiveData
            validationsList.postValue(validations)

            if (nameValue != null && validations.isNotEmpty()
                && validations.count { validation -> validation.resource.status == Status.SUCCESS } == validations.size
                && checkInternetConnectionWithMessage()
            ) {
                // Perform the Save operation when we have the Name value, right number of success validations
                // and network connectivity

                // Construct a SingleSource for the User's Profile Picture
                val api: Single<String> = if (hasProfilePictureChanged.value == true) {
                    chosenImageFile?.let {
                        // When the Profile Picture has changed

                        // Start the [liveProgress] indication for Image Upload
                        liveProgress.postValue(Resource.loading(R.string.progress_edit_profile_uploading_image))
                        // Upload and Get the URL to the Profile Picture
                        photoRepository.uploadPhoto(it, user)
                    } ?: Single.fromCallable {
                        // When the Profile Image File is unavailable, reuse existing URL to the Profile Picture
                        // (Pass empty when null)
                        userInfo.value?.profilePicUrl ?: ""
                    }
                } else {
                    // When the Profile Picture is not changed, reuse existing URL to the Profile Picture
                    Single.fromCallable {
                        // (Pass empty when null)
                        userInfo.value?.profilePicUrl ?: ""
                    }
                }

                // Complete the Update and save the resulting disposable
                compositeDisposable.add(
                    api.flatMap { profilePicUrl: String ->
                        // Start the [liveProgress] indication for User Info Update
                        liveProgress.postValue(Resource.loading(R.string.progress_edit_profile_save))
                        // Update the User Information to the Remote
                        userRepository.doUpdateUserInfo(
                            user.copy(
                                name = nameValue,
                                tagline = taglineValue,
                                profilePicUrl = profilePicUrl
                            )
                        )
                    }
                        .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                        .subscribe(
                            // OnSuccess
                            { resource: Resource<String> ->
                                // Stop the [liveProgress] indication
                                liveProgress.postValue(Resource.success())
                                if (resource.status == Status.SUCCESS) {
                                    // When Resource status is Success, we have updated the user info
                                    // to the remote successfully.
                                    // Hence trigger an event to close the Activity with Success Result of response
                                    finishWithSuccess.postValue(Event(resource.getData() ?: ""))
                                } else {
                                    // When Resource status is other than Success, user info did not update successfully
                                    // Hence display the Resource message and do not close the activity
                                    messageString.postValue(resource)
                                }
                            },
                            // OnError
                            { throwable: Throwable? ->
                                // Stop the [liveProgress] indication
                                liveProgress.postValue(Resource.success())
                                // Handle and display the appropriate error
                                handleNetworkError(throwable)
                            }
                        )
                )
            }

        } else {
            // If there is no change in the existing information,
            // then close the Activity with no action result
            finishWithNoAction.postValue(Event(true))
        }
    }

}