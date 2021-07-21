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

package com.mindorks.kaushiknsanji.instagram.demo.utils.test.action

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.InstrumentedTestUtils
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf

/**
 * Utility object for [ViewAction]s to perform interaction on [View]s in
 * [androidx.recyclerview.widget.RecyclerView] items.
 *
 * @author Kaushik N Sanji
 */
object RecyclerViewItemActions {

    // Constant to interact with the Root View of an Item
    const val ROOT_VIEW_ID = -1

    // Function type for providing default Constraints that ensure the Item View passed
    // to perform meets this criteria
    private val defaultConstraints: (containerViewId: Int) -> Matcher<View> =
        { containerViewId: Int ->
            AllOf.allOf(
                // Parent of Item View should be a RecyclerView of the given Id
                ViewMatchers.withParent(ViewMatchers.withId(containerViewId)),
                // Item View needs to be displayed (not necessarily completely)
                // in the viewable physical screen of the device
                ViewMatchers.isDisplayed()
            )
        }

    /**
     * Performs the provided [actionOnView][ViewAction] on the [View] in the RecyclerView Item
     * identified by its resource [targetViewId].
     *
     * @param targetViewId [Int] value of the Id of the [View] in the Item on which the
     * provided action needs to be performed. Can be [ROOT_VIEW_ID] if action needs to be performed
     * on the Item View root instead.
     * @param containerViewId [Int] value of the Id of the [View] containing the
     * View with [targetViewId], which will be the Id of the [androidx.recyclerview.widget.RecyclerView].
     * @param actionOnView [ViewAction] that needs to be performed on the View with [targetViewId].
     * @param targetViewConstraints View [Matcher] constraints that ensure the Item View passed to
     * [ViewAction.perform] meets this criteria. Defaulted to [defaultConstraints].
     * @return [ViewAction] that performs an action on a View in an Item View of RecyclerView.
     */
    fun actionOnViewInItem(
        @IdRes targetViewId: Int,
        @IdRes containerViewId: Int,
        actionOnView: ViewAction,
        targetViewConstraints: Matcher<View> = defaultConstraints(containerViewId)
    ): ViewAction = object : ViewAction {

        // Resources to read the Resource name of the [targetViewId] for description
        private var resources: Resources? = null

        /**
         * Returns a [Matcher] that will be tested prior to calling [perform], in order to ensure
         * that the [View] passed to [perform] meets the provided [targetViewConstraints].
         */
        override fun getConstraints(): Matcher<View> = targetViewConstraints

        /**
         * Returns a description of this [ViewAction].
         */
        override fun getDescription(): String {
            return if (targetViewId == ROOT_VIEW_ID) {
                // When the target View is the Item View root
                "performing ViewAction ${actionOnView.description} on the Item View"
            } else {
                // When the target View is a View in the Item View
                "performing ViewAction ${actionOnView.description} on a child View with " +
                        "specified id '${
                            InstrumentedTestUtils.getViewIdDescription(
                                resources,
                                targetViewId
                            )
                        }'"
            }
        }

        /**
         * Performs the provided action [actionOnView] on the given [itemView].
         *
         * @param uiController [UiController] to use to interact with the UI.
         * @param itemView [View] to act upon. Never `null`.
         */
        override fun perform(uiController: UiController, itemView: View) {
            // Save resources
            resources = itemView.resources

            // Perform the provided action on the Item View
            if (targetViewId == ROOT_VIEW_ID) {
                // When the target View is the Item View root
                actionOnView.perform(uiController, itemView)
            } else {
                // When the target View is a View in the Item View
                actionOnView.perform(uiController, itemView.findViewById(targetViewId))
            }
        }
    }
}