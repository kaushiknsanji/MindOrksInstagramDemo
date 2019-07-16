package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.widget.TextView


/**
 * Utility object for Text Appearance related modifications with classes like [SpannableStringBuilder]
 *
 * @author Kaushik N Sanji
 */
object TextAppearanceUtils {

    /**
     * Method that sets the Html Text content [htmlTextToSet] on the [textView] passed
     */
    fun setHtmlText(textView: TextView, htmlTextToSet: String) {
        // Initialize a SpannableStringBuilder to build the text
        val spannableStringBuilder = SpannableStringBuilder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For Android N and above
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet, Html.FROM_HTML_MODE_COMPACT))
        } else {
            // For older versions
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet))
        }
        // Set the Spannable Text on TextView with the SPANNABLE Buffer type,
        // for later modification on spannable if required
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE)
    }

    /**
     * Method that prepares and returns the Html Formatted text for the Text String passed [textWithHtmlContent]
     *
     * @return String containing the Html formatted text of [textWithHtmlContent]
     */
    fun getHtmlFormattedText(textWithHtmlContent: String): String {
        // Initialize a SpannableStringBuilder to build the text
        val spannableStringBuilder = SpannableStringBuilder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For Android N and above
            spannableStringBuilder.append(Html.fromHtml(textWithHtmlContent, Html.FROM_HTML_MODE_COMPACT))
        } else {
            // For older versions
            spannableStringBuilder.append(Html.fromHtml(textWithHtmlContent))
        }

        // Return the Formatted Html Text
        return spannableStringBuilder.toString()
    }

}