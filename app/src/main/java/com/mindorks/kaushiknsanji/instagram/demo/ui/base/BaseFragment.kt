package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.FragmentModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResourceEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract base [Fragment] for all Fragments in the app, that facilitates
 * setup and abstraction to common tasks.
 *
 * @param VM The type of [BaseViewModel] which will be the Primary ViewModel of the Fragment.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    // Primary ViewModel instance of the Fragment, injected by Dagger
    @Inject
    lateinit var viewModel: VM

    // DialogManager instance provided by Dagger
    @Inject
    lateinit var dialogManager: DialogManager

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject the Fragment's dependencies first
        injectDependencies(buildFragmentComponent())
        // Call to super
        super.onCreate(savedInstanceState)
        // Setup any LiveData observers
        setupObservers()
        // Invoke BaseViewModel's "onCreate()" method
        viewModel.onCreate()
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(provideLayoutId(), container, false)

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup the Fragment view
        setupView(view, savedInstanceState)
        // Initialize DialogManager
        dialogManager.setup()
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    protected open fun setupObservers() {
        // Register an observer for message LiveData
        viewModel.messageString.observeResourceEvent(this) { _, message: String ->
            // Show the message when the event occurs
            showMessage(message)
        }

        // Register an observer for message-id LiveData
        viewModel.messageStringId.observeResourceEvent(this) { _, messageResId: Int ->
            // Show the message when the event occurs
            showMessage(messageResId)
        }
    }

    /**
     * Displays a [android.widget.Toast] for the [message] string.
     */
    fun showMessage(message: String) = context?.let { context -> Toaster.show(context, message) }

    /**
     * Displays a [android.widget.Toast] for the message Resource id [messageResId].
     */
    fun showMessage(@StringRes messageResId: Int) = showMessage(getString(messageResId))

    /**
     * Handles "Back" key event by delegating to its Activity that subclasses [BaseActivity]
     */
    fun goBack() {
        if (activity is BaseActivity<*>) (activity as BaseActivity<*>).goBack()
    }

    /**
     * Builds and provides the Dagger Component for Fragment, i.e., [FragmentComponent]
     */
    private fun buildFragmentComponent(): FragmentComponent =
        DaggerFragmentComponent.builder()
            .applicationComponent((context!!.applicationContext as InstagramApplication).applicationComponent)
            .fragmentModule(FragmentModule(this))
            .build()

    /**
     * To be overridden by subclasses to inject dependencies exposed by [FragmentComponent] into Fragment.
     */
    protected abstract fun injectDependencies(fragmentComponent: FragmentComponent)

    /**
     * To be overridden by subclasses to provide the Resource Layout Id for the Fragment.
     */
    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    /**
     * To be overridden by subclasses to setup the Layout of the Fragment.
     */
    protected abstract fun setupView(view: View, savedInstanceState: Bundle?)

}