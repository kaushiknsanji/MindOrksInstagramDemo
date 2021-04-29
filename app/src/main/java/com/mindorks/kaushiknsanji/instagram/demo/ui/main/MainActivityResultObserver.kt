package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.LifecycleOwner
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivityResultContracts
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivityResultObserver
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity
import java.io.Serializable

/**
 * [MainActivity] Lifecycle observer class to receive and handle the activity result in a separate
 * class for the calls being registered via [registry].
 *
 * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks
 * @property viewModel [MainViewModel] instance which is the Primary ViewModel of the [MainActivity]
 * to delegate certain actions to it in the callbacks.
 * @property sharedViewModel [MainSharedViewModel] instance which is the Shared ViewModel of
 * the [MainActivity] to delegate certain actions to it in the callbacks.
 *
 * @constructor Creates an instance of [MainActivityResultObserver] for registering calls, storing
 * and handling their callbacks in a separate class.
 *
 * @author Kaushik N Sanji
 */
class MainActivityResultObserver(
    registry: ActivityResultRegistry,
    private val viewModel: MainViewModel,
    private val sharedViewModel: MainSharedViewModel
) : BaseActivityResultObserver<MainActivity>(registry) {

    // Launcher for EditProfileActivity
    private lateinit var editProfile: ActivityResultLauncher<Map<String, Serializable>>

    // Launcher for PostDetailActivity
    private lateinit var postDetail: ActivityResultLauncher<Map<String, Serializable>>

    // Launcher for PostLikeActivity
    private lateinit var postLike: ActivityResultLauncher<Map<String, Serializable>>

    /**
     * Called after the [LifecycleOwner]'s `onCreate` method returns.
     *
     * This method registers all the required
     * [androidx.activity.result.ActivityResultLauncher]s for the [MainActivity].
     *
     * @param registry [ActivityResultRegistry] that stores all the
     * [androidx.activity.result.ActivityResultCallback]s for all the registered calls.
     * @param owner [LifecycleOwner] of the [MainActivity] that makes the call.
     * @param activity [MainActivity] instance derived from [owner].
     */
    override fun initResultLaunchers(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        activity: MainActivity
    ) {
        // Register the calls for EditProfileActivity
        editProfile = editProfileLauncher(registry, owner, activity)

        // Register the calls for PostDetailActivity
        postDetail = postDetailLauncher(registry, owner, activity)

        // Register the calls for PostLikeActivity
        postLike = postLikeLauncher(registry, owner)
    }

    /**
     * Registers the calls for [EditProfileActivity] and stores their
     * [androidx.activity.result.ActivityResultCallback]s via the [registry].
     *
     * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks.
     * @param owner [LifecycleOwner] of the [MainActivity] that makes the call.
     * @param activity [MainActivity] instance derived from [owner].
     *
     * @return An [ActivityResultLauncher] for [EditProfileActivity] to execute the call contracts.
     */
    private fun editProfileLauncher(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        activity: MainActivity
    ): ActivityResultLauncher<Map<String, Serializable>> = registry.register(
        reqKeyGenerator(TAG, EditProfileActivity.REQUEST_EDIT_PROFILE),
        owner,
        MainActivityResultContracts.EditProfile()
    ) { bundle: Bundle ->
        bundle.takeUnless { it.isEmpty }?.run {
            // Taking action based on the Result codes
            when (BaseActivityResultContracts.getResultCode(this)) {
                // For the Successful edit
                EditProfileActivity.RESULT_EDIT_PROFILE_SUCCESS -> {
                    // Delegate to the MainSharedViewModel, to trigger profile updates in HomeFragment
                    // and ProfileFragment
                    sharedViewModel.onEditProfileSuccess()
                    // Display the success message if available
                    getString(EditProfileActivity.EXTRA_RESULT_EDIT_SUCCESS)
                        ?.takeUnless { it.isBlank() }?.let {
                            activity.showMessage(it)
                        }
                }
                // For the Update that did not require any action as there was no change
                EditProfileActivity.RESULT_EDIT_PROFILE_NO_ACTION -> {
                    // Display a message to the user saying no changes were done
                    activity.showMessage(R.string.message_edit_profile_no_change)
                }
            }
        }
    }

    /**
     * Registers the calls for [PostDetailActivity] and stores their
     * [androidx.activity.result.ActivityResultCallback]s via the [registry].
     *
     * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks
     * @param owner [LifecycleOwner] of the [MainActivity] that makes the call.
     * @param activity [MainActivity] instance derived from [owner].
     *
     * @return An [ActivityResultLauncher] for [PostDetailActivity] to execute the call contracts.
     */
    private fun postDetailLauncher(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        activity: MainActivity
    ): ActivityResultLauncher<Map<String, Serializable>> = registry.register(
        reqKeyGenerator(TAG, PostDetailActivity.REQUEST_POST_DETAIL),
        owner,
        MainActivityResultContracts.PostDetail()
    ) { bundle: Bundle ->
        bundle.takeUnless { it.isEmpty }?.run {
            // Taking action based on the Result codes
            when (BaseActivityResultContracts.getResultCode(this)) {
                // For the Successful Delete
                PostDetailActivity.RESULT_DELETE_POST_SUCCESS -> {
                    // When we have Intent extras of the result

                    // Delegate to the MainSharedViewModel, to trigger profile updates in HomeFragment
                    // and ProfileFragment
                    getString(PostDetailActivity.EXTRA_RESULT_DELETED_POST_ID)
                        ?.takeUnless { it.isBlank() }
                        ?.let { sharedViewModel.onPostItemDeleted(it) }
                    // Display the success message if available
                    getString(PostDetailActivity.EXTRA_RESULT_DELETE_POST_SUCCESS)
                        ?.takeUnless { it.isBlank() }?.let {
                            activity.showMessage(it)
                        }
                }

                // For Post Like Updates
                PostDetailActivity.RESULT_LIKE_POST -> {
                    // When we have Intent extras of the result

                    // Delegate to the MainSharedViewModel, to trigger the item update in HomeFragment
                    getString(PostDetailActivity.EXTRA_RESULT_LIKE_POST_ID)
                        ?.takeUnless { it.isBlank() }?.let { postId: String ->
                            sharedViewModel.onPostLikeUpdate(
                                postId,
                                getBoolean(PostDetailActivity.EXTRA_RESULT_LIKE_POST_STATE)
                            )
                        }
                }
            }
        }
    }

    /**
     * Registers the calls for [PostLikeActivity] and stores their
     * [androidx.activity.result.ActivityResultCallback]s via the [registry].
     *
     * @param registry [ActivityResultRegistry] instance to register the calls and store their callbacks
     * @param owner [LifecycleOwner] of the [MainActivity] that makes the call.
     *
     * @return An [ActivityResultLauncher] for [PostLikeActivity] to execute the call contracts.
     */
    private fun postLikeLauncher(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner
    ): ActivityResultLauncher<Map<String, Serializable>> = registry.register(
        reqKeyGenerator(TAG, PostLikeActivity.REQUEST_POST_LIKE),
        owner,
        MainActivityResultContracts.PostLike()
    ) { bundle: Bundle ->
        bundle.takeUnless { it.isEmpty }?.run {
            // Taking action based on the Result codes
            when (BaseActivityResultContracts.getResultCode(this)) {
                // For Post Like Updates
                PostLikeActivity.RESULT_LIKE_POST -> {
                    // When we have Intent extras of the result

                    // Delegate to the MainSharedViewModel, to trigger the item update in HomeFragment
                    getString(PostLikeActivity.EXTRA_RESULT_LIKE_POST_ID)
                        ?.takeUnless { it.isBlank() }?.let { postId: String ->
                            sharedViewModel.onPostLikeUpdate(
                                postId,
                                getBoolean(PostLikeActivity.EXTRA_RESULT_LIKE_POST_STATE)
                            )
                        }
                }
            }
        }
    }

    /**
     * Launches [EditProfileActivity] with the given input [intentExtrasMap].
     *
     * @param intentExtrasMap [Map] containing the Intent Extras to load from.
     */
    fun launchEditProfile(intentExtrasMap: Map<String, Serializable> = emptyMap()) {
        editProfile.launch(intentExtrasMap)
    }

    /**
     * Launches [PostDetailActivity] with the given input [intentExtrasMap].
     *
     * @param intentExtrasMap [Map] containing the Intent Extras to load from.
     */
    fun launchPostDetail(intentExtrasMap: Map<String, Serializable> = emptyMap()) {
        postDetail.launch(intentExtrasMap)
    }

    /**
     * Launches [PostLikeActivity] with the given input [intentExtrasMap].
     *
     * @param intentExtrasMap [Map] containing the Intent Extras to load from.
     */
    fun launchPostLike(intentExtrasMap: Map<String, Serializable> = emptyMap()) {
        postLike.launch(intentExtrasMap)
    }

    companion object {
        // Constant used for logs and also in Request Keys
        const val TAG = "MainActivityResultObserver"
    }

}