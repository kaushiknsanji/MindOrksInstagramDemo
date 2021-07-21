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

import com.google.android.material.textfield.TextInputLayout
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status

/**
 * Kotlin file for extension functions on `TextInputLayout`
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [TextInputLayout] that sets the error [errorMessage]
 * when the [status] of the message is [Status.ERROR]
 */
fun TextInputLayout.setErrorStatus(status: Status, errorMessage: String?) {
    when (status) {
        // On Error, set the error on the field
        Status.ERROR -> error = errorMessage
        // When there is no Error, disable the error on the field
        else -> isErrorEnabled = false
    }
}