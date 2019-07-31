package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
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
        edit_login_email.addTextChangedListener(object : TextWatcher {
            /**
             * This method is called to notify you that, somewhere within
             * [s], the text has been changed.
             * It is legitimate to make further changes to [s] from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [android.text.Spannable.setSpan] in [onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(s: Editable?) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * are about to be replaced by new text with length [after].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * have just replaced old text that had length [before].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When the email text changes, delegate to the LoginViewModel
                viewModel.onEmailChange(s.toString())
            }

        })

        // Register text change listener on Password field for validations
        edit_login_password.addTextChangedListener(object : TextWatcher {
            /**
             * This method is called to notify you that, somewhere within
             * [s], the text has been changed.
             * It is legitimate to make further changes to [s] from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [android.text.Spannable.setSpan] in [onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(s: Editable?) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * are about to be replaced by new text with length [after].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //No-op
            }

            /**
             * This method is called to notify you that, within [s],
             * the [count] characters beginning at [start]
             * have just replaced old text that had length [before].
             * It is an error to attempt to make changes to [s] from
             * this callback.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When the password text changes, delegate to the LoginViewModel
                viewModel.onPasswordChange(s.toString())
            }

        })

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
        viewModel.emailField.observe(this, Observer { emailValue ->
            edit_login_email.setTextOnChange(emailValue)
        })

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this, Observer { passwordValue ->
            edit_login_password.setTextOnChange(passwordValue)
        })

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
        viewModel.loginProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_login.setVisibility(started)
        })

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