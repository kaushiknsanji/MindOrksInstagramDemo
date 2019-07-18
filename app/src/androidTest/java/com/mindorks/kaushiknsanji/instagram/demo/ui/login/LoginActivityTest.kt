package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
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

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val component = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    /**
     * [IntentsTestRule] for Activity Intents setup.
     */
    private val main = IntentsTestRule(
        LoginActivity::class.java,
        false,
        /** Disable Touch on launch */
        false
        /** Do not launch the Activity yet */
    )

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules to be applied in the
     * order of [component], followed by [main]
     */
    @get:Rule
    val ruleChain = RuleChain.outerRule(component)
        .around(main)

    @Before
    fun setUp() {
        // No-op
    }

    @Test
    fun testCheckViewsDisplay() {
        // Launch the LoginActivity to begin the test
        main.launchActivity(Intent(component.getContext(), LoginActivity::class.java))

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