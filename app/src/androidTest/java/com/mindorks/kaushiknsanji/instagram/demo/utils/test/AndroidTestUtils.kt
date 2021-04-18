package com.mindorks.kaushiknsanji.instagram.demo.utils.test

import android.content.res.Resources
import androidx.annotation.IdRes

/**
 * Utility object that provides utility functions to facilitate all Instrumented Tests.
 *
 * @author Kaushik N Sanji
 */
object AndroidTestUtils {

    /**
     * Returns a description of a [android.view.View] having the Resource name of its [viewId]
     * if available for use with custom view [org.hamcrest.Matcher]
     * and [androidx.test.espresso.ViewAction]
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

}