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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

/**
 * Kotlin file for extension functions on `LiveData`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Extension function on the [LiveData] of [Event]s to let observe the [Event] only when its
 * content has never been consumed. When the Event's content was never consumed,
 * the given action [onEventUnconsumedContent] will be executed on the content.
 *
 * @param T The type of data wrapped in an [Event].
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param onEventUnconsumedContent The lambda action to be executed on the content [T].
 */
inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnconsumedContent: (data: T) -> Unit
) {
    observe(owner) { event: Event<T>? ->
        event?.getContentIfNotConsumed()?.let(onEventUnconsumedContent)
    }
}

/**
 * Extension function on the [LiveData] of [Resource]s to let observe the [Resource] any time when its
 * content wrapped in [Resource.dataEvent] is needed. This will allow the content to be replayed
 * any number of times, and the given action [onEventAction] will be executed on the content.
 *
 * @param T The type of content wrapped in the [Resource.dataEvent] of the [Resource]. Can be `null`.
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param onEventAction The lambda action to be executed on the content [T] based on [Resource.status].
 */
inline fun <T> LiveData<Resource<T>>.observeResource(
    owner: LifecycleOwner,
    crossinline onEventAction: (status: Status, data: T?) -> Unit
) {
    observe(owner) { resourceWrapper: Resource<T> ->
        onEventAction(resourceWrapper.status, resourceWrapper.peekData())
    }
}

/**
 * Extension function on the [LiveData] of [Resource]s to let observe the [Resource] only when its
 * content wrapped in [Resource.dataEvent] has never been consumed. When the Resource's content was never consumed,
 * the given action [onEventUnconsumedContent] will be executed on the content.
 *
 * @param T The type of content wrapped in the [Resource.dataEvent] of the [Resource].
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param onEventUnconsumedContent The lambda action to be executed on the content [T] based on [Resource.status].
 */
inline fun <T> LiveData<Resource<T>>.observeResourceEvent(
    owner: LifecycleOwner,
    crossinline onEventUnconsumedContent: (status: Status, data: T) -> Unit
) {
    observe(owner) { resourceWrapper: Resource<T> ->
        resourceWrapper.dataEvent?.getContentIfNotConsumed()?.let { data: T ->
            onEventUnconsumedContent(resourceWrapper.status, data)
        }
    }
}

/**
 * Extension function on [LiveData] of any Nullable content of type [T] that is NOT
 * wrapped in any other types like [Resource] or [Event]. When observed, the given [action]
 * will be executed on the content, only when the content is `NOT NULL`.
 *
 * @param T The type of content that is NOT wrapped in any other types. Can be `null`.
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param action The lambda action to be executed on the content [T] when it is `NOT NULL`.
 */
inline fun <T> LiveData<T?>.observeNonNull(
    owner: LifecycleOwner,
    crossinline action: (data: T) -> Unit
) {
    observe(owner) {
        it?.let(action)
    }
}