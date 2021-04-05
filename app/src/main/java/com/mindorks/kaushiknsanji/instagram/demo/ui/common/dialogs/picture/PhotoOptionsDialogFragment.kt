package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.databinding.DialogPhotoOptionsBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding

/**
 * [BaseDialogFragment] subclass that inflates the layout 'R.layout.dialog_photo_options' to show a Dialog
 * with Photo Options to capture/select a picture.
 * [PhotoOptionsDialogSharedViewModel] is the Primary [androidx.lifecycle.ViewModel] of this DialogFragment, which is shared
 * with other Activities/Fragments that require to show this Dialog.
 *
 * @author Kaushik N Sanji
 */
class PhotoOptionsDialogFragment : BaseDialogFragment<PhotoOptionsDialogSharedViewModel>() {

    // ViewBinding instance for this DialogFragment
    private val binding by viewBinding(DialogPhotoOptionsBinding::bind)

    /**
     * Injects dependencies exposed by [DialogFragmentComponent] into DialogFragment.
     */
    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the DialogFragment.
     */
    override fun provideLayoutId(): Int = R.layout.dialog_photo_options

    /**
     * Initializes the Layout of the DialogFragment.
     */
    override fun setupView(view: View, savedInstanceState: Bundle?) {
        super.setupView(view, savedInstanceState)
        // Register click listener on "Camera" option
        binding.includePhotoOptions.viewPhotoOptionCameraBackground.setOnClickListener {
            // Delegate to the Primary Shared ViewModel
            viewModel.onOptionCamera()
        }

        // Register click listener on "Gallery" option
        binding.includePhotoOptions.viewPhotoOptionGalleryBackground.setOnClickListener {
            // Delegate to the Primary Shared ViewModel
            viewModel.onOptionGallery()
        }
    }

    /**
     * Can be overridden to provide the style resource describing the theme to be used for the [Dialog].
     * If not provided, value of `0` will be used as the default [Dialog] theme.
     */
    override fun provideTheme(): Int = R.style.AppTheme_MaterialAlertDialogOverlay

    /**
     * Can be overridden to customize the basic appearance and behavior of the
     * fragment's dialog by providing one of these style values [androidx.fragment.app.DialogFragment.STYLE_NORMAL],
     * [androidx.fragment.app.DialogFragment.STYLE_NO_TITLE], [androidx.fragment.app.DialogFragment.STYLE_NO_FRAME], or
     * [androidx.fragment.app.DialogFragment.STYLE_NO_INPUT]. [androidx.fragment.app.DialogFragment.STYLE_NORMAL] will
     * be the Default Style used.
     */
    override fun provideDialogStyle(): Int = STYLE_NO_TITLE

    companion object {

        // Request code for Image Pick
        const val REQUEST_IMAGE_PICK = 1001

        /**
         * Factory method to create a new instance of this DialogFragment.
         *
         * @return A new instance of DialogFragment [PhotoOptionsDialogFragment].
         */
        @JvmStatic
        fun newInstance() =
            PhotoOptionsDialogFragment().apply {
                arguments = Bundle()
            }
    }
}