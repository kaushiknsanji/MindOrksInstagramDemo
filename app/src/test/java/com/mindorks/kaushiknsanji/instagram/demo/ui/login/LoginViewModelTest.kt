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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Local Unit Test on [LoginViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    /**
     * Getter for [org.junit.rules.TestRule] that returns [InstantTaskExecutorRule].
     * [InstantTaskExecutorRule] ensures that the `set` and `post` actions on [androidx.lifecycle.LiveData]
     * happens on the same Thread synchronously, for the Testing purpose.
     */
    @get:Rule
    val rule = InstantTaskExecutorRule()

    // NetworkHelper instance
    @Mock
    private lateinit var networkHelper: NetworkHelper

    // UserRepository instance
    @Mock
    private lateinit var userRepository: UserRepository

    // LiveData Observer for Resource messages
    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

    // LiveData Observer for Login progress
    @Mock
    private lateinit var loginProgressObserver: Observer<Boolean>

    // LiveData Observer for MainActivity launch events
    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for SignUpActivity launch events
    @Mock
    private lateinit var launchSignUpObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for Email validation results
    @Mock
    private lateinit var emailValidationObserver: Observer<Resource<Int>>

    // LiveData Observer for Password validation results
    @Mock
    private lateinit var passwordValidationObserver: Observer<Resource<Int>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        // Create the LoginViewModel
        loginViewModel = LoginViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository
        )

        // Register the LiveData Observers forever
        loginViewModel.messageStringId.observeForever(messageStringIdObserver)
        loginViewModel.loginProgress.observeForever(loginProgressObserver)
        loginViewModel.launchMain.observeForever(launchMainObserver)
        loginViewModel.launchSignUp.observeForever(launchSignUpObserver)
        loginViewModel.emailValidation.observeForever(emailValidationObserver)
        loginViewModel.passwordValidation.observeForever(passwordValidationObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        loginViewModel.messageStringId.removeObserver(messageStringIdObserver)
        loginViewModel.loginProgress.removeObserver(loginProgressObserver)
        loginViewModel.launchMain.removeObserver(launchMainObserver)
        loginViewModel.launchSignUp.removeObserver(launchSignUpObserver)
        loginViewModel.emailValidation.removeObserver(emailValidationObserver)
        loginViewModel.passwordValidation.removeObserver(passwordValidationObserver)
    }

    @Test
    fun givenServerResponse200_whenLogin_shouldLaunchMainActivity() {
        // Create Dummy User
        val user = DataModelObjectProvider.signedInUser
        // Password for the User (derived from email hash for testing)
        val password = user.email.hashCode().toString()

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = user.email
        loginViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Connected
        doReturn(true).`when`(networkHelper).isNetworkConnected()

        // Mock the UserRepository Login Action to provide the SingleSource of existing User
        doReturn(Single.just(user))
            .`when`(userRepository)
            .doUserLogin(user.email, password)

        // Start the Login
        loginViewModel.onLogin()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Resource for Email validation results LiveData
        val expectedEmailValidationResource = Resource.Success<Int>()
        // Assert that the Email validation results LiveData is set to the expected Resource
        assert(loginViewModel.emailValidation.value == expectedEmailValidationResource)
        // Verify that the Email validation results LiveData Observer has received the expected Resource
        verify(emailValidationObserver).onChanged(expectedEmailValidationResource)

        // Expected Resource for Password validation results LiveData
        val expectedPasswordValidationResource = Resource.Success<Int>()
        // Assert that the Password validation results LiveData is set to the expected Resource
        assert(loginViewModel.passwordValidation.value == expectedPasswordValidationResource)
        // Verify that the Password validation results LiveData Observer has received the expected Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordValidationResource)

        // Verify that there was a call to save the user information in the preferences
        verify(userRepository).saveCurrentUser(user)
        // Assert that the Login Progress indication LiveData has stopped
        assert(loginViewModel.loginProgress.value == false)
        // Verify that the Login Progress LiveData Observer received both Start and Stop values
        verify(loginProgressObserver).onChanged(true)
        verify(loginProgressObserver).onChanged(false)

        // Expected Event for MainActivity
        val expectedMainEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the MainActivity Launch Event LiveData is triggered
        assert(loginViewModel.launchMain.value == expectedMainEvent)
        // Verify that the MainActivity Launch Event LiveData Observer received the expected Event
        verify(launchMainObserver).onChanged(expectedMainEvent)
    }

    @Test
    fun givenInvalidEmail_whenLogin_shouldDispatchEmailInvalidError() {
        // Invalid Email
        val invalidEmail = "abcdefg"
        // Valid password derived from email hash for testing
        val password = invalidEmail.hashCode().toString()

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = invalidEmail
        loginViewModel.passwordField.value = password

        // Start the Login
        loginViewModel.onLogin()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Error Resource for Invalid Email
        val expectedEmailErrorResource =
            Resource.Error(R.string.error_login_sign_up_email_field_invalid)
        // Assert that the Email validation results LiveData is set to the expected Invalid Error Resource
        assert(loginViewModel.emailValidation.value == expectedEmailErrorResource)
        // Verify that the Email validation results LiveData Observer has received the expected Invalid Error Resource
        verify(emailValidationObserver).onChanged(expectedEmailErrorResource)

        // Expected Resource for Password validation results LiveData
        val expectedPasswordValidationResource = Resource.Success<Int>()
        // Assert that the Password validation results LiveData is set to the expected Resource
        assert(loginViewModel.passwordValidation.value == expectedPasswordValidationResource)
        // Verify that the Password validation results LiveData Observer has received the expected Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordValidationResource)

        // Verify that there was no interactions with the UserRepository
        verifyNoInteractions(userRepository)
        // Verify that the Login Progress LiveData Observer never received any value
        verifyNoInteractions(loginProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenInvalidPassword_whenLogin_shouldDispatchPasswordInvalidError() {
        // Valid Email
        val email = "test@mindorks.com"
        // Invalid Password (less than 6 min-character-length)
        val invalidPassword = "abc"

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = email
        loginViewModel.passwordField.value = invalidPassword

        // Start the Login
        loginViewModel.onLogin()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Resource for Email validation results LiveData
        val expectedEmailValidationResource = Resource.Success<Int>()
        // Assert that the Email validation results LiveData is set to the expected Resource
        assert(loginViewModel.emailValidation.value == expectedEmailValidationResource)
        // Verify that the Email validation results LiveData Observer has received the expected Resource
        verify(emailValidationObserver).onChanged(expectedEmailValidationResource)

        // Expected Error Resource for Invalid Password
        val expectedPasswordErrorResource =
            Resource.Error(R.string.error_login_sign_up_password_field_small_length)
        // Assert that the Password validation results LiveData is set to the expected Invalid Error Resource
        assert(loginViewModel.passwordValidation.value == expectedPasswordErrorResource)
        // Verify that the Password validation results LiveData Observer has received the expected Invalid Error Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordErrorResource)

        // Verify that there was no interactions with the UserRepository
        verifyNoInteractions(userRepository)
        // Verify that the Login Progress LiveData Observer never received any value
        verifyNoInteractions(loginProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenAllEmptyFields_whenLogin_shouldDispatchEmptyFieldsError() {
        // Set empty values for the Field LiveData
        loginViewModel.emailField.value = ""
        loginViewModel.passwordField.value = ""

        // Start the Login
        loginViewModel.onLogin()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Error Resource for Empty Email
        val expectedEmailErrorResource =
            Resource.Error(R.string.error_login_sign_up_email_field_empty)
        // Assert that the Email validation results LiveData is set to the expected Error Resource
        assert(loginViewModel.emailValidation.value == expectedEmailErrorResource)
        // Verify that the Email validation results LiveData Observer has received the expected Error Resource
        verify(emailValidationObserver).onChanged(expectedEmailErrorResource)

        // Expected Error Resource for Empty Password
        val expectedPasswordErrorResource =
            Resource.Error(R.string.error_login_sign_up_password_field_empty)
        // Assert that the Password validation results LiveData is set to the expected Error Resource
        assert(loginViewModel.passwordValidation.value == expectedPasswordErrorResource)
        // Verify that the Password validation results LiveData Observer has received the expected Error Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordErrorResource)

        // Verify that there was no interactions with the UserRepository
        verifyNoInteractions(userRepository)
        // Verify that the Login Progress LiveData Observer never received any value
        verifyNoInteractions(loginProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenNoInternet_whenLogin_shouldShowNetworkError() {
        // Create Dummy User
        val user = DataModelObjectProvider.signedInUser
        // Password for the User (derived from email hash for testing)
        val password = user.email.hashCode().toString()

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = user.email
        loginViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Disconnected
        doReturn(false).`when`(networkHelper).isNetworkConnected()

        // Start the Login
        loginViewModel.onLogin()

        // Expected Network Error Resource
        val expectedNetworkErrorMessage = Resource.Error(R.string.error_network_connection_issue)
        // Assert that expected Network Error is set on the Resource messages LiveData
        assert(loginViewModel.messageStringId.value == expectedNetworkErrorMessage)
        // Verify that the Resource messages LiveData Observer received the expected Network Error Resource
        verify(messageStringIdObserver).onChanged(expectedNetworkErrorMessage)
    }

    @Test
    fun whenSignUpWithEmail_shouldLaunchSignUpActivity() {
        // Initiate the "Sign Up with Email" event
        loginViewModel.onSignUpWithEmail()

        // Expected Event for SignUpActivity
        val expectedSignUpEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the SignUpActivity Launch Event LiveData is triggered
        assert(loginViewModel.launchSignUp.value == expectedSignUpEvent)
        // Verify that the SignUpActivity Launch Event LiveData Observer received the expected Event
        verify(launchSignUpObserver).onChanged(expectedSignUpEvent)
    }

}