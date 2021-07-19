package com.mindorks.kaushiknsanji.instagram.demo.utils.test.action

import android.content.res.Resources
import android.view.Menu
import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.InstrumentedTestUtils
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf

/**
 * Utility object for [ViewAction]s to perform Navigation action on [BottomNavigationView] Menu.
 *
 * @author Kaushik N Sanji
 */
object BottomNavigationViewActions {

    /**
     * Navigates to the given [menuItemId] of the [Menu] displayed by [BottomNavigationView].
     *
     * @param menuItemId [Int] Resource Id of the [android.view.MenuItem] to navigate to.
     * @return [ViewAction] to perform the Navigation action to the desired [menuItemId]
     * of the [Menu] displayed by [BottomNavigationView].
     */
    fun navigateTo(
        @IdRes menuItemId: Int
    ): ViewAction = object : ViewAction {

        // Resources to read the Resource name of the [menuItemId] for description
        private var resources: Resources? = null

        /**
         * Returns a [Matcher] that will be tested prior to calling [perform], in order to ensure
         * that the [BottomNavigationView] passed to [perform] meets the provided constraints.
         */
        override fun getConstraints(): Matcher<View> = AllOf.allOf(
            // Should be an instance of BottomNavigationView
            ViewMatchers.isAssignableFrom(BottomNavigationView::class.java),
            // Should be completely visible in the viewable physical screen of the device
            ViewMatchers.isCompletelyDisplayed()
        )

        /**
         * Returns a description of this [ViewAction].
         */
        override fun getDescription(): String = "click on menu item with id '${
            InstrumentedTestUtils.getViewIdDescription(
                resources,
                menuItemId
            )
        }'"

        /**
         * Performs Navigation action to the given [menuItemId] of the [Menu]
         * displayed by [BottomNavigationView].
         *
         * @param uiController [UiController] to use to interact with the UI.
         * @param view [View] to act upon which is the [BottomNavigationView]. Never `null`.
         */
        override fun perform(uiController: UiController, view: View) {
            // Save resources
            resources = view.resources

            // Perform Navigation action on the BottomNavigationView Menu to the given [menuItemId]
            // if found, else throw a RuntimeException (using PerformException)
            with((view as BottomNavigationView).menu) {
                findItem(menuItemId)?.let {
                    view.selectedItemId = menuItemId
                } ?: throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(
                        RuntimeException(
                            InstrumentedTestUtils.getMenuItemNotFoundErrorMessage(this, resources)
                        )
                    )
                    .build()
            }
        }

    }

}