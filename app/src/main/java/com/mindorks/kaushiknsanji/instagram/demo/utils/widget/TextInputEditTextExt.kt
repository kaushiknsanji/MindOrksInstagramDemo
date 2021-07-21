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

package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import com.google.android.material.textfield.TextInputEditText

/**
 * Kotlin file for extension functions on `TextInputEditText`
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [TextInputEditText] to set the [value] only when it is different from its current value.
 */
fun TextInputEditText.setTextOnChange(value: String) {
    if (text.toString() != value) {
        // When changed, set the new text to the field
        setText(value)
    }
}