package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.R.attr.uiOptions
import android.view.View

/**
 * Utility object for [android.view.Window] related metrics and properties.
 *
 * @author Kaushik N Sanji
 */
object WindowUtils {

    /**
     * Checks the [uiOptions] to see if the UI flag [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] has been set.
     * If the flag has been set, then it means the UI Immersive Mode is enabled.
     *
     * NOTE: This assumes that other necessary flags has already been set, and it does not validate them.
     *
     * @return Returns `true` if the UI flag [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] has been already set; `false` otherwise.
     */
    fun isImmersiveModeEnabled(): Boolean = ((uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions)

}