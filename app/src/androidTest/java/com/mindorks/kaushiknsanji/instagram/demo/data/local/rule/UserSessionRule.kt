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

package com.mindorks.kaushiknsanji.instagram.demo.data.local.rule

import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.di.rule.TestComponentRule
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