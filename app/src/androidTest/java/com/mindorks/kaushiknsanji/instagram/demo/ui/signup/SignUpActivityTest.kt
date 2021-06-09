package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher.TextInputLayoutMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * Instrumented Test on [SignUpActivity].
 *
 * @author Kaushik N Sanji
 */
class SignUpActivityTest {

    // Get the Application Context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val componentRule = TestComponentRule(appContext)

    /**
     * [ActivityScenarioRule] for [SignUpActivity] setup, which launches SignUpActivity
     * for every test method.
     */
    private val signUpActivityScenarioRule =
        ActivityScenarioRule<SignUpActivity>(Intent(appContext, SignUpActivity::class.java))

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule], followed by [signUpActivityScenarioRule].
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)
        .around(signUpActivityScenarioRule)

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
        // Check if the Name Field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the Email Field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_email))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the Password Field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the "Sign Up" Button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.button_sign_up))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the "Login with Email" Button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.text_button_sign_up_option_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenValidCredentials_whenSignUp_shouldLaunchMainActivity() {
        // Create User "User_10" for Sign Up testing
        val user10 = DataModelObjectProvider.signedInUser

        // Type in the name of the user in the TextInputEditText field of name
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_name))
            .perform(ViewActions.typeText(user10.name), ViewActions.closeSoftKeyboard())

        // Type in the email of the user in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_email))
            .perform(ViewActions.typeText(user10.email), ViewActions.closeSoftKeyboard())

        // Type in the email hash of the user in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_password))
            .perform(
                ViewActions.typeText(user10.email.hashCode().toString()),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Sign Up Button to start the Sign Up process
        Espresso.onView(ViewMatchers.withId(R.id.button_sign_up))
            .perform(ViewActions.click())

        // At this moment, MainActivity should have launched.
        // Assert it by checking if the bottom navigation view is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenInvalidEmail_whenSignUp_shouldDisplayInvalidEmailError() {
        // Name of the user to be entered
        val name = "test"
        // Invalid Email to be entered
        val invalidEmail = "abc"
        // Expected error shown for invalid email entered
        val expectedInvalidEmailError =
            appContext.resources.getString(R.string.error_login_sign_up_email_field_invalid)

        // Type in the name in the TextInputEditText field of name
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_name))
            .perform(ViewActions.typeText(name), ViewActions.closeSoftKeyboard())

        // Type in the invalid email in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_email))
            .perform(ViewActions.typeText(invalidEmail), ViewActions.closeSoftKeyboard())

        // Type in the email hash in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_password))
            .perform(
                ViewActions.typeText(invalidEmail.hashCode().toString()),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Sign Up Button to start the Sign Up validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_sign_up))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of email should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_email))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedInvalidEmailError
                    )
                )
            )
    }

    @Test
    fun givenInvalidPassword_whenSignUp_shouldDisplayInvalidPasswordError() {
        // Name of the user to be entered
        val name = "test"
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"
        // Expected error shown for invalid password entered
        val expectedInvalidPasswordError =
            appContext.resources.getString(R.string.error_login_sign_up_password_field_small_length)

        // Type in the name in the TextInputEditText field of name
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_name))
            .perform(ViewActions.typeText(name), ViewActions.closeSoftKeyboard())

        // Type in the valid email in the TextInputEditText field of email
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_email))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        // Type in the invalid password in the TextInputEditText field of password
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_password))
            .perform(
                ViewActions.typeText(password),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Sign Up Button to start the Sign Up validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_sign_up))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of password should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_password))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedInvalidPasswordError
                    )
                )
            )
    }

    @Test
    fun givenAllEmptyFields_whenSignUp_shouldDisplayEmptyFieldsError() {
        // Expected error shown for empty name entered
        val expectedEmptyNameError =
            appContext.resources.getString(R.string.error_sign_up_name_field_empty)
        // Expected error shown for empty email entered
        val expectedEmptyEmailError =
            appContext.resources.getString(R.string.error_login_sign_up_email_field_empty)
        // Expected error shown for empty password entered
        val expectedEmptyPasswordError =
            appContext.resources.getString(R.string.error_login_sign_up_password_field_empty)

        // Ensure the TextInputEditText field for name is empty
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_name))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        // Ensure the TextInputEditText field for email is empty
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_email))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        // Ensure the TextInputEditText field for password is empty
        Espresso.onView(ViewMatchers.withId(R.id.edit_sign_up_password))
            .perform(
                ViewActions.typeText(""),
                ViewActions.closeSoftKeyboard()
            )

        // Click on the Sign Up Button to start the Sign Up validation process
        Espresso.onView(ViewMatchers.withId(R.id.button_sign_up))
            .perform(ViewActions.click())

        // At this moment, the TextInputLayout of name should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_name))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedEmptyNameError
                    )
                )
            )

        // Also, the TextInputLayout of email should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_email))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedEmptyEmailError
                    )
                )
            )

        // Also, the TextInputLayout of password should display the expected error
        Espresso.onView(ViewMatchers.withId(R.id.text_input_sign_up_password))
            .check(
                ViewAssertions.matches(
                    TextInputLayoutMatchers.hasErrorText(
                        expectedEmptyPasswordError
                    )
                )
            )
    }

    @Test
    fun whenLoginWithEmail_shouldLaunchLoginActivity() {
        // Click on the "Login with Email" Button to launch the LoginActivity
        Espresso.onView(ViewMatchers.withId(R.id.text_button_sign_up_option_login))
            .perform(ViewActions.click())

        // At this moment, LoginActivity should have launched.
        // Assert it by checking if the "Sign Up with Email" Button is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.text_button_login_option_sign_up))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}