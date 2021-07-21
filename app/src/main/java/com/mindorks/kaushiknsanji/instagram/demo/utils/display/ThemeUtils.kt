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

package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.content.Context
import android.util.TypedValue

/**
 * Utility object for [android.content.res.Resources.Theme] based operations.
 *
 * @author Kaushik N Sanji
 */
object ThemeUtils {

    // Maintains a thread-independent instance of TypedValue
    private val TL_TYPED_VALUE = ThreadLocal<TypedValue>()

    /**
     * Returns a thread-independent instance of [TypedValue]. A New instance of [TypedValue] will be created
     * if not yet initialized in the current thread.
     *
     * [TypedValue] instance will be released when the thread ends.
     */
    private fun getTypedValue(): TypedValue {
        // Get the thread-independent instance of TypedValue
        var typedValue: TypedValue? = TL_TYPED_VALUE.get()
        // If the thread-independent instance is unavailable, create a new one
        if (typedValue == null) {
            typedValue = TypedValue()
            // Set the thread-independent instance with the new TypedValue
            TL_TYPED_VALUE.set(typedValue)
        }
        // Return the instance of TypedValue
        return typedValue
    }

    /**
     * Returns the Themed Dimension of the ActionBar Height in Pixels.
     *
     * @param context [Context] to access the `Theme` and `Resources`.
     */
    fun getThemedActionBarHeightInPixels(context: Context): Int {
        // Get the TypedValue instance
        val tv = getTypedValue()
        // Retrieve the ActionBarSize from the theme
        context.applicationContext.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
        // Return the dimension in pixel size
        return TypedValue.complexToDimensionPixelSize(tv.data, context.applicationContext.resources.displayMetrics)
    }

}