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

package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Kotlin file for Utility functions on `ViewBinding`.
 *
 * @author Kaushik N Sanji
 */

/**
 * Returns a [Lazy] delegate to access the [ViewBinding] instance of the [AppCompatActivity]
 * whose method reference for inflating its layout is passed to [bindingInflater]. Method reference
 * passed must accept a [LayoutInflater] instance.
 *
 * Usage:
 * ```
 * class MainActivity : AppCompatActivity(){
 * private val binding by viewBinding(ActivityMainBinding::inflate)
 * ...
 * }
 * ```
 * This is executed on the UI Thread without any [thread safety][LazyThreadSafetyMode.NONE]
 * for faster performance.
 *
 * @receiver [AppCompatActivity] instance.
 * @param VB The type of [ViewBinding] instance needed.
 * @param bindingInflater The lambda action for invoking the provided Method reference,
 * passing in the [LayoutInflater] instance derived from the receiver [AppCompatActivity].
 */
@MainThread
inline fun <VB : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> VB
): Lazy<VB> = lazy(LazyThreadSafetyMode.NONE) {
    // Ensuring that this is called on Main Thread
    checkIfNotOnMainThread()
    // Inflate the layout with the inflater and provide the ViewBinding instance
    bindingInflater(layoutInflater)
}

/**
 * Returns a [ViewBinding] instance of the [Fragment] via a [custom delegate][FragmentViewBindingDelegate].
 * Method reference passed to [viewBindingFactory] for binding to its [Root View][View], in order
 * to generate the corresponding [ViewBinding] instance must accept a [View] reference.
 *
 * Usage:
 * ```
 * class HomeFragment : Fragment(){
 * private val binding by viewBinding(FragmentHomeBinding::bind)
 * ...
 * }
 * ```
 * This is executed on the UI Thread and this [property][ViewBinding] should be accessed only
 * after the [Fragment's View has been created][Fragment.onCreateView] and should NOT be accessed
 * after the [Fragment View is destroyed][Fragment.onDestroyView], otherwise it
 * will result in [IllegalStateException].
 *
 * @receiver [Fragment] instance.
 * @param VB The type of [ViewBinding] instance needed.
 * @param viewBindingFactory The lambda action for invoking the provided Method reference, passing
 * in the constructed [View] of the Fragment derived from the receiver [Fragment].
 */
@MainThread
fun <VB : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (View) -> VB
): FragmentViewBindingDelegate<VB> {
    // Ensuring that this is called on Main Thread
    checkIfNotOnMainThread()
    // Construct and return the delegate
    return FragmentViewBindingDelegate(this, viewBindingFactory)
}

/**
 * [ReadOnlyProperty] delegate class for providing the [ViewBinding] instance property
 * of the [Fragment].
 *
 * @param VB The type of [ViewBinding] instance needed.
 * @param fragment [Fragment] whose [Lifecycle] is to be observed for clearing the [ViewBinding]
 * instance output property when it is [destroyed][Fragment.onDestroyView].
 * @property viewBindingFactory The lambda action for invoking the provided Method reference, passing
 * in the constructed [View] of the Fragment derived from the input property [Fragment].
 */
class FragmentViewBindingDelegate<VB : ViewBinding>(
    fragment: Fragment,
    private val viewBindingFactory: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

    // Object to check for initialization of the property and also to clear the same
    internal object UNINITIALIZED_VALUE

    // ViewBinding instance property to be provided
    private var _binding: Any = UNINITIALIZED_VALUE

    init {
        // Observe the LiveData of the Fragment in order to clear the ViewBinding instance property
        // during `onDestroy`
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                /**
                 * Notifies that `ON_DESTROY` event occurred.
                 *
                 * This method will be called before the [LifecycleOwner]'s `onDestroy` method
                 * is called.
                 *
                 * @param owner the component, whose state was changed
                 */
                override fun onDestroy(owner: LifecycleOwner) {
                    // Clearing ViewBinding instance property since the Fragment's View is
                    // being destroyed
                    _binding = UNINITIALIZED_VALUE
                }
            })
        }
    }

    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        // Proceed when the Fragment's Lifecycle state is beyond created, otherwise throw exception
        check(thisRef.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            "Should not attempt to get bindings when $thisRef View is destroyed!"
        }

        // Bind to the Fragment's View when ViewBinding instance property is uninitialized
        if (_binding === UNINITIALIZED_VALUE) {
            _binding = viewBindingFactory(thisRef.requireView())
        }

        // Return the ViewBinding instance property
        @Suppress("UNCHECKED_CAST")
        return _binding as VB
    }
}

/**
 * Returns a [Lazy] delegate to access the Non-null [ViewBinding] instance
 * of [BaseItemViewHolder]. This is executed on the UI Thread
 * without any [thread safety][LazyThreadSafetyMode.NONE] for faster performance.
 *
 * @receiver [BaseItemViewHolder] instance.
 * @param VB The type of [ViewBinding] instance needed.
 * @param viewBindingProvider The lambda action that provides a [ViewBinding] instance of type [VB].
 * This is nullable and will throw an exception when the instance provided is `null`.
 */
@MainThread
inline fun <VB : ViewBinding> BaseItemViewHolder<*, *, *, VB>.viewBinding(
    crossinline viewBindingProvider: () -> VB?
): Lazy<VB> = lazy(LazyThreadSafetyMode.NONE) {
    // Ensuring that this is called on Main Thread
    checkIfNotOnMainThread()

    // Throwing an exception when ViewBinding instance provided is null for some reason.
    // Otherwise, return the Non-null ViewBinding instance
    requireNotNull(viewBindingProvider()) {
        "Provided ViewBinding instance is 'NULL'. Please check if the Factory " +
                "for constructing the ViewBinding instance has been provided " +
                "to the base class of $this or not."
    }
}