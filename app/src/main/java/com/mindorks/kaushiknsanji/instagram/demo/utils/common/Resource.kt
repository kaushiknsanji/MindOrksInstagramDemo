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

/**
 * Sealed Class used as Statuses with content wrapped in [dataEvent].
 * The content that is wrapped in [dataEvent], can also at times reference to Android/App Resource Ids.
 * In certain cases, hardcoded Strings can also be passed. Hence the name [Resource].
 *
 * @param T the type of content wrapped in the [dataEvent] of this [Resource]
 * @property status [Status] metadata information
 * @property dataEvent Any content of type [T] wrapped in an [Event]. Can be `null`.
 * @constructor A Private Constructor that creates an Instance of [Resource]
 *
 * @author Kaushik N Sanji
 */
sealed class Resource<out T>(val status: Status, val dataEvent: Event<T>?) {

    /**
     * Data Class for [Status.SUCCESS] Status
     *
     * @property data The content to be wrapped in the [dataEvent] of the [Resource]
     * @constructor Creates a factory constructor for [Status.SUCCESS] Status
     */
    data class Success<out T>(private val data: T? = null) :
        Resource<T>(Status.SUCCESS, data?.let { Event(it) })

    /**
     * Data Class for [Status.ERROR] Status
     *
     * @property data The content to be wrapped in the [dataEvent] of the [Resource]
     * @constructor Creates a factory constructor for [Status.ERROR] Status
     */
    data class Error<out T>(private val data: T? = null) :
        Resource<T>(Status.ERROR, data?.let { Event(it) })

    /**
     * Data Class for [Status.LOADING] Status
     *
     * @property data The content to be wrapped in the [dataEvent] of the [Resource]
     * @constructor Creates a factory constructor for [Status.LOADING] Status
     */
    data class Loading<out T>(private val data: T? = null) :
        Resource<T>(Status.LOADING, data?.let { Event(it) })

    /**
     * Data Class for [Status.UNKNOWN] Status
     *
     * @property data The content to be wrapped in the [dataEvent] of the [Resource]
     * @constructor Creates a factory constructor for [Status.UNKNOWN] Status
     */
    data class Unknown<out T>(private val data: T? = null) :
        Resource<T>(Status.UNKNOWN, data?.let { Event(it) })

    /**
     * Reads and returns the content wrapped in [dataEvent]. Can be `null` if content [T] was `null`.
     */
    fun peekData(): T? = dataEvent?.peekContent()
}