package com.mindorks.kaushiknsanji.instagram.demo.utils.test.activity

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat

/**
 * [ActivityResultRegistry] class to stub the result of Activity launched, for testing.
 *
 * @property expectedResult [Any] object that represents the expected result of the
 * activity launched for result.
 *
 * @constructor Creates an instance of [ActivityResultRegistry] for test.
 *
 * @author Kaushik N Sanji
 */
class TestActivityResultRegistry(
    private val expectedResult: Any
) : ActivityResultRegistry() {

    // Flag to keep track of successful launch of the requested activity
    var isActivityLaunched: Boolean = false

    /**
     * Start the process of executing an [ActivityResultContract] in a type-safe way,
     * using the provided [contract][ActivityResultContract].
     *
     * @param requestCode request code to use
     * @param contract contract to use for type conversions
     * @param input input required to execute an ActivityResultContract.
     * @param options Additional options for how the Activity should be started.
     */
    override fun <I : Any?, O : Any?> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?
    ) {
        // Dispatch the provided [expectedResult] immediately and save the launch boolean
        isActivityLaunched = dispatchResult(requestCode, expectedResult)
    }

}