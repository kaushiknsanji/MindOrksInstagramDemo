package com.mindorks.kaushiknsanji.instagram.demo.utils.network

import android.content.Context

/**
 * Fake [NetworkHelper] implementation for testing Network related tasks.
 *
 * @property context The Application [Context] instance
 * @constructor Creates an Instance of [FakeNetworkHelperImpl]
 *
 * @author Kaushik N Sanji
 */
class FakeNetworkHelperImpl(private val context: Context) :
    NetworkHelper by NetworkHelperImpl(context) {

    /**
     * Checks for Network connectivity.
     *
     * @return `true` if the Network is established; `false` otherwise.
     * Defaulted to `true` always for testing.
     */
    override fun isNetworkConnected(): Boolean = true
}