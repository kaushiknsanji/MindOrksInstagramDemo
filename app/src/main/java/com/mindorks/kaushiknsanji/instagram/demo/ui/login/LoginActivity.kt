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

package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityLoginBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_login' to show the Login screen.
 * [LoginViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class LoginActivity : BaseActivity<LoginViewModel>() {

    companion object {
        const val TAG = "LoginActivity"
    }

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityLoginBinding::inflate)

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the [Root View][View] for the Activity
     * inflated using `Android ViewBinding`.
     */
    override fun provideContentView(): View = binding.root

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        with(binding) {
            // Register text change listener on Email field for validations
            editLoginEmail.doOnTextChanged { text, _, _, _ ->
                // When email text changes, delegate to the LoginViewModel
                viewModel.onEmailChange(text.toString())
            }

            // Register text change listener on Password field for validations
            editLoginPassword.doOnTextChanged { text, _, _, _ ->
                // When password text changes, delegate to the LoginViewModel
                viewModel.onPasswordChange(text.toString())
            }

            // Register click listener on Login Button
            buttonLogin.setOnClickListener { viewModel.onLogin() }

            // Register click listener on "Sign Up with Email" Button
            textButtonLoginOptionSignUp.setOnClickListener { viewModel.onSignUpWithEmail() }
        }

    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Email field value to set the new value on change
        viewModel.emailField.observe(this) { emailValue ->
            binding.editLoginEmail.setTextOnChange(emailValue)
        }

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this) { passwordValue ->
            binding.editLoginPassword.setTextOnChange(passwordValue)
        }

        // Register an observer on Email field validation result to show the error if any
        viewModel.emailValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputLoginEmail.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Password field validation result to show the error if any
        viewModel.passwordValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputLoginPassword.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on login request progress to show/hide the Progress Circle
        viewModel.loginProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            binding.progressLogin.showWhen(started)
        }

        // Register an observer for MainActivity launch events
        viewModel.launchMain.observeEvent(this) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish() // Terminate this activity
        }

        // Register an observer for SignUpActivity launch events
        viewModel.launchSignUp.observeEvent(this) {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish() // Terminate this activity
        }

    }

}