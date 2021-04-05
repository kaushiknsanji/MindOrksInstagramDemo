package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.databinding.DialogProgressTextBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.hide
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.show

/**
 * [BaseDialogFragment] subclass that inflates the layout 'R.layout.dialog_progress_text' to show a Progress Dialog
 * with a status message.
 * [ProgressTextDialogSharedViewModel] is the Primary [androidx.lifecycle.ViewModel] of this DialogFragment, which is shared
 * with other Activities/Fragments that require to show this Dialog.
 *
 * @author Kaushik N Sanji
 */
class ProgressTextDialogFragment : BaseDialogFragment<ProgressTextDialogSharedViewModel>() {

    // ViewBinding instance for this DialogFragment
    private val binding by viewBinding(DialogProgressTextBinding::bind)

    /**
     * Injects dependencies exposed by [DialogFragmentComponent] into DialogFragment.
     */
    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the DialogFragment.
     */
    override fun provideLayoutId(): Int = R.layout.dialog_progress_text

    /**
     * Initializes the [Dialog] of the DialogFragment.
     */
    override fun setupDialog(dialog: AlertDialog, savedInstanceState: Bundle?) {
        super.setupDialog(dialog, savedInstanceState)
        // This Dialog should not be cancelable
        isCancelable = false
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the Progress status LiveData to set/hide the Status Text
        viewModel.progressStatus.observeResource(this) { status: Status, data: Int? ->
            // Setting the Status Text based on Resource Status
            when (status) {
                Status.LOADING -> {
                    // When Loading, initialize and show the Status TextView if available
                    binding.textProgressCircleStatus.run {
                        data?.let { statusResourceId: Int ->
                            show()
                            text = getString(statusResourceId)
                        } ?: run {
                            // When there is no Status Text, hide the Status TextView
                            hide()
                        }
                    }
                }
                else -> {
                    // When NOT Loading, hide the Status TextView
                    binding.textProgressCircleStatus.hide()
                }
            }
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
    override fun provideDialogStyle(): Int = STYLE_NO_INPUT

    companion object {

        /**
         * Factory method to create a new instance of this DialogFragment.
         *
         * @return A new instance of DialogFragment [ProgressTextDialogFragment]
         */
        @JvmStatic
        fun newInstance() =
            ProgressTextDialogFragment().apply {
                arguments = Bundle()
            }

    }
}