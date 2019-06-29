package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ViewHolderModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract Base class for the [RecyclerView.ViewHolder] that implements [LifecycleOwner]
 * to be Lifecycle aware. Provides abstraction for common tasks and setup.
 *
 * @param T The type of ItemView's data.
 * @param VM The type of ItemView's ViewModel that extends [BaseItemViewModel]
 * @param itemLayoutId [Int] value of the layout resource Id of the ItemView.
 * @param container [ViewGroup] that contains the ItemViews.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseItemViewHolder<T : Any, VM : BaseItemViewModel<T>>(
    @LayoutRes itemLayoutId: Int, container: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(container.context).inflate(itemLayoutId, container, false)),
    LifecycleOwner {

    init {
        // Mark the Lifecycle state as Created when ViewHolder is initialized
        onCreate()
    }

    // For the ItemView's ViewModel
    @Inject
    lateinit var itemViewModel: VM

    // For the Lifecycle
    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

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
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
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
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    /**
     * Called when the Item ViewHolders go into the background.
     * Moves the Lifecycle State from Resumed to Started and Created.
     */
    fun onStop() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    /**
     * Called when the RecyclerView is detached from Window, or when the Activity/Fragment is destroyed.
     * Moves the Lifecycle State to Destroyed, and does the cleanup required.
     */
    fun onDestroy() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
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
        itemViewModel.messageString.observe(this, Observer { resourceWrapper ->
            resourceWrapper.data?.run {
                // Show the message when the event occurs
                showMessage(this)
            }
        })

        // Register an observer for message-id LiveData
        itemViewModel.messageStringId.observe(this, Observer { resourceWrapper ->
            resourceWrapper.data?.run {
                // Show the message when the event occurs
                showMessage(this)
            }
        })

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