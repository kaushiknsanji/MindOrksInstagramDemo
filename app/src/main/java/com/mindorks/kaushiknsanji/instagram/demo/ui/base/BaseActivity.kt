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

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ActivityModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResourceEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.DialogManager
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.Toaster
import javax.inject.Inject

/**
 * An abstract base [AppCompatActivity] for all the Activities in the app, that facilitates
 * setup and provides abstraction to common tasks.
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
        provideContentView()?.let { setContentView(it) } ?: setContentView(provideLayoutId())
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
     * Can be overridden by subclasses to provide the Resource Layout Id for the Activity.
     * If not provided, value of `0` will be returned.
     */
    @LayoutRes
    protected open fun provideLayoutId(): Int = 0

    /**
     * Can be overridden by subclasses to provide the [Root View][View] for the Activity
     * inflated using either `Android ViewBinding or DataBinding`.
     */
    protected open fun provideContentView(): View? = null

    /**
     * To be overridden by subclasses to setup the Layout of the Activity.
     */
    protected abstract fun setupView(savedInstanceState: Bundle?)

}