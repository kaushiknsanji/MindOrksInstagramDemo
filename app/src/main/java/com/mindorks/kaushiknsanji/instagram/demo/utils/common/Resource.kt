package com.mindorks.kaushiknsanji.instagram.demo.utils.common

/**
 * Class with a [status] metadata on the content [data] wrapped.
 *
 * @param T the type of [data] in this [Resource]
 * @property status [Status] metadata information
 * @property data Any content of type [T]. Can be null.
 * @constructor A Private Constructor that creates an Instance of [Resource]
 *
 * @author Kaushik N Sanji
 */
data class Resource<out T> private constructor(val status: Status, val data: T?) {

    companion object {

        /**
         * Factory method for [Resource] with status [Status.SUCCESS].
         *
         * @param data The content to be wrapped in the [Resource]
         */
        fun <T> success(data: T? = null): Resource<T> = Resource(Status.SUCCESS, data)

        /**
         * Factory method for [Resource] with status [Status.ERROR].
         *
         * @param data The content to be wrapped in the [Resource]
         */
        fun <T> error(data: T? = null): Resource<T> = Resource(Status.ERROR, data)

        /**
         * Factory method for [Resource] with status [Status.LOADING].
         *
         * @param data The content to be wrapped in the [Resource]
         */
        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data)

        /**
         * Factory method for [Resource] with status [Status.UNKNOWN].
         *
         * @param data The content to be wrapped in the [Resource]
         */
        fun <T> unknown(data: T? = null): Resource<T> = Resource(Status.UNKNOWN, data)
    }

}