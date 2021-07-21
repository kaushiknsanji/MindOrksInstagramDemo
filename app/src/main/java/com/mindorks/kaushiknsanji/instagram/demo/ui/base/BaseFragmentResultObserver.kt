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

package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.activity.result.ActivityResultRegistry
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * An abstract base [DefaultLifecycleObserver] that facilitates the setup of Lifecycle observers
 * needed for receiving and handling the activity result in a separate class for the calls being
 * registered via [registry].
 *
 * @param T The type of [BaseFragment] registering this Lifecycle observer for activity results.
 * @property registry [ActivityResultRegistry] instance to register the calls and store their callbacks.
 *
 * @constructor Creates an instance of [DefaultLifecycleObserver] for registering calls, storing
 * and handling their callbacks in a separate class.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseFragmentResultObserver<T : BaseFragment<out BaseViewModel>>(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    /**
     * Notifies that `ON_CREATE` event occurred. This method will be called
     * after the [LifecycleOwner]'s `onCreate` method returns.
     *
     * This is the place where we register our [androidx.activity.result.ActivityResultLauncher]s.
     *
     * @param owner the component, whose state was changed.
     */
    override fun onCreate(owner: LifecycleOwner) {
        // Register all Activity Result Launchers
        @Suppress("UNCHECKED_CAST")
        initResultLaunchers(registry, owner, owner as T)
    }

    /**
     * Called after the [LifecycleOwner]'s `onCreate` method returns.
     *
     * This method should register all the required
     * [androidx.activity.result.ActivityResultLauncher]s for the Fragment.
     *
     * @param registry [ActivityResultRegistry] that stores all the
     * [androidx.activity.result.ActivityResultCallback]s for all the registered calls.
     * @param owner [LifecycleOwner] of the Fragment that makes the call.
     * @param fragment A Fragment instance of type [BaseFragment] derived from [owner].
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun initResultLaunchers(
        registry: ActivityResultRegistry,
        owner: LifecycleOwner,
        fragment: T
    )

    /**
     * A unique [String] key generator that identifies the calls being registered in [registry].
     *
     * @param tag [String] identifier of the subclass.
     * @param requestCode Unique [Int] value of the request.
     * @return Returns a unique key [String] identifying this call.
     */
    protected fun reqKeyGenerator(tag: String, requestCode: Int) =
        "${tag}_rq#$requestCode"

}