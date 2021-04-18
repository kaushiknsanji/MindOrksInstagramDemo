package com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.AndroidTestUtils
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Utility object for RecyclerView hamcrest [Matcher]s to match its item views.
 *
 * @author Kaushik N Sanji
 */
object RecyclerViewMatchers {

    /**
     * Returns a matcher that matches an Item [View] currently displayed on screen
     * at the item [position][itemPosition] in the [RecyclerView] based on a particular content of a
     * [View] in that Item which is expected to be present.
     *
     * @param itemPosition [Int] value of the position of the Item in the data set
     * of the [RecyclerView.Adapter]
     * @param targetViewInItemContentMatcher A [Matcher] for a particular [View] in the Item
     * @param targetViewIdInItem [Int] value of the Id of the [View] in the Item being matched
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
                    AndroidTestUtils.getViewIdDescription(resources, targetViewIdInItem)
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


}