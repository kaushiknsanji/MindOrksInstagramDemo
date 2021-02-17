package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment

/**
 * [BaseDialogFragment] subclass that constructs and shows a Confirmation Type of Dialog to request the
 * User to re-confirm the previous action.
 * [ConfirmOptionDialogSharedViewModel] is the Primary [androidx.lifecycle.ViewModel] of this DialogFragment,
 * which is shared with other Activities/Fragments that require to show this Dialog.
 *
 * @author Kaushik N Sanji
 */
class ConfirmOptionDialogFragment : BaseDialogFragment<ConfirmOptionDialogSharedViewModel>() {

    /**
     * Injects dependencies exposed by [DialogFragmentComponent] into DialogFragment.
     */
    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    /**
     * Constructs a Template [android.app.Dialog] with the required elements using [dialogBuilder],
     * since we can only hide the elements that are initialized.
     */
    override fun constructTemplateDialog(
        dialogBuilder: AlertDialog.Builder,
        savedInstanceState: Bundle?
    ) {
        super.constructTemplateDialog(dialogBuilder, savedInstanceState)

        dialogBuilder.apply {
            // Set the Default Title
            setTitle(R.string.title_dialog_confirm_default)

            // Set the Default Message
            setMessage(R.string.message_dialog_confirm_default)

            // Set the Default Positive Button Name and its Listener
            setPositiveButton(R.string.dialog_confirm_button_positive_default) { _, _ ->
                // Delegate to the ViewModel to trigger an event
                viewModel.onDialogPositiveButtonClicked()
            }

            // Set the Default Negative Button Name and its Listener
            setNegativeButton(R.string.dialog_confirm_button_negative_default) { _, _ ->
                // Delegate to the ViewModel to trigger an event
                viewModel.onDialogNegativeButtonClicked()
            }
        }

    }

    companion object {

        /**
         * Factory method to create a new instance of this DialogFragment.
         *
         * @return A new instance of DialogFragment [ConfirmOptionDialogFragment]
         */
        @JvmStatic
        fun newInstance() =
            ConfirmOptionDialogFragment().apply {
                arguments = Bundle()
            }

    }
}