package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setErrorStatus
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setTextOnChange
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
        edit_sign_up_email.addTextChangedListener(object : TextWatcher {
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
                // When the email text changes, delegate to the SignUpViewModel
                viewModel.onEmailChange(s.toString())
            }

        })

        // Register text change listener on Password field for validations
        edit_sign_up_password.addTextChangedListener(object : TextWatcher {
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
                // When the password text changes, delegate to the SignUpViewModel
                viewModel.onPasswordChange(s.toString())
            }

        })

        // Register text change listener on Name field for validations
        edit_sign_up_name.addTextChangedListener(object : TextWatcher {
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
                // When the name text changes, delegate to the SignUpViewModel
                viewModel.onNameChange(s.toString())
            }

        })

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
        viewModel.emailField.observe(this, Observer { emailValue ->
            edit_sign_up_email.setTextOnChange(emailValue)
        })

        // Register an observer on Password field value to set the new value on change
        viewModel.passwordField.observe(this, Observer { passwordValue ->
            edit_sign_up_password.setTextOnChange(passwordValue)
        })

        // Register an observer on Name field value to set the new value on change
        viewModel.nameField.observe(this, Observer { nameValue ->
            edit_sign_up_name.setTextOnChange(nameValue)
        })

        // Register an observer on Email field validation result to show the error if any
        viewModel.emailValidation.observe(this, Observer { resourceWrapper: Resource<Int> ->
            text_input_sign_up_email.setErrorStatus(
                resourceWrapper.status,
                resourceWrapper.data?.run { getString(this) }
            )
        })

        // Register an observer on Password field validation result to show the error if any
        viewModel.passwordValidation.observe(this, Observer { resourceWrapper: Resource<Int> ->
            text_input_sign_up_password.setErrorStatus(
                resourceWrapper.status,
                resourceWrapper.data?.run { getString(this) }
            )
        })

        // Register an observer on Name field validation result to show the error if any
        viewModel.nameValidation.observe(this, Observer { resourceWrapper: Resource<Int> ->
            text_input_sign_up_name.setErrorStatus(
                resourceWrapper.status,
                resourceWrapper.data?.run { getString(this) }
            )
        })

        // Register an observer on Sign-Up request progress to show/hide the Progress Circle
        viewModel.signUpProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_sign_up.visibility = if (started) {
                View.VISIBLE
            } else {
                View.GONE
            }
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