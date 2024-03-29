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

package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel

/**
 * Utility class for the management of custom Dialogs shown by Activities/Fragments of the App.
 *
 * @property fragmentManager Instance of [FragmentManager] to show and manage the [DialogFragment]s
 * @constructor Instance of [DialogManager] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class DialogManager(private val fragmentManager: FragmentManager) {

    // DialogFragment instance of the Active Dialog shown if any
    private var activeDialogFragment: DialogFragment? = null

    /**
     * Called during the setup of Activities/Fragments.
     *
     * Looks up the [fragmentManager] for an active dialog if any to be saved in [activeDialogFragment].
     */
    fun setup() {
        fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG)?.let { fragment: Fragment ->
            // If there is an active DialogFragment instance with the tag [DIALOG_FRAGMENT_TAG]
            // then save it in [activeDialogFragment]
            if (DialogFragment::class.java.isInstance(fragment)) {
                activeDialogFragment = (fragment as DialogFragment)
            }
        }
    }

    /**
     * Checks whether the required [dialogFragmentClass] is of the [activeDialogFragment] type and
     * whether the [activeDialogFragment] is shown or not.
     *
     * @return Returns `true` when the [dialogFragmentClass] is of the [activeDialogFragment] type
     * and is currently shown; `false` otherwise
     */
    private fun <T : BaseDialogFragment<out BaseViewModel>> isDialogSameAndActive(dialogFragmentClass: Class<T>): Boolean =
        dialogFragmentClass.isInstance(activeDialogFragment) && (activeDialogFragment?.dialog?.isShowing == true)

    /**
     * Dismisses Active Dialog if any
     */
    fun dismissActiveDialog() {
        activeDialogFragment?.let { dialogFragment: DialogFragment ->
            // Dismiss allowing state loss when a DialogFragment is shown
            dialogFragment.dismissAllowingStateLoss()
            // Clear the Active DialogFragment reference
            activeDialogFragment = null
        }
    }

    /**
     * Displays the Dialog immediately for the DialogFragment of type [dialogFragmentClass] if it is currently not shown.
     *
     * @param T The type of [BaseDialogFragment].
     * @param dialogFragmentClass [Class] of the DialogFragment to be shown.
     * @param creator Lambda that creates and provides the instance of the DialogFragment to be shown.
     */
    fun <T : BaseDialogFragment<out BaseViewModel>> showDialogNow(
        dialogFragmentClass: Class<T>,
        creator: () -> T
    ) {
        // If the DialogFragment required is currently NOT shown, then create the instance and show the Dialog immediately
        if (!isDialogSameAndActive(dialogFragmentClass)) {
            dismissActiveDialog() // Dismiss active dialog if any
            // Create the DialogFragment instance and save
            activeDialogFragment = creator.invoke().also {
                // Also, show the dialog immediately
                it.showNow(fragmentManager, DIALOG_FRAGMENT_TAG)
            }
        }
    }

    /**
     * Displays the Dialog for the DialogFragment of type [dialogFragmentClass] if it is currently not shown.
     *
     * @param T The type of [BaseDialogFragment].
     * @param dialogFragmentClass [Class] of the DialogFragment to be shown.
     * @param creator Lambda that creates and provides the instance of the DialogFragment to be shown.
     */
    fun <T : BaseDialogFragment<out BaseViewModel>> showDialog(
        dialogFragmentClass: Class<T>,
        creator: () -> T
    ) {
        // If the DialogFragment required is currently NOT shown, then create the instance and show the Dialog
        if (!isDialogSameAndActive(dialogFragmentClass)) {
            dismissActiveDialog() // Dismiss active dialog if any
            // Create the DialogFragment instance and save
            activeDialogFragment = creator.invoke().also {
                // Also, show the dialog
                it.show(fragmentManager, DIALOG_FRAGMENT_TAG)
            }
        }
    }

    companion object {
        // Constant used as tags for DialogFragments to be shown and managed using this Utility
        const val DIALOG_FRAGMENT_TAG = "instagram.DIALOG_FRAGMENT_TAG"
    }

}