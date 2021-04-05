package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerDialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.DialogFragmentModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResourceEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.*
import javax.inject.Inject

/**
 * An abstract base [DialogFragment] for all DialogFragments in the app, that facilitates
 * setup and provides abstraction to common tasks.
 *
 * @param VM The type of [BaseDialogViewModel] which will be the Primary ViewModel of the DialogFragment.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseDialogFragment<VM : BaseDialogViewModel> : DialogFragment() {

    // Primary ViewModel instance of the DialogFragment, injected by Dagger
    @Inject
    lateinit var viewModel: VM

    // Instance of Dialog created
    private lateinit var alertDialog: AlertDialog

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
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * [onCreateView] does not need to be implemented since the AlertDialog takes care of its own content.
     *
     * This method will be called after [onCreate] and before [onCreateView]. The
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
        MaterialAlertDialogBuilder(requireContext(), provideTheme()).apply {
            // Construct a Template Dialog with the required elements using builder
            constructTemplateDialog(this, savedInstanceState)
        }.create().apply {
            // Setup the Dialog
            setupDialog(this, savedInstanceState)
            // Save the instance created
            alertDialog = this
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        provideLayoutId().takeIf { it != 0 }?.run {
            inflater.inflate(this, container, false).apply {
                // Read the padding dimensions for Custom View from resources
                val horizontalPadding = resources.getDimensionPixelSize(
                    R.dimen.dialog_custom_view_horizontal_padding
                )
                val verticalPadding = resources.getDimensionPixelSize(
                    R.dimen.dialog_custom_view_vertical_padding
                )
                // Set the inflated view as Dialog's Custom View with the above padding
                alertDialog.setView(
                    this,
                    horizontalPadding,
                    verticalPadding,
                    horizontalPadding,
                    verticalPadding
                )
            }
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

        // Register an observer for the Dialog Title-id LiveData
        viewModel.titleDialogId.observeResource(this) { _, titleResId: Int? ->
            // Set the new Dialog Title
            titleResId?.let { setDialogTitle(getString(it)) }
                ?: alertDialog.hideTitle() // Hide the Title when null
        }

        // Register an observer for the Dialog Message-id LiveData
        viewModel.messageDialogId.observeResource(this) { _, messageResId: Int? ->
            // Set the new Dialog Message
            messageResId?.let { setDialogMessage(getString(it)) }
                ?: alertDialog.hideMessage() // Hide the Message when null
        }

        // Register an observer for the Dialog's Positive Button Text-id LiveData
        viewModel.positiveButtonTextId.observeResource(this) { _, nameId: Int? ->
            // Set the new Name for Positive Button
            nameId?.let { setDialogButtonName(getString(it), DialogInterface.BUTTON_POSITIVE) }
                ?: alertDialog.hideButtonPositive() // Hide Positive Button when null
        }

        // Register an observer for the Dialog's Negative Button Text-id LiveData
        viewModel.negativeButtonTextId.observeResource(this) { _, nameId: Int? ->
            // Set the new Name for Negative Button
            nameId?.let { setDialogButtonName(getString(it), DialogInterface.BUTTON_NEGATIVE) }
                ?: alertDialog.hideButtonNegative() // Hide Negative Button when null
        }

        // Register an observer for the Dialog's Neutral Button Text-id LiveData
        viewModel.neutralButtonTextId.observeResource(this) { _, nameId: Int? ->
            // Set the new Name for Neutral Button
            nameId?.let { setDialogButtonName(getString(it), DialogInterface.BUTTON_NEUTRAL) }
                ?: alertDialog.hideButtonNeutral() // Hide Neutral Button when null
        }

        // Register an observer on the LiveData that detects if any Buttons are initialized
        // in order to hide the Button Panel when none of them are initialized
        viewModel.hasAnyButtons.observe(this) { hasButtons: Boolean ->
            hasButtons.takeUnless { it }?.run { alertDialog.hideButtonPanel() }
        }
    }

    /**
     * Updates the Button Name to the new [name] if the required [whichButton] is set.
     *
     * @param whichButton The identifier of the button like [DialogInterface.BUTTON_POSITIVE].
     */
    private fun setDialogButtonName(name: String, whichButton: Int) {
        alertDialog.getButton(whichButton)?.text = name
    }

    /**
     * Updates the Dialog message to the new [message], only when a Custom View is not being used for the Dialog.
     */
    private fun setDialogMessage(message: String) {
        if (!hasCustomView()) {
            alertDialog.setMessage(message)
        }
    }

    /**
     * Updates the Dialog title to the new [title], only when a Custom View is not being used for the Dialog.
     */
    private fun setDialogTitle(title: String) {
        if (!hasCustomView()) {
            alertDialog.setTitle(title)
        }
    }

    /**
     * Checks whether a Custom View is being used for the Dialog.
     *
     * @return Returns `true` if a Custom View is used; `false` otherwise
     */
    private fun hasCustomView(): Boolean = (provideLayoutId() != 0)

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
     * Can be overridden by subclasses to provide the Resource Layout Id for the DialogFragment.
     * If not provided, value of `0` will be returned.
     */
    @LayoutRes
    protected open fun provideLayoutId(): Int = 0

    /**
     * Can be overridden by subclasses to construct a Template [Dialog] with the
     * required elements using [dialogBuilder], since we can only hide the elements
     * that are initialized.
     */
    @CallSuper
    protected open fun constructTemplateDialog(
        dialogBuilder: MaterialAlertDialogBuilder,
        savedInstanceState: Bundle?
    ) {
    }

    /**
     * Can be overridden by subclasses to setup the [Dialog] of the DialogFragment.
     */
    @CallSuper
    protected open fun setupDialog(dialog: AlertDialog, savedInstanceState: Bundle?) {
        // Fragment will always be shown as a Dialog
        showsDialog = true
        // Setup the Style of the Dialog
        setStyle(provideDialogStyle(), theme)
    }

    /**
     * Can be overridden by subclasses to setup the Layout of the DialogFragment
     * when the Layout Id of the Custom View has been provided through [provideLayoutId].
     */
    @CallSuper
    protected open fun setupView(view: View, savedInstanceState: Bundle?) {
    }

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