package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.os.Looper

/**
 * Kotlin file for Utility functions on `Threads` and `Loopers`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Checks if the current Thread is MAIN Thread or not. Throws an exception if not on MAIN Thread.
 */
fun checkIfNotOnMainThread() {
    val myLooper = Looper.myLooper()
    check(myLooper != null && myLooper == Looper.getMainLooper()) {
        "ERROR: Cannot be called from other threads. Needs to be called on the MAIN thread only."
    }
}