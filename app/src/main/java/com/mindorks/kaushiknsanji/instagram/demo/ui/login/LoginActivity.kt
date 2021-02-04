package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
import kotlinx.android.synthetic.main.activity_login.*

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

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_login

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Register text change listener on Email field for validations
        edit_login_email.doOnTextChanged { text, _, _, _ ->
            // When email text changes, delegate to the LoginViewModel
            viewModel.onEmailChange(text.toString())
        }

        // Register text change listener on Password field for validations
        edit_login_password.doOnTextChanged { text, _, _, _ ->
            // When password text changes, delegate to the LoginViewModel
            viewModel.onPasswordChange(text.toString())
        }

        // Register click listener on Login Button
        button_login.setOnClickListener { viewModel.onLogin() }

        // Register click listener on "Sign Up with Email" Button
        text_button_login_option_sign_up.setOnClickListener { viewModel.onSignUpWithEmail() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Email field value to set the new value on change
        viewModel.emailField.observe(this) { emailValue ->
            edit_login_email.setTextOnChange(emailValue)
        }

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this) { passwordValue ->
            edit_login_password.setTextOnChange(passwordValue)
        }

        // Register an observer on Email field validation result to show the error if any
        viewModel.emailValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_login_email.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on Password field validation result to show the error if any
        viewModel.passwordValidation.observeResource(this) { status: Status, messageResId: Int? ->
            text_input_login_password.setErrorStatus(
                status,
                messageResId?.run { getString(this) }
            )
        }

        // Register an observer on login request progress to show/hide the Progress Circle
        viewModel.loginProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_login.showWhen(started)
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