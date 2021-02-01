package com.mindorks.kaushiknsanji.instagram.demo.utils.display

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.core.text.HtmlCompat


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
        // Set the Spannable Text on TextView with the SPANNABLE Buffer type,
        // for later modification on spannable if required
        textView.setText(
            // Initialize a SpannableStringBuilder to build the text
            SpannableStringBuilder().apply {
                // Append the Html Text in Html format
                append(HtmlCompat.fromHtml(htmlTextToSet, HtmlCompat.FROM_HTML_MODE_COMPACT))
            },
            TextView.BufferType.SPANNABLE
        )
    }

    /**
     * Method that prepares and returns the Html Formatted text for the Text String passed [textWithHtmlContent]
     *
     * @return String containing the Html formatted text of [textWithHtmlContent]
     */
    fun getHtmlFormattedText(textWithHtmlContent: String): String =
        SpannableStringBuilder().apply {
            // Use SpannableStringBuilder to append the Html Text in Html format
            append(HtmlCompat.fromHtml(textWithHtmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT))
        }.toString() // Return the Formatted Html Text

}