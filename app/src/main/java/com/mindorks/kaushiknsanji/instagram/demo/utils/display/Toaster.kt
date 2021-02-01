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
    fun show(context: Context, text: CharSequence) {
        //Create a Toast with the text message passed
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        //Set the Background Color to White
        toast.view.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(context, R.color.white), BlendModeCompat.SRC_IN
            )
        //Get the TextView of Toast
        val textView = toast.view.findViewById(android.R.id.message) as TextView
        //Set the Text Color to Black
        textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        //Show the Toast
        toast.show()
    }

}