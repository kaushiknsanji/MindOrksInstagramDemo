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

/**
 * Interface to be implemented by components that need to expose the methods for the Host to register as Listener.
 *
 * @param L The type of Listeners.
 *
 * @author Kaushik N Sanji
 */
interface ListenerHost<L> {
    /**
     * Registers a [listener] that will be notified of any events.
     *
     * @return `true` if the [listener] has been added, `false` if the [listener] was already registered.
     */
    fun registerListener(listener: L): Boolean

    /**
     * Unregisters a previously registered [listener]. Does nothing if the [listener] was not previously registered.
     *
     * @return `true` if the [listener] has been successfully removed; `false` if the [listener] was not previously registered.
     */
    fun unregisterListener(listener: L): Boolean
}