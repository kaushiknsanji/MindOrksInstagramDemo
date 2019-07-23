package com.mindorks.kaushiknsanji.instagram.demo.utils.common

/**
 * Class with a [status] metadata on the content wrapped in [dataEvent].
 *
 * @param T the type of content wrapped in the [dataEvent] of this [Resource]
 * @property status [Status] metadata information
 * @property dataEvent Any content of type [T] wrapped in an [Event]. Can be `null`.
 * @constructor A Private Constructor that creates an Instance of [Resource]
 *
 * @author Kaushik N Sanji
 */
data class Resource<out T> private constructor(val status: Status, val dataEvent: Event<T>?) {

    companion object {

        /**
         * Factory method for [Resource] with status [Status.SUCCESS].
         *
         * @param data The content to be wrapped in the [dataEvent] of the [Resource]
         */
        fun <T> success(data: T? = null): Resource<T> = Resource(Status.SUCCESS, data?.let { Event(it) })

        /**
         * Factory method for [Resource] with status [Status.ERROR].
         *
         * @param data The content to be wrapped in the [dataEvent] of the [Resource]
         */
        fun <T> error(data: T? = null): Resource<T> = Resource(Status.ERROR, data?.let { Event(it) })

        /**
         * Factory method for [Resource] with status [Status.LOADING].
         *
         * @param data The content to be wrapped in the [dataEvent] of the [Resource]
         */
        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data?.let { Event(it) })

        /**
         * Factory method for [Resource] with status [Status.UNKNOWN].
         *
         * @param data The content to be wrapped in the [dataEvent] of the [Resource]
         */
        fun <T> unknown(data: T? = null): Resource<T> = Resource(Status.UNKNOWN, data?.let { Event(it) })
    }

    /**
     * Reads and returns the content wrapped in [dataEvent]. Can be `null` if content [T] was `null`.
     */
    fun getData(): T? = dataEvent?.peekContent()

}