/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindorks.kaushiknsanji.instagram.demo.ui.photo


import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.FragmentPhotoBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.paracamera.Camera
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

    // Activity Result observer to execute activity call contracts
    // and handle the results in a separate class
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val fragmentResultObserver: PhotoFragmentResultObserver by lazy {
        PhotoFragmentResultObserver(
            requireActivity().activityResultRegistry,
            viewModel,
            camera
        )
    }

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
            // Launch the Camera App to prompt the user to Take a Picture,
            // which saves the captured image to a temporary file
            fragmentResultObserver.takePicture()
        }

        // Register click listener on "Gallery" option
        (binding.includePhotoOptions?.viewPhotoOptionGalleryBackground
            ?: binding.viewPhotoOptionGalleryBackground!!).setOnClickListener {
            // Launch the Gallery App to prompt the user to Pick an Image
            fragmentResultObserver.pickImage()
        }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer for Activity results
        lifecycle.addObserver(fragmentResultObserver)

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