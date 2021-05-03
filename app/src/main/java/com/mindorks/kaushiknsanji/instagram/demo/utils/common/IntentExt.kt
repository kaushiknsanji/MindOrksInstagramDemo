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