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