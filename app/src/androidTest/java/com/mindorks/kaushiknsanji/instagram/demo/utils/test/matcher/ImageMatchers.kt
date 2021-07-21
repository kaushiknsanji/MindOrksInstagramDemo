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

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Utility object for hamcrest [Matcher]s to match the [Drawable]s in [View]s.
 *
 * @author Kaushik N Sanji
 */
object ImageMatchers {

    /**
     * Returns a [Matcher] to match a [View] based on its background resource.
     *
     * @param expectedDrawableResourceId [Int] value of the Drawable resource that is expected to
     * be used as a background for the [View] being matched.
     */
    fun hasBackground(
        @DrawableRes expectedDrawableResourceId: Int
    ): Matcher<View> = object : TypeSafeMatcher<View>() {

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
                "has background resource $expectedDrawableResourceId"
            )
        }

        /**
         * Matches safely the [View] by comparing its actual [View.getBackground]
         * with the expected [Drawable] pointed to by the [expectedDrawableResourceId].
         *
         * @return A [Boolean] result of the comparison of actual vs expected [view] background.
         */
        override fun matchesSafely(view: View): Boolean {
            return assertSameBitmap(
                view.background,
                AppCompatResources.getDrawable(view.context, expectedDrawableResourceId)
            )
        }

    }

    /**
     * Returns a [Matcher] to match a [TextView] based on its Compound Drawables.
     *
     * @param expectedDrawableResourceId [Int] value of the Drawable resource that is
     * expected to be used as one of the Compound Drawables of [TextView] being matched.
     */
    fun hasCompoundDrawable(
        @DrawableRes expectedDrawableResourceId: Int
    ): Matcher<View> = object : BoundedMatcher<View, TextView>(TextView::class.java) {

        /**
         * Generates a description of the object. The description may be part of a
         * a description of a larger object of which this is just a component, so it
         * should be worded appropriately.
         *
         * @param description The description to be built or appended to.
         */
        override fun describeTo(description: Description) {
            // Description shown for the expected case when the match fails
            description.appendText("has CompoundDrawable resource $expectedDrawableResourceId")
        }

        /**
         * Matches safely the [textView] by comparing its actual [TextView.getCompoundDrawables]
         * with the expected [Drawable] pointed to by the [expectedDrawableResourceId].
         *
         * @return A [Boolean] result of the comparison of actual vs expected [Drawable]
         * used as a Compound Drawable in [textView]. Returns `true` if any of
         * its [TextView.getCompoundDrawables] is same as the expected [Drawable] pointed
         * to by the [expectedDrawableResourceId].
         */
        override fun matchesSafely(textView: TextView): Boolean {
            return textView.compoundDrawables.any { actualDrawable: Drawable ->
                assertSameBitmap(
                    actualDrawable,
                    AppCompatResources.getDrawable(textView.context, expectedDrawableResourceId)
                )
            }
        }

    }

    /**
     * Returns a [Matcher] to match an [ImageView] based on its Drawable.
     *
     * @param expectedDrawableResourceId [Int] value of the Drawable resource that is expected to
     * be used as a Drawable for the [ImageView] being matched.
     */
    fun hasImageDrawable(
        @DrawableRes expectedDrawableResourceId: Int
    ): Matcher<View> = object : BoundedMatcher<View, ImageView>(ImageView::class.java) {

        /**
         * Generates a description of the object. The description may be part of a
         * a description of a larger object of which this is just a component, so it
         * should be worded appropriately.
         *
         * @param description The description to be built or appended to.
         */
        override fun describeTo(description: Description) {
            description.appendText("has image resource $expectedDrawableResourceId")
        }

        /**
         * Matches safely the [ImageView] by comparing its actual [ImageView.getDrawable]
         * with the expected [Drawable] pointed to by the [expectedDrawableResourceId].
         *
         * @return A [Boolean] result of the comparison of actual vs expected [imageView] Drawable.
         */
        override fun matchesSafely(imageView: ImageView): Boolean {
            return assertSameBitmap(
                imageView.drawable,
                AppCompatResources.getDrawable(imageView.context, expectedDrawableResourceId)
            )
        }

    }

    /**
     * Method that verifies whether the [android.graphics.Bitmap] from both the
     * Drawables [actualDrawable] and [expectedDrawable] are same or not.
     *
     * @param actualDrawable [Drawable] currently present in the [View] being matched.
     * @param expectedDrawable [Drawable] that needs to be present in the [View] being matched.
     * @return A [Boolean] result of the comparison of [actualDrawable] with the [expectedDrawable].
     * Returns `true` when their [android.graphics.Bitmap]s are same; `false` otherwise.
     * Can return `false` when either of the Drawables are `null`.
     */
    private fun assertSameBitmap(actualDrawable: Drawable?, expectedDrawable: Drawable?): Boolean {
        // Return false when either of the Drawables are null
        if (null == actualDrawable || null == expectedDrawable) {
            return false
        }

        // Read the current Drawable from the Drawables. For StateListDrawable
        // and LevelListDrawable, this will be the child drawable currently in use
        val actualDrawableCurrent = actualDrawable.current
        val expectedDrawableCurrent = expectedDrawable.current

        // Convert the Drawables to Bitmap and return the result of their comparison
        return actualDrawableCurrent.toBitmap().sameAs(expectedDrawableCurrent.toBitmap())
    }

}