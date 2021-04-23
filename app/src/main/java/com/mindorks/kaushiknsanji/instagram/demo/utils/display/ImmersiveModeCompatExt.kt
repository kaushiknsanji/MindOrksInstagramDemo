package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.annotation.TargetApi
import android.os.Build
import android.view.*
import androidx.activity.ComponentActivity
import com.mindorks.kaushiknsanji.instagram.demo.R

/**
 * Kotlin file for extension functions to facilitate Fullscreen Immersive mode setup
 * for `ComponentActivity`.
 *
 * (Based on the article - https://medium.com/swlh/modifying-system-ui-visibility-in-android-11-e66a4128898b)
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [ComponentActivity] which hides system bars to make the [ComponentActivity]
 * go fullscreen.
 *
 * In order to show the [ComponentActivity] in fullscreen by default when launched, call to this
 * extension function when the [ComponentActivity.getWindow] gets the focus in
 * [ComponentActivity.onWindowFocusChanged] callback.
 *
 * For devices with display cutouts, ensure to also call to [showBelowCutout] in order to prevent
 * app content jumping in and out of cutout area whenever immersive mode is activated/deactivated.
 *
 * @see showSystemUi
 * @see showBelowCutout
 * @see addSystemUiVisibilityListener
 */
fun ComponentActivity.hideSystemUi() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // For devices with Android R (API level 30) and beyond,
        // use WindowInsetsController to go immersive
        window.insetsController?.apply {
            // Set the behavior of system bars to be revealed temporarily only when the user interacts
            // with gestures such as swiping from the edge of the screen where the bar is hidden.
            // This prevents revealing the bars on usual touch/drag/zoom gesture interactions.
            // (Alternative to the deprecated "View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY")
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Set the Color of Navigation Bar to a translucent one, so that it appears translucent
            // when revealed through swipe/tap gesture.
            // (Alternative to the deprecated window flag "WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION")
            window.navigationBarColor = getColor(R.color.immersiveModeSystemBarColor)

            // Finally, hide all the bars - navigation, status and caption bars
            // (Alternative to the deprecated "View.SYSTEM_UI_FLAG_HIDE_NAVIGATION" and "View.SYSTEM_UI_FLAG_FULLSCREEN")
            hide(WindowInsets.Type.systemBars())
        }
    } else {
        // For devices not yet in Android R (API level 30),
        // use the legacy System UI Visibility flags to go immersive

        // Set the System UI Visibility flags
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                // Hide Navigation bar
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        // Hide Status bar
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        // Ensure the app content stays behind the bars when revealed,
                        // in order to avoid content jump and resize caused by the bars being revealed
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        // Reveal the bars only on swipe/tap gesture
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Ensure Navigation bar is translucent by including its window flag, so that it appears
        // translucent when revealed through swipe/tap gesture
        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}

/**
 * Extension function on [ComponentActivity] which shows system bars to make the [ComponentActivity]
 * return from fullscreen. App content will still appear behind the bars in order to avoid content
 * jump and another layout measure caused by the bars being revealed.
 *
 * @see hideSystemUi
 * @see addSystemUiVisibilityListener
 */
fun ComponentActivity.showSystemUi() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // For devices with Android R (API level 30) and beyond,
        // use Window and WindowInsetsController to return from immersive

        // Ensure the app content stays behind the bars when revealed,
        // in order to avoid content jump and resize caused by the bars being revealed
        // (Alternative to the deprecated "View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION" and
        // "View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN")
        window.setDecorFitsSystemWindows(false)

        // Finally, reveal the system bars
        window.insetsController?.show(WindowInsets.Type.systemBars())
    } else {
        // For devices not yet in Android R (API level 30),
        // use the legacy System UI Visibility flags to return from immersive

        // Reveal the bars by ensuring the app content stays behind them,
        // in order to avoid content jump and resize caused by the bars being revealed
        // (Setting the System UI Visibility flags again clears the flags previously set)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }
}

/**
 * Extension function on [ComponentActivity] which ensures app content is shown always
 * below the display cutouts.
 *
 * Applicable only to devices with [Build.VERSION_CODES.P] and above. In devices with
 * [Build.VERSION_CODES.P] and above, app content is shown in the cutout area by default unless the
 * System UI Visibility flags [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION]
 * and [View.SYSTEM_UI_FLAG_FULLSCREEN] are set/cleared causing the content to jump in and out of
 * cutout area performing another layout measure of the window. Hence to avoid this jump, overlap and
 * layout measure, we need to show the content below the cutout area in such devices.
 *
 * To be called from [ComponentActivity.onCreate].
 *
 * @see hideSystemUi
 */
@TargetApi(Build.VERSION_CODES.P)
fun ComponentActivity.showBelowCutout() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // Never allow the window to overlap with the cutout area
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
    }
}

/**
 * Extension function on [Window] which registers a System UI visibility listener
 * to receive events on the visibility change of System Bars, in order to capture the
 * current visibility state of System Bars.
 *
 * @param visibilityListener A Lambda to take action based on the System Bars visibility state
 * `isVisible` argument value passed to it. `isVisible` will be `true` when the System Bars
 * are shown; `false` otherwise.
 *
 * To be called from [ComponentActivity.onCreate].
 *
 * @see hideSystemUi
 * @see showSystemUi
 */
fun Window.addSystemUiVisibilityListener(visibilityListener: (isVisible: Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // For devices with Android R (API level 30) and beyond,
        // register listener on WindowInsets to read the insets
        decorView.setOnApplyWindowInsetsListener { view, insets ->
            // We are not interested in applying the insets ourselves since we are only interested
            // in reading the visibility of System bars, so delegate applying the insets to the View
            val remainingInsets = view.onApplyWindowInsets(insets)

            // Invoke the lambda to pass the visibility state of System bars
            visibilityListener(
                // Checking individually the visibility of Navigation bars and Status bars,
                // since the Caption bars which is part of systemBars() can but not always be present.
                remainingInsets.isVisible(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            )

            // Return the remaining insets present after applying them via the View
            remainingInsets
        }
    } else {
        // For devices not yet in Android R (API level 30),
        // register the legacy System UI Visibility change listener to read the
        // current visibility flags set
        @Suppress("DEPRECATION")
        decorView.setOnSystemUiVisibilityChangeListener { visibilityFlags: Int ->
            // Invoke the lambda to pass the visibility state of System bars
            visibilityListener(
                // Checking the visibility of Navigation bars
                // When unset, this flag will be 0, which means the System Bars are shown; otherwise hidden
                (visibilityFlags and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0
            )
        }
    }
}

/**
 * Extension function on [ComponentActivity] which toggles window immersion
 * based on the value of [immersiveMode] passed.
 *
 * @param immersiveMode A [Boolean] value to toggle window immersion.
 * Pass `true` to go immersive; `false` otherwise.
 */
fun ComponentActivity.goImmersiveWhen(immersiveMode: Boolean) {
    if (immersiveMode) {
        // When we need to go immersive, hide the System bars
        hideSystemUi()
    } else {
        // When we need to return from immersive, show the System bars
        showSystemUi()
    }
}