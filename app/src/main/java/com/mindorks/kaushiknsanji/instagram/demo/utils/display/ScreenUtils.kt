package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.content.res.Resources

/**
 * Utility object for Screen related metrics.
 *
 * @author Kaushik N Sanji
 */
object ScreenUtils {

    /**
     * Returns [Int] value of the Width of the Screen in Pixels
     */
    fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

    /**
     * Returns [Int] value of the Height of the Screen in Pixels
     */
    fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

}