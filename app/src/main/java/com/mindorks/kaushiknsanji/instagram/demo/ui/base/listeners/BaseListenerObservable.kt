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

package com.mindorks.kaushiknsanji.instagram.demo.ui.base.listeners

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Base class for Listener entities in the application.
 *
 * @param L The type of Listeners.
 *
 * @author Kaushik N Sanji
 */
open class BaseListenerObservable<L> : ListenerHost<L> {

    // Thread-safe set of Listeners
    private val listeners: MutableSet<L> = Collections.newSetFromMap(ConcurrentHashMap<L, Boolean>(1))

    /**
     * Registers a [listener] that will be notified of any events.
     *
     * @return `true` if the [listener] has been added, `false` if the [listener] was already registered.
     */
    override fun registerListener(listener: L): Boolean = listeners.add(listener)

    /**
     * Unregisters a previously registered [listener]. Does nothing if the [listener] was not previously registered.
     *
     * @return `true` if the [listener] has been successfully removed; `false` if the [listener] was not previously registered.
     */
    override fun unregisterListener(listener: L): Boolean = listeners.remove(listener)

    /**
     * Returns a read-only reference to the [Set] containing all the registered [listeners].
     */
    fun getListeners(): Set<L> = Collections.unmodifiableSet(listeners)
}