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

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers

/**
 * Utility object for a collection of hamcrest [Matcher]s to match [TextInputLayout]s.
 *
 * @author Kaushik N Sanji
 */
object TextInputLayoutMatchers {

    /**
     * Returns a [Matcher] that matches [TextInputLayout] based on its error value matched by
     * its [errorMatcher].
     *
     * @param errorMatcher [Matcher] of type [String] that matches the expected error value
     * shown on a [TextInputLayout].
     *
     * @return A [Matcher] that matches [TextInputLayout].
     */
    fun hasErrorText(
        errorMatcher: Matcher<String>
    ): Matcher<View> = object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {

        /**
         * Generates a description of the object. The description may be part of a
         * a description of a larger object of which this is just a component, so it
         * should be worded appropriately.
         *
         * @param description The description to be built or appended to.
         */
        override fun describeTo(description: Description) {
            description.appendText("with error: ")
            errorMatcher.describeTo(description)
        }

        /**
         * Matches safely the [textInputLayout] based on the provided [errorMatcher]
         * for expected error.
         *
         * @return A [Boolean] result of the Matcher [errorMatcher]. Returns `true` only when the
         * error is enabled on [textInputLayout], i.e., [TextInputLayout.setErrorEnabled] and
         * the [errorMatcher] matches the error.
         */
        override fun matchesSafely(textInputLayout: TextInputLayout): Boolean =
            textInputLayout.isErrorEnabled && errorMatcher.matches(textInputLayout.error)
    }

    /**
     * Returns a [Matcher] that matches [TextInputLayout] based on its error [expectedError] value.
     *
     * @param expectedError [String] value of the error expected to be shown on a [TextInputLayout].
     *
     * @return A [Matcher] that matches [TextInputLayout].
     */
    fun hasErrorText(
        expectedError: String
    ): Matcher<View> = hasErrorText(Matchers.`is`(expectedError))
}