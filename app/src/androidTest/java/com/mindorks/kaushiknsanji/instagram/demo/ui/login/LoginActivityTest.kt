package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.TestComponentRule
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
     * [ActivityScenarioRule] for LoginActivity Intents setup.
     */
    private val loginActivityIntentRule =
        ActivityScenarioRule<LoginActivity>(Intent(appContext, LoginActivity::class.java))

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules to be applied in the
     * order of [componentRule], followed by [loginActivityIntentRule]
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)
        .around(loginActivityIntentRule)

    @Before
    fun setUp() {
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
    }

}