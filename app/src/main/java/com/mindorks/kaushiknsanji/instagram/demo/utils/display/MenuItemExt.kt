package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.view.MenuItem

/**
 * Kotlin file for extension functions on android `MenuItem`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [MenuItem] to make the [MenuItem] visible.
 */
fun MenuItem.show() {
    isVisible = true
}

/**
 * Extension function on [MenuItem] to hide the [MenuItem].
 */
fun MenuItem.hide() {
    isVisible = false
}

/**
 * Extension function on [MenuItem] to enable the [MenuItem].
 */
fun MenuItem.enable() {
    isEnabled = true
}

/**
 * Extension function on [MenuItem] to disable the [MenuItem].
 */
fun MenuItem.disable() {
    isEnabled = false
}

/**
 * Extension function on [MenuItem] to make the [MenuItem] visible and enabled.
 */
fun MenuItem.showAndEnable() {
    show()
    enable()
}

/**
 * Extension function on [MenuItem] to make the [MenuItem] hidden and disabled.
 */
fun MenuItem.hideAndDisable() {
    hide()
    disable()
}

/**
 * Extension function on [MenuItem] to "show and enable" or "hide and disable" the [MenuItem]
 * based on the provided [visibility condition][visibilityCondition].
 * When [visibilityCondition] evaluates to `true`, then this [MenuItem] will be
 * shown and enabled; otherwise hidden and disabled.
 */
fun MenuItem.showAndEnableWhen(visibilityCondition: Boolean) {
    if (visibilityCondition) {
        showAndEnable()
    } else {
        hideAndDisable()
    }
}