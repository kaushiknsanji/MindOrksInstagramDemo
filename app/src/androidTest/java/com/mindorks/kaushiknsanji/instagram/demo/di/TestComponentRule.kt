package com.mindorks.kaushiknsanji.instagram.demo.di

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