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

package com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.InstrumentedTestUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.getFirstVisibleItemPosition
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Utility object for RecyclerView hamcrest [Matcher]s to match its item views.
 *
 * @author Kaushik N Sanji
 */
object RecyclerViewMatchers {

    /**
     * Returns a [Matcher] that matches an Item [View] currently displayed on screen
     * at the item [position][itemPosition] in the [RecyclerView] based on a particular content of a
     * [View] in that Item which is expected to be present.
     *
     * @param itemPosition [Int] value of the position of the Item in the data set
     * of the [RecyclerView.Adapter].
     * @param targetViewInItemContentMatcher A [Matcher] for a particular [View] in the Item.
     * @param targetViewIdInItem [Int] value of the Id of the [View] in the Item being matched.
     *
     * @return A [Matcher] that matches a [View] in an Item [View] of [RecyclerView].
     */
    fun hasViewInItemAtPosition(
        itemPosition: Int,
        targetViewInItemContentMatcher: Matcher<View>,
        @IdRes targetViewIdInItem: Int
    ): Matcher<View> = object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

        // Resources to read the Resource name of the [targetViewIdInItem] for description
        private var resources: Resources? = null

        /**
         * Generates a description of the object. The description may be part of a
         * a description of a larger object of which this is just a component, so it
         * should be worded appropriately.
         *
         * @param description The description to be built or appended to.
         */
        override fun describeTo(description: Description) {
            // Description shown for the expected case when the match fails
            description.appendText(
                "has child view with id '${
                    InstrumentedTestUtils.getViewIdDescription(resources, targetViewIdInItem)
                }' in item at position '$itemPosition' and ${
                    targetViewInItemContentMatcher.toString().replace(": is", " as")
                }"
            )
        }

        /**
         * Matches safely the [View] identified by its [id][targetViewIdInItem]
         * in an item of [recyclerView] identified by its [position][itemPosition],
         * based on the [Matcher][targetViewInItemContentMatcher].
         *
         * @return A [Boolean] result of the Matcher [targetViewInItemContentMatcher].
         * Can also be `false` when the item identified by its [position][itemPosition]
         * is not present in the [recyclerView].
         */
        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            // Save resources
            resources = recyclerView.resources

            return recyclerView.findViewHolderForAdapterPosition(itemPosition)
                ?.let { viewHolder: RecyclerView.ViewHolder ->
                    // Match the target child view with its matcher
                    targetViewInItemContentMatcher.matches(
                        viewHolder.itemView.findViewById(targetViewIdInItem)
                    )
                } ?: false
        }

    }

    /**
     * Returns a [Matcher] that matches the position of the first currently visible Item [View] of
     * the [RecyclerView] with the expected [position][expectedItemPosition].
     *
     * @param expectedItemPosition [Int] value of the expected position of the first visible item.
     *
     * @return A [Matcher] that matches First visible Item [View] of [RecyclerView] based on
     * the given [expectedItemPosition].
     */
    fun withFirstVisibleItemAtPosition(
        expectedItemPosition: Int
    ): Matcher<View> = object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

        /**
         * Generates a description of the object.  The description may be part of a
         * a description of a larger object of which this is just a component, so it
         * should be worded appropriately.
         *
         * @param description The description to be built or appended to.
         */
        override fun describeTo(description: Description) {
            // Description shown for the expected case when the match fails
            description.appendText("with item view at position '$expectedItemPosition'")
        }

        /**
         * Matches safely the position of the first currently visible Item View of the [recyclerView]
         * with the expected [position][expectedItemPosition].
         *
         * @return A [Boolean] result of the comparison between [expectedItemPosition] and
         * the detected position of the first currently visible Item View of the [recyclerView].
         */
        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            return expectedItemPosition == recyclerView.getFirstVisibleItemPosition()
        }

    }

}