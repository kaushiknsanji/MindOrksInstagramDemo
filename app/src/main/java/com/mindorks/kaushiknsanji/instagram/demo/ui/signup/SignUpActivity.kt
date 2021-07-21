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

package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivitySignUpBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_sign_up' to show the Sign-Up screen.
 * [SignUpViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class SignUpActivity : BaseActivity<SignUpViewModel>() {

    companion object {
        const val TAG = "SignUpActivity"
    }

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivitySignUpBinding::inflate)

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
            editSignUpEmail.doOnTextChanged { text, _, _, _ ->
                // When email text changes, delegate to the SignUpViewModel
                viewModel.onEmailChange(text.toString())
            }

            // Register text change listener on Password field for validations
            editSignUpPassword.doOnTextChanged { text, _, _, _ ->
                // When password text changes, delegate to the SignUpViewModel
                viewModel.onPasswordChange(text.toString())
            }

            // Register text change listener on Name field for validations
            editSignUpName.doOnTextChanged { text, _, _, _ ->
                // When name text changes, delegate to the SignUpViewModel
                viewModel.onNameChange(text.toString())
            }

            // Register click listener on "Sign Up" Button
            buttonSignUp.setOnClickListener { viewModel.onSignUp() }

            // Register click listener on "Login with Email" Button
            textButtonSignUpOptionLogin.setOnClickListener { viewModel.onLoginWithEmail() }
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
            binding.editSignUpEmail.setTextOnChange(emailValue)
        }

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this) { passwordValue ->
            binding.editSignUpPassword.setTextOnChange(passwordValue)
        }

        // Register an observer on Name field value to set the new value on change
        viewModel.nameField.observe(this) { nameValue ->
            binding.editSignUpName.setTextOnChange(nameValue)
        }

        // Register an observer on Email field validation result to show the error if any
        viewModel.emailValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputSignUpEmail.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Password field validation result to show the error if any
        viewModel.passwordValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputSignUpPassword.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Name field validation result to show the error if any
        viewModel.nameValidation.observeResource(this) { status: Status, messageResId: Int? ->
            binding.textInputSignUpName.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Sign-Up request progress to show/hide the Progress Circle
        viewModel.signUpProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            binding.progressSignUp.showWhen(started)
        }

        // Register an observer for MainActivity launch events
        viewModel.launchMain.observeEvent(this) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish() // Terminate this activity
        }

        // Register an observer for LoginActivity launch events
        viewModel.launchLogin.observeEvent(this) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish() // Terminate this activity
        }

    }
}