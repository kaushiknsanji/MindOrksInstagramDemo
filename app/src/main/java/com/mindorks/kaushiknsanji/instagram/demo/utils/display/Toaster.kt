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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.mindorks.kaushiknsanji.instagram.demo.R

/**
 * Utility object for customizing Android [Toast].
 *
 * @author Kaushik N Sanji
 */
object Toaster {

    /**
     * Method that displays a [Toast] for the given [text] message
     * with the Text in Black Color, overlaid over White Color Background
     */
    @Suppress("DEPRECATION")
    fun show(context: Context, text: CharSequence) {
        // Create a Toast with the text message passed
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        // Set the Background Color to White
        toast.view?.background?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(context, R.color.white), BlendModeCompat.SRC_IN
            )
        // Get the TextView of Toast
        val textView = toast.view?.findViewById(android.R.id.message) as? TextView
        // Set the Text Color to Black
        textView?.setTextColor(ContextCompat.getColor(context, R.color.black))
        // Show the Toast
        toast.show()
    }

}