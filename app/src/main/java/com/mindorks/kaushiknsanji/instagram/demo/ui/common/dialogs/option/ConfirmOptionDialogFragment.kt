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

package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        dialogBuilder: MaterialAlertDialogBuilder,
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

    /**
     * Can be overridden to provide the style resource describing the theme to be used for the [Dialog].
     * If not provided, value of `0` will be used as the default [Dialog] theme.
     */
    override fun provideTheme(): Int = R.style.AppTheme_MaterialAlertDialogOverlay

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