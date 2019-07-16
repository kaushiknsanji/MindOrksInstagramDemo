package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ActivityModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract base [AppCompatActivity] for all the Activities in the app, that facilitates
 * setup and abstraction to common tasks.
 *
 * @param VM The type of [BaseViewModel] which will be the Primary ViewModel of the Activity.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    // Primary ViewModel instance of the Activity, injected by Dagger
    @Inject
    lateinit var viewModel: VM

    // DialogManager instance provided by Dagger
    @Inject
    lateinit var dialogManager: DialogManager

    /**
     * Called when the activity is starting.  This is where most initialization should be done.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [.onSaveInstanceState].
     *     <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject the Activity's dependencies first
        injectDependencies(buildActivityComponent())
        // Call to super
        super.onCreate(savedInstanceState)
        // Set the Activity's layout
        setContentView(provideLayoutId())
        // Setup any LiveData observers
        setupObservers()
        // Setup the Activity view
        setupView(savedInstanceState)
        // Initialize DialogManager
        dialogManager.setup()
        // Invoke BaseViewModel's "onCreate()" method
        viewModel.onCreate()
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    protected open fun setupObservers() {
        // Register an observer for message LiveData
        viewModel.messageString.observe(this, Observer { resourceWrapper ->
            resourceWrapper.data?.run {
                // Show the message when the event occurs
                showMessage(this)
            }
        })

        // Register an observer for message-id LiveData
        viewModel.messageStringId.observe(this, Observer { resourceWrapper ->
            resourceWrapper.data?.run {
                // Show the message when the event occurs
                showMessage(this)
            }
        })
    }

    /**
     * Displays a [android.widget.Toast] for the [message] string.
     */
    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    /**
     * Displays a [android.widget.Toast] for the message Resource id [messageResId].
     */
    fun showMessage(@StringRes messageResId: Int) = showMessage(getString(messageResId))

    /**
     * Can be overridden by subclasses to provide implementation for "Back" key event.
     * Called by Fragments to delegate the "back" key event to their Activity.
     */
    open fun goBack() = onBackPressed()

    /**
     * Takes care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Builds and provides the Dagger Component for Activity, i.e., [ActivityComponent]
     */
    private fun buildActivityComponent(): ActivityComponent =
        DaggerActivityComponent.builder()
            .applicationComponent((application as InstagramApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    /**
     * To be overridden by subclasses to inject dependencies exposed by [ActivityComponent] into Activity.
     */
    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    /**
     * To be overridden by subclasses to provide the Resource Layout Id for the Activity.
     */
    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    /**
     * To be overridden by subclasses to setup the Layout of the Activity.
     */
    protected abstract fun setupView(savedInstanceState: Bundle?)

}