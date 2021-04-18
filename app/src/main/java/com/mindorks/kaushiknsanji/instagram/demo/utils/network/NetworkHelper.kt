package com.mindorks.kaushiknsanji.instagram.demo.utils.network

/**
 * Interface for Network related tasks.
 *
 * @author Kaushik N Sanji
 */
interface NetworkHelper {

    /**
     * Checks for Network connectivity.
     *
     * @return `true` if the Network is established; `false` otherwise.
     */
    fun isNetworkConnected(): Boolean

    /**
     * Generates a [NetworkError] instance appropriately based on the [throwable]
     */
    fun castToNetworkError(throwable: Throwable): NetworkError

}