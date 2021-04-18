package com.mindorks.kaushiknsanji.instagram.demo.ui.login

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
     * [ActivityScenarioRule] for [LoginActivity] Intents setup, which launches LoginActivity
     * for every test method.
     */
    private val loginActivityIntentRule =
        ActivityScenarioRule<LoginActivity>(Intent(appContext, LoginActivity::class.java))

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule], followed by [loginActivityIntentRule]
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)
        .around(loginActivityIntentRule)

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
        // Check if the Login Field is displayed
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
        val user10 = DataModelObjectProvider.createUser("10")

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