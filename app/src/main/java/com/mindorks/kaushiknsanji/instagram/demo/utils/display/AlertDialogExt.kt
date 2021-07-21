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

import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.mindorks.kaushiknsanji.instagram.demo.R

/**
 * Kotlin file for extension functions on android `AlertDialog`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [AlertDialog] to retrieve the Top Panel View
 * that contains a TextView for Title. Can be `null` if the Top Panel is NOT present.
 */
private fun AlertDialog.getTitleTopPanel(): LinearLayout? =
    findViewById(R.id.topPanel)

/**
 * Extension function on [AlertDialog] to retrieve the [Space] view used
 * for adding space over the Content Panel when Top Panel is NOT present
 * and Content Panel is present. Can be `null` if NOT present.
 */
private fun AlertDialog.getTextSpacerNoTitle(): Space? =
    findViewById(R.id.textSpacerNoTitle)

/**
 * Extension function on [AlertDialog] to retrieve the Content Panel View
 * that contains a TextView for Message. Can be `null` if the Content Panel is NOT present.
 */
private fun AlertDialog.getContentPanelForMessage(): FrameLayout? =
    findViewById(R.id.contentPanel)

/**
 * Extension function on [AlertDialog] to retrieve the [Space] view used
 * for adding space below the Content Panel when Button Panel is NOT present
 * and Content Panel is present. Can be `null` if NOT present.
 */
private fun AlertDialog.getTextSpacerNoButtons(): Space? =
    findViewById(R.id.textSpacerNoButtons)

/**
 * Extension function on [AlertDialog] to retrieve the Button Panel View
 * that contains Buttons for user actions. Can be `null` if the Button Panel is NOT present.
 */
private fun AlertDialog.getButtonPanel(): ScrollView? =
    findViewById(R.id.buttonPanel)

/**
 * Extension function on [AlertDialog] to retrieve the Positive [Button].
 * Can be `null` if this Button is NOT present.
 */
private fun AlertDialog.getButtonPositive(): Button? =
    findViewById(android.R.id.button1)

/**
 * Extension function on [AlertDialog] to retrieve the Negative [Button].
 * Can be `null` if this Button is NOT present.
 */
private fun AlertDialog.getButtonNegative(): Button? =
    findViewById(android.R.id.button2)

/**
 * Extension function on [AlertDialog] to retrieve the Neutral [Button].
 * Can be `null` if this Button is NOT present.
 */
private fun AlertDialog.getButtonNeutral(): Button? =
    findViewById(android.R.id.button3)

/**
 * Extension function on [AlertDialog] to hide the Top Panel that contains a TextView for Title.
 */
fun AlertDialog.hideTitle() {
    getTitleTopPanel()?.apply {
        // Hide the Top Panel if present
        hide()
        getContentPanelForMessage()?.let {
            // If the Content Panel is present and is visible, then as per the out-of-box code
            // show the space that needs to be present above the Content Panel
            // and below the hidden Title Panel
            if (it.isVisible) {
                getTextSpacerNoTitle()?.show()
            }
        }
    }
}

/**
 * Extension function on [AlertDialog] to hide the Content Panel that contains a TextView for Message.
 */
fun AlertDialog.hideMessage() {
    getContentPanelForMessage()?.hide()
}

/**
 * Extension function on [AlertDialog] to hide the Positive [Button].
 */
fun AlertDialog.hideButtonPositive() {
    getButtonPositive()?.hide()
}

/**
 * Extension function on [AlertDialog] to hide the Negative [Button].
 */
fun AlertDialog.hideButtonNegative() {
    getButtonNegative()?.hide()
}

/**
 * Extension function on [AlertDialog] to hide the Neutral [Button].
 */
fun AlertDialog.hideButtonNeutral() {
    getButtonNeutral()?.hide()
}

/**
 * Extension function on [AlertDialog] to hide the Button Panel that contains buttons
 * for user actions.
 */
fun AlertDialog.hideButtonPanel() {
    getButtonPanel()?.apply {
        // Hide the Button Panel if present
        hide()
        getContentPanelForMessage()?.let {
            // If the Content Panel is present and is visible, then as per the out-of-box code
            // show the space that needs to be present below the Content Panel
            // and above the hidden Button Panel
            if (it.isVisible) {
                getTextSpacerNoButtons()?.show()
            }
        }
    }
}