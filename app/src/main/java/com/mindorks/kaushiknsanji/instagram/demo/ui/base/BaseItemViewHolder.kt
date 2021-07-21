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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ViewHolderModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResourceEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract Base class for the [RecyclerView.ViewHolder] that implements [LifecycleOwner]
 * to be Lifecycle aware. Facilitates setup and provides abstraction to common tasks.
 *
 * @param T The type of ItemView's data.
 * @param L The type of Listeners that extends [BaseAdapter.DefaultListener].
 * @param VM The type of ItemView's ViewModel that extends [BaseItemViewModel]
 * @param VB The type of ItemView's ViewBinding that extends [ViewBinding]. Can be [ViewBinding]
 * if not intending to use ViewBinding for layout inflation.
 * @param itemLayoutId [Int] value of the layout resource Id of the ItemView.
 * @param container [ViewGroup] that contains the ItemViews.
 * @property listener Instance of listeners of type [L] to receive Navigation events. Defaulted to `null`.
 * @param layoutInflater [LayoutInflater] instance built using the parameters provided,
 * for inflating the layout [itemLayoutId].
 * @param viewBindingFactory Factory Method reference for inflating the layout [itemLayoutId] using ViewBinding.
 * Can be `null` if not intending to use ViewBinding for layout inflation.
 * @param viewBinding [ViewBinding] instance of type [VB] constructed when [viewBindingFactory]
 * is provided. Will be `null` if [viewBindingFactory] is NOT provided.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseItemViewHolder<T : Any, L : BaseAdapter.DefaultListener<T>, VM : BaseItemViewModel<T>, VB : ViewBinding>(
    @LayoutRes itemLayoutId: Int,
    container: ViewGroup,
    protected val listener: L? = null,
    layoutInflater: LayoutInflater = LayoutInflater.from(container.context),
    viewBindingFactory: ((inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> VB)? = null,
    viewBinding: VB? = viewBindingFactory?.run { this(layoutInflater, container, false) }
) : RecyclerView.ViewHolder(
    viewBinding?.root ?: layoutInflater.inflate(itemLayoutId, container, false)
), LifecycleOwner {

    // For the ItemView's ViewModel
    @Inject
    lateinit var itemViewModel: VM

    // For the Lifecycle
    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

    // ViewBinding instance for this ItemView
    protected val itemViewBinding by viewBinding { viewBinding }

    init {
        // Mark the Lifecycle state as Created when ViewHolder is initialized
        onCreate()
    }

    /**
     * Returns the [Lifecycle] of the provider.
     */
    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    /**
     * Called when the ViewHolder is created. Moves the Lifecycle State to Initialized and Created.
     * Also, takes care of injecting dependencies, registering LiveData observers and setting up the ItemView's Layout.
     */
    fun onCreate() {
        // Inject the required dependencies first
        injectDependencies(buildViewHolderComponent())
        // Move the Lifecycle state to Initialized and Created
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        // Setup any LiveData observers
        setupObservers()
        // Setup the ItemView Layout
        setupView(itemView)
    }

    /**
     * Called when the Item ViewHolders become visible.
     * Moves the Lifecycle State to Started and Resumed.
     */
    fun onStart() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    /**
     * Called when the Item ViewHolders go into the background.
     * Moves the Lifecycle State from Resumed to Started and Created.
     */
    fun onStop() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    /**
     * Called when the RecyclerView is detached from Window, or when the Activity/Fragment is destroyed.
     * Moves the Lifecycle State to Destroyed, and does the cleanup required.
     */
    fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        // Do the cleanup of the Item's ViewModel
        itemViewModel.onManualCleared()
    }

    /**
     * Binds the ItemView with its [itemData] by delegating to the [itemViewModel]'s LiveData.
     * Can be overridden by subclasses to provide their own logic while binding.
     */
    open fun bind(itemData: T) {
        itemViewModel.updateItemData(itemData)
    }

    /**
     * Builds and provides the Dagger Component for [RecyclerView.ViewHolder], i.e., [ViewHolderComponent]
     */
    private fun buildViewHolderComponent(): ViewHolderComponent =
        DaggerViewHolderComponent.builder()
            .applicationComponent((itemView.context.applicationContext as InstagramApplication).applicationComponent)
            .viewHolderModule(ViewHolderModule(this))
            .build()

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    protected open fun setupObservers() {
        // Register an observer for message LiveData
        itemViewModel.messageString.observeResourceEvent(this) { _, message: String ->
            // Show the message when the event occurs
            showMessage(message)
        }

        // Register an observer for message-id LiveData
        itemViewModel.messageStringId.observeResourceEvent(this) { _, messageResId: Int ->
            // Show the message when the event occurs
            showMessage(messageResId)
        }

    }

    /**
     * Displays a [android.widget.Toast] for the [message] string.
     */
    fun showMessage(message: String) = Toaster.show(itemView.context, message)

    /**
     * Displays a [android.widget.Toast] for the message Resource id [messageResId].
     */
    fun showMessage(@StringRes messageResId: Int) = showMessage(itemView.context.getString(messageResId))

    /**
     * To be overridden by subclasses to setup the Layout of the [itemView].
     */
    protected abstract fun setupView(itemView: View)

    /**
     * To be overridden by subclasses to inject dependencies exposed by [ViewHolderComponent] into [RecyclerView.ViewHolder].
     */
    protected abstract fun injectDependencies(viewHolderComponent: ViewHolderComponent)

}