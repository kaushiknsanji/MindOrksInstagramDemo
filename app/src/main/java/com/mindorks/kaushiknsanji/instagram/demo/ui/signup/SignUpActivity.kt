package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
import kotlinx.android.synthetic.main.activity_sign_up.*

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

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_sign_up

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Register text change listener on Email field for validations
        edit_sign_up_email.doOnTextChanged { text, _, _, _ ->
            // When email text changes, delegate to the SignUpViewModel
            viewModel.onEmailChange(text.toString())
        }

        // Register text change listener on Password field for validations
        edit_sign_up_password.doOnTextChanged { text, _, _, _ ->
            // When password text changes, delegate to the SignUpViewModel
            viewModel.onPasswordChange(text.toString())
        }

        // Register text change listener on Name field for validations
        edit_sign_up_name.doOnTextChanged { text, _, _, _ ->
            // When name text changes, delegate to the SignUpViewModel
            viewModel.onNameChange(text.toString())
        }

        // Register click listener on "Sign Up" Button
        button_sign_up.setOnClickListener { viewModel.onSignUp() }

        // Register click listener on "Login with Email" Button
        text_button_sign_up_option_login.setOnClickListener { viewModel.onLoginWithEmail() }

    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Email field value to set the new value on change
        viewModel.emailField.observe(this) { emailValue ->
            edit_sign_up_email.setTextOnChange(emailValue)
        }

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this) { passwordValue ->
            edit_sign_up_password.setTextOnChange(passwordValue)
        }

        // Register an observer on Name field value to set the new value on change
        viewModel.nameField.observe(this) { nameValue ->
            edit_sign_up_name.setTextOnChange(nameValue)
        }

        // Register an observer on Email field validation result to show the error if any
        viewModel.emailValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_sign_up_email.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Password field validation result to show the error if any
        viewModel.passwordValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_sign_up_password.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Name field validation result to show the error if any
        viewModel.nameValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_sign_up_name.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Sign-Up request progress to show/hide the Progress Circle
        viewModel.signUpProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_sign_up.setVisibility(started)
        })

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