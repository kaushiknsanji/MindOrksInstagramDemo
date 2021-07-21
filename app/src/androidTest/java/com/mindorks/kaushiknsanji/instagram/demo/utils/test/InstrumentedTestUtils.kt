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

package com.mindorks.kaushiknsanji.instagram.demo.utils.test

import android.content.res.Resources
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.view.forEachIndexed

/**
 * Utility object that provides utility functions to facilitate all Instrumented Tests.
 *
 * @author Kaushik N Sanji
 */
object InstrumentedTestUtils {

    /**
     * Returns a description of a [android.view.View] having the Resource name of its [viewId]
     * if available for use with custom view [org.hamcrest.Matcher]
     * and [androidx.test.espresso.ViewAction].
     *
     * @param resources [Resources] instance to read the Resource name of [viewId].
     * Can be `null` when not available.
     * @param viewId [Int] value of the Id of a [android.view.View] whose description is required.
     * @return [String] containing the description of the [android.view.View] with [viewId].
     */
    fun getViewIdDescription(resources: Resources?, @IdRes viewId: Int): String =
        if (resources != null) {
            // When we have the resource instance
            try {
                // Try to find the Resource name of the [viewId] for description
                // and return the same if successful
                resources.getResourceName(viewId)
            } catch (e: Resources.NotFoundException) {
                // On failure, when the resource of [viewId] is not available,
                // provide a different description
                "$viewId (resource name not found)"
            }
        } else {
            // When we do not have the resource instance, return the String representation
            // of [viewId] for description
            viewId.toString()
        }

    /**
     * Prepares and returns an error message for a [MenuItem] not found in a
     * [menu] handled and displayed by a [android.view.View].
     *
     * @param menu [Menu] displayed by a [android.view.View].
     * @param resources [Resources] instance to read the Resource name of [MenuItem.getItemId].
     * @return [String] representing the error message that contains a list
     * of [MenuItem]s found in the [menu] displayed by a [android.view.View].
     */
    fun getMenuItemNotFoundErrorMessage(menu: Menu, resources: Resources?): String =
        with(StringBuilder("Menu item was not found, available menu items:")) {
            // Read the line separator of the platform
            val newLineSeparator = System.getProperty("line.separator")

            // Append a new line
            append(newLineSeparator)

            // Iterate through all the Menu items of the Menu displayed by the View in order to
            // build the message with the Menu Item details
            menu.forEachIndexed { position: Int, menuItem: MenuItem ->
                // For each MenuItem at position
                append("[MenuItem] position=$position")

                // Append the title of the MenuItem if present
                menuItem.title?.let { append(", title=$it") }

                // Append the Resource name of MenuItem if Resources instance is available
                resources?.let {
                    append(", id=${getViewIdDescription(it, menuItem.itemId)}")
                }

                // Append a new line
                append(newLineSeparator)
            }

            // Return the complete error message built
            toString()
        }

}