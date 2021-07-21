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

package com.mindorks.kaushiknsanji.instagram.demo.di.rule

import android.content.Context
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerTestComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.TestComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationTestModule
import org.junit.rules.ExternalResource

/**
 * [org.junit.rules.TestRule] required for the setup of Dagger for Testing.
 *
 * @property context [Context] of the Target Application being instrumented.
 *
 * @author Kaushik N Sanji
 */
class TestComponentRule(private val context: Context) : ExternalResource() {

    // Dagger [TestComponent] instance for testing
    private var testComponent: TestComponent? = null

    /**
     * Getter for the [Context] of the Target Application being instrumented.
     */
    fun getContext(): Context = context

    /**
     * Getter for the Application [TestComponent].
     */
    fun getComponent(): TestComponent = testComponent!!

    /**
     * Builds the [TestComponent] instance and initializes it
     * as the [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] for Testing.
     */
    private fun setupDaggerTestComponentInApplication() {
        (context.applicationContext as InstagramApplication).run {
            testComponent = DaggerTestComponent.builder()
                .applicationTestModule(ApplicationTestModule(this))
                .build()
            // Set the TestComponent instance as the ApplicationComponent for testing
            setTestComponent(testComponent!!)
        }
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable [after])
     */
    @Throws(Throwable::class)
    override fun before() {
        // Initialize the TestComponent instance as the ApplicationComponent for testing
        setupDaggerTestComponentInApplication()
    }

    /**
     * Override to tear down your specific external resource.
     */
    override fun after() {
        // Clear the TestComponent reference after testing
        testComponent = null
    }

}