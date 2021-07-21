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
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.rule.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher.TextInputLayoutMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * Instrumented Test on [LoginActivity].
 *
 * @author Kaushik N Sanji
 */
class LoginActivityTest {

    // Get the Application Context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val componentRule = TestComponentRule(appContext)

    /**
     * [ActivityScenarioRule] for [LoginActivity] setup, which launches LoginActivity
     * for every test method.
     */
    private val loginActivityScenarioRule =
        ActivityScenarioRule<LoginActivity>(Intent(appContext, LoginActivity::class.java))

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule], followed by [loginActivityScenarioRule].
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)
        .around(loginActivityScenarioRule)

    @Before
    fun setUp() {
        // No-op
    }

    @After
    fun tearDown() {
        // No-op
    }

    @Test
    fun testCheckViewsDisplay() {
        // Check if the Email Field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_email))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the Password Field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the Login Button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the "Sign Up with Email" Button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_button_login_option_sign_up))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenValidCredentials_whenLogin_shouldLaunchMainActivity() {
        // Create User "User_10" for Login testing
        val user10 = DataModelObjectProvider.signedInUser

        // Type in the email of the user in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_email))
            .perform(ViewActions.typeText(user10.email), ViewActions.closeSoftKeyboard())

        // Type in the email hash of the user in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_password))
            .perform(
                ViewActions.typeText(user10.email.hashCode().toString()),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Login Button to start the Login process
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .perform(ViewActions.click())

        // At this moment, MainActivity should have launched.
        // Assert it by checking if the bottom navigation view is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenInvalidEmail_whenLogin_shouldDisplayInvalidEmailError() {
        // Invalid Email to be entered
        val invalidEmail = "abc"
        // Expected error shown for invalid email entered
        val expectedInvalidEmailError =
            appContext.resources.getString(R.string.error_login_sign_up_email_field_invalid)

        // Type in the invalid email in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_email))
            .perform(ViewActions.typeText(invalidEmail), ViewActions.closeSoftKeyboard())

        // Type in the email hash in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_password))
            .perform(
                ViewActions.typeText(invalidEmail.hashCode().toString()),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Login Button to start the Login validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of email should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_email))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedInvalidEmailError
                    )
                )
            )
    }

    @Test
    fun givenInvalidPassword_whenLogin_shouldDisplayInvalidPasswordError() {
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"
        // Expected error shown for invalid password entered
        val expectedInvalidPasswordError =
            appContext.resources.getString(R.string.error_login_sign_up_password_field_small_length)

        // Type in the valid email in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_email))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        // Type in the invalid password in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_password))
            .perform(
                ViewActions.typeText(password),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Login Button to start the Login validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of password should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_password))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedInvalidPasswordError
                    )
                )
            )
    }

    @Test
    fun givenAllEmptyFields_whenLogin_shouldDisplayEmptyFieldsError() {
        // Expected error shown for empty email entered
        val expectedEmptyEmailError =
            appContext.resources.getString(R.string.error_login_sign_up_email_field_empty)
        // Expected error shown for empty password entered
        val expectedEmptyPasswordError =
            appContext.resources.getString(R.string.error_login_sign_up_password_field_empty)

        // Ensure the TextInputEditText field for email is empty
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_email))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        // Ensure the TextInputEditText field for password is empty
        Espresso.onView(ViewMatchers.withId(R.id.edit_login_password))
            .perform(
                ViewActions.typeText(""),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Login Button to start the Login validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of email should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_email))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedEmptyEmailError
                    )
                )
            )

        // Also, the TextInputLayout of password should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_password))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedEmptyPasswordError
                    )
                )
            )
    }

    @Test
    fun whenSignUpWithEmail_shouldLaunchSignUpActivity() {
        // Click on the "Sign Up with Email" Button to launch the SignUpActivity
        Espresso.onView(ViewMatchers.withId(R.id.text_button_login_option_sign_up))
            .perform(ViewActions.click())

        // At this moment, SignUpActivity should have launched.
        // Assert it by checking if the Text Input field for user name is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}