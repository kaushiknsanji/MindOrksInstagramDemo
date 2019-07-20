package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerDialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.DialogFragmentModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResourceEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract base [DialogFragment] for all DialogFragments in the app, that facilitates
 * setup and abstraction to common tasks.
 *
 * @param VM The type of [BaseViewModel] which will be the Primary ViewModel of the DialogFragment.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseDialogFragment<VM : BaseViewModel> : DialogFragment() {

    // Primary ViewModel instance of the DialogFragment, injected by Dagger
    @Inject
    lateinit var viewModel: VM

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject the Fragment's dependencies first
        injectDependencies(buildDialogFragmentComponent())
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
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * [onCreateView] does not need to be implemented since the AlertDialog takes care of its own content.
     *
     * This method will be called after [onCreate] and before [onCreateView].  The
     * default implementation simply instantiates and returns a [Dialog]
     * class.
     *
     * *Note: DialogFragment own the [Dialog.setOnCancelListener] and [Dialog.setOnDismissListener] callbacks.
     * You must not set them yourself.*
     * To find out about these events, override [.onCancel] and [.onDismiss].
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext(), provideTheme()).apply {
            // Fragment will always be shown as a Dialog
            showsDialog = true
            // Setup the Style of the Dialog
            setStyle(provideDialogStyle(), theme)
            // Setup the Dialog
            setupDialog(this, savedInstanceState)
        }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state
     * has been restored in to the view. This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup the DialogFragment view
        setupView(view, savedInstanceState)
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
     * Builds and provides the Dagger Component for DialogFragment, i.e., [DialogFragmentComponent]
     */
    private fun buildDialogFragmentComponent(): DialogFragmentComponent =
        DaggerDialogFragmentComponent.builder()
            .applicationComponent((context!!.applicationContext as InstagramApplication).applicationComponent)
            .dialogFragmentModule(DialogFragmentModule(this))
            .build()

    /**
     * To be overridden by subclasses to inject dependencies exposed by [DialogFragmentComponent] into DialogFragment.
     */
    protected abstract fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent)

    /**
     * To be overridden by subclasses to provide the Resource Layout Id for the DialogFragment.
     */
    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    /**
     * To be overridden by subclasses to setup the [Dialog] of the DialogFragment.
     */
    protected abstract fun setupDialog(dialog: Dialog, savedInstanceState: Bundle?)

    /**
     * To be overridden by subclasses to setup the Layout of the DialogFragment.
     */
    protected abstract fun setupView(view: View, savedInstanceState: Bundle?)

    /**
     * Can be overridden to provide the style resource describing the theme to be used for the [Dialog].
     * If not provided, value of `0` will be used as the default [Dialog] theme.
     */
    @StyleRes
    protected open fun provideTheme(): Int = 0

    /**
     * Can be overridden to customize the basic appearance and behavior of the
     * fragment's dialog by providing one of these style values [androidx.fragment.app.DialogFragment.STYLE_NORMAL],
     * [androidx.fragment.app.DialogFragment.STYLE_NO_TITLE], [androidx.fragment.app.DialogFragment.STYLE_NO_FRAME], or
     * [androidx.fragment.app.DialogFragment.STYLE_NO_INPUT]. [androidx.fragment.app.DialogFragment.STYLE_NORMAL] will
     * be the Default Style used.
     */
    protected open fun provideDialogStyle(): Int = STYLE_NORMAL
}