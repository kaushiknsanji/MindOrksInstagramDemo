package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Data class used as a wrapper to the [content] representing the [Event]s emitted by a [LiveData].
 *
 * @param T The type of [content] wrapped in an [Event]
 * @property content The wrapped private data of an [Event]
 * @constructor Creates an Instance of [Event]
 *
 * @author Kaushik N Sanji
 */
data class Event<out T>(private val content: T) {

    //Keeps track of whether the [content] has been consumed or not
    private var isContentConsumed = AtomicBoolean(false)

    /**
     * Returns the [content] when it has not been consumed previously as determined by the state of [isContentConsumed].
     * This prevents the repeated consumption of the content.
     */
    fun getContentIfNotConsumed(): T? = if (isContentConsumed.getAndSet(true)) {
        //Returning Null when the content has already been consumed once
        null
    } else {
        //Returning the Content when it has never been consumed
        content
    }

    /**
     * Returns the [content], whether or not the [content] has been previously consumed.
     */
    fun peekContent(): T = content

}

/**
 * Extension function on the [LiveData] of [Event]s to let observe the [Event] only when its
 * content has never been consumed. When the Event's content was never consumed,
 * the given action [onEventUnconsumedContent] will be executed on the content.
 *
 * @param T The type of data wrapped in an [Event]
 * @param owner The [LifecycleOwner] which controls the observer
 * @param onEventUnconsumedContent The lambda action to be executed on the content [T].
 */
inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnconsumedContent: (T) -> Unit
) {
    observe(owner, Observer { event ->
        event?.getContentIfNotConsumed()?.let(onEventUnconsumedContent)
    })
}

/**
 * Extension function on the [LiveData] of [Resource]s to let observe the [Resource] any time when its
 * content wrapped in [Resource.dataEvent] is needed. This will allow the content to be replayed
 * any number of times, and the given action [onEventAction] will be executed on the content.
 *
 * @param T The type of content wrapped in the [Resource.dataEvent] of the [Resource]. Can be `null`.
 * @param owner The [LifecycleOwner] which controls the observer
 * @param onEventAction The lambda action to be executed on the content [T].
 */
inline fun <T> LiveData<Resource<T>>.observeResource(
    owner: LifecycleOwner,
    crossinline onEventAction: (status: Status, data: T?) -> Unit
) {
    observe(owner, Observer { resourceWrapper: Resource<T> ->
        resourceWrapper.getData()?.let { data: T ->
            // When there is content
            onEventAction(resourceWrapper.status, data)
        } ?: run {
            // When there is no content
            onEventAction(resourceWrapper.status, null)
        }
    })
}

/**
 * Extension function on the [LiveData] of [Resource]s to let observe the [Resource] only when its
 * content wrapped in [Resource.dataEvent] has never been consumed. When the Resource's content was never consumed,
 * the given action [onEventUnconsumedContent] will be executed on the content.
 *
 * @param T The type of content wrapped in the [Resource.dataEvent] of the [Resource]
 * @param owner The [LifecycleOwner] which controls the observer
 * @param onEventUnconsumedContent The lambda action to be executed on the content [T].
 */
inline fun <T> LiveData<Resource<T>>.observeResourceEvent(
    owner: LifecycleOwner,
    crossinline onEventUnconsumedContent: (status: Status, data: T) -> Unit
) {
    observe(owner, Observer { resourceWrapper: Resource<T> ->
        resourceWrapper.dataEvent?.getContentIfNotConsumed()?.let { data: T ->
            onEventUnconsumedContent(resourceWrapper.status, data)
        }
    })
}