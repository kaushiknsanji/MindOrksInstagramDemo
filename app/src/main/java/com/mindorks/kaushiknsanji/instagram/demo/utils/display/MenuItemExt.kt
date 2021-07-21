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

import android.view.MenuItem

/**
 * Kotlin file for extension functions on android `MenuItem`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [MenuItem] to make the [MenuItem] visible.
 */
fun MenuItem.show() {
    isVisible = true
}

/**
 * Extension function on [MenuItem] to hide the [MenuItem].
 */
fun MenuItem.hide() {
    isVisible = false
}

/**
 * Extension function on [MenuItem] to enable the [MenuItem].
 */
fun MenuItem.enable() {
    isEnabled = true
}

/**
 * Extension function on [MenuItem] to disable the [MenuItem].
 */
fun MenuItem.disable() {
    isEnabled = false
}

/**
 * Extension function on [MenuItem] to make the [MenuItem] visible and enabled.
 */
fun MenuItem.showAndEnable() {
    show()
    enable()
}

/**
 * Extension function on [MenuItem] to make the [MenuItem] hidden and disabled.
 */
fun MenuItem.hideAndDisable() {
    hide()
    disable()
}

/**
 * Extension function on [MenuItem] to "show and enable" or "hide and disable" the [MenuItem]
 * based on the provided [visibility condition][visibilityCondition].
 * When [visibilityCondition] evaluates to `true`, then this [MenuItem] will be
 * shown and enabled; otherwise hidden and disabled.
 */
fun MenuItem.showAndEnableWhen(visibilityCondition: Boolean) {
    if (visibilityCondition) {
        showAndEnable()
    } else {
        hideAndDisable()
    }
}