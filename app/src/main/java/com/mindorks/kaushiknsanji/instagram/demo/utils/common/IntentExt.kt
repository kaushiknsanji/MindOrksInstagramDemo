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

package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.content.Intent
import java.io.Serializable

/**
 * Kotlin file for extension functions on `Intent`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on [Intent] to load the Intent Extras from [map].
 *
 * @param map The source [Map] containing the Intent Extras to load from.
 * @return Returns the same [Intent] object, for chaining multiple calls
 * into a single statement.
 */
fun Intent.putExtrasFromMap(map: Map<String, Serializable>): Intent =
    this.apply {
        map.forEach { (key: String, value: Serializable) -> putExtra(key, value) }
    }