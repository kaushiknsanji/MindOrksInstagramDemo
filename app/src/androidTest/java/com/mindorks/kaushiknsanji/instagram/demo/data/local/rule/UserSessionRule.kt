package com.mindorks.kaushiknsanji.instagram.demo.data.local.rule

import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.di.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import org.junit.rules.ExternalResource

/**
 * [org.junit.rules.TestRule] required for the setup of Signed-in User (in session) for testing.
 * Depends on Dagger [componentRule] for its initialization. Hence should be applied after
 * the [componentRule] in the [org.junit.rules.RuleChain] setup.
 *
 * @property componentRule The [org.junit.rules.TestRule] of Dagger setup.
 *
 * @author Kaushik N Sanji
 */
class UserSessionRule(private val componentRule: TestComponentRule) : ExternalResource() {

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable [after])
     */
    override fun before() {
        // Have a dummy User signed-in for testing

        // Get the UserRepository to save active user's information
        val userRepository = componentRule.getComponent().getUserRepository()

        // Get User "User_10" and save it as the signed-in user, via the UserRepository
        userRepository.saveCurrentUser(signedInUser)
    }

    /**
     * Override to tear down your specific external resource.
     */
    override fun after() {
        // Remove the signed-in User information from UserRepository
        componentRule.getComponent().getUserRepository().removeCurrentTestUser()
    }

    companion object {

        // Default Signed in User "User_10" for testing
        val signedInUser: User = DataModelObjectProvider.signedInUser

    }

}