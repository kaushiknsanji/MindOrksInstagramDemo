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

package com.mindorks.kaushiknsanji.instagram.demo.ui.splash

import android.content.Intent
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.rule.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * Instrumented Test on [SplashActivity].
 *
 * @author Kaushik N Sanji
 */
class SplashActivityTest {

    // Get the Application Context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val componentRule = TestComponentRule(appContext)

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule].
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)

    /**
     * Method to manually launch [SplashActivity] for every test method.
     *
     * @return [ActivityScenario] for [SplashActivity]
     */
    private fun launchSplashActivity(): ActivityScenario<SplashActivity> =
        ActivityScenario.launch(Intent(appContext, SplashActivity::class.java))

    /**
     * Returns [UserRepository] instance obtained via [componentRule].
     */
    private fun getUserRepository(): UserRepository =
        componentRule.getComponent().getUserRepository()

    /**
     * Removes any active [com.mindorks.kaushiknsanji.instagram.demo.data.model.User] information
     * from [UserRepository].
     */
    private fun startWithoutUser() {
        // Get the UserRepository and clear any user information if any
        getUserRepository().removeCurrentTestUser()
    }

    /**
     * Loads a [DataModelObjectProvider.signedInUser] into the [UserRepository] as an
     * active user who is already registered.
     */
    private fun startWithUser() {
        // Get User "User_10" and save it as the signed-in user, via the UserRepository
        getUserRepository().saveCurrentUser(DataModelObjectProvider.signedInUser)
    }

    @Test
    fun testCheckViewsDisplay() {
        // Launch the SplashActivity
        launchSplashActivity()

        // Check if the Splash logo is displayed
        Espresso.onView(ViewMatchers.withId(R.id.image_splash_logo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenNoUser_shouldLaunchLoginActivity() {
        // Clear any user information if any, so that LoginActivity gets launched after a second
        startWithoutUser()

        // Launch the SplashActivity
        launchSplashActivity()

        // Wait for a second in order to allow the layout to load completely
        SystemClock.sleep(1000)

        // At this moment, LoginActivity should have launched since there is no user logged-in.
        // Assert it by checking if the "Sign Up with Email" Button is displayed or not.
        Espresso.onView(ViewMatchers.withId(R.id.text_button_login_option_sign_up))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenRegisteredUser_shouldLaunchMainActivity() {
        // Have a dummy User signed-in, so that MainActivity gets launched after a second
        startWithUser()

        // Launch the SplashActivity
        launchSplashActivity()

        // Wait for a second in order to allow the layout to load completely
        SystemClock.sleep(1000)

        // At this moment, MainActivity should have launched since we have an
        // active user already logged-in.
        // Assert it by checking if the bottom navigation view is displayed or not.
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}