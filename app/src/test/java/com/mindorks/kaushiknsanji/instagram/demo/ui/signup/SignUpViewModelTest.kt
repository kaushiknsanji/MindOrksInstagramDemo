package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Local Unit Test on [SignUpViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest {

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

    // LiveData Observer for SignUp progress
    @Mock
    private lateinit var signUpProgressObserver: Observer<Boolean>

    // LiveData Observer for MainActivity launch events
    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for LoginActivity launch events
    @Mock
    private lateinit var launchLoginObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for Name validation results
    @Mock
    private lateinit var nameValidationObserver: Observer<Resource<Int>>

    // LiveData Observer for Email validation results
    @Mock
    private lateinit var emailValidationObserver: Observer<Resource<Int>>

    // LiveData Observer for Password validation results
    @Mock
    private lateinit var passwordValidationObserver: Observer<Resource<Int>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        // Create the SignUpViewModel
        signUpViewModel = SignUpViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository
        )

        // Register the LiveData Observers forever
        signUpViewModel.messageStringId.observeForever(messageStringIdObserver)
        signUpViewModel.signUpProgress.observeForever(signUpProgressObserver)
        signUpViewModel.launchMain.observeForever(launchMainObserver)
        signUpViewModel.launchLogin.observeForever(launchLoginObserver)
        signUpViewModel.nameValidation.observeForever(nameValidationObserver)
        signUpViewModel.emailValidation.observeForever(emailValidationObserver)
        signUpViewModel.passwordValidation.observeForever(passwordValidationObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        signUpViewModel.messageStringId.removeObserver(messageStringIdObserver)
        signUpViewModel.signUpProgress.removeObserver(signUpProgressObserver)
        signUpViewModel.launchMain.removeObserver(launchMainObserver)
        signUpViewModel.launchLogin.removeObserver(launchLoginObserver)
        signUpViewModel.nameValidation.removeObserver(nameValidationObserver)
        signUpViewModel.emailValidation.removeObserver(emailValidationObserver)
        signUpViewModel.passwordValidation.removeObserver(passwordValidationObserver)
    }

    @Test
    fun givenServerResponse200_whenSignUp_shouldLaunchMainActivity() {
        // Create Dummy User
        val user = DataModelObjectProvider.signedInUser
        // Password for the User (derived from email hash for testing)
        val password = user.email.hashCode().toString()

        // Set the values for the Field LiveData
        signUpViewModel.nameField.value = user.name
        signUpViewModel.emailField.value = user.email
        signUpViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Connected
        doReturn(true).`when`(networkHelper).isNetworkConnected()

        // Mock the UserRepository SignUp Action to provide the SingleSource of newly registered User
        doReturn(Single.just(user))
            .`when`(userRepository)
            .doUserSignUp(user.email, password, user.name)

        // Start the SignUp
        signUpViewModel.onSignUp()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Resource for Name validation results LiveData
        val expectedNameValidationResource = Resource.Success<Int>()
        // Assert that the Name validation results LiveData is set to the expected Resource
        assert(signUpViewModel.nameValidation.value == expectedNameValidationResource)
        // Verify that the Name validation results LiveData Observer has received the expected Resource
        verify(nameValidationObserver).onChanged(expectedNameValidationResource)

        // Expected Resource for Email validation results LiveData
        val expectedEmailValidationResource = Resource.Success<Int>()
        // Assert that the Email validation results LiveData is set to the expected Resource
        assert(signUpViewModel.emailValidation.value == expectedEmailValidationResource)
        // Verify that the Email validation results LiveData Observer has received the expected Resource
        verify(emailValidationObserver).onChanged(expectedEmailValidationResource)

        // Expected Resource for Password validation results LiveData
        val expectedPasswordValidationResource = Resource.Success<Int>()
        // Assert that the Password validation results LiveData is set to the expected Resource
        assert(signUpViewModel.passwordValidation.value == expectedPasswordValidationResource)
        // Verify that the Password validation results LiveData Observer has received the expected Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordValidationResource)

        // Verify that there was a call to save the user information in the preferences
        verify(userRepository).saveCurrentUser(user)
        // Assert that the SignUp Progress indication LiveData has stopped
        assert(signUpViewModel.signUpProgress.value == false)
        // Verify that the SignUp Progress LiveData Observer received both Start and Stop values
        verify(signUpProgressObserver).onChanged(true)
        verify(signUpProgressObserver).onChanged(false)

        // Expected Event for MainActivity
        val expectedMainEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the MainActivity Launch Event LiveData is triggered
        assert(signUpViewModel.launchMain.value == expectedMainEvent)
        // Verify that the MainActivity Launch Event LiveData Observer received the expected Event
        verify(launchMainObserver).onChanged(expectedMainEvent)
    }

    @Test
    fun givenInvalidEmail_whenSignUp_shouldDispatchEmailInvalidError() {
        // Valid Name
        val name = "test"
        // Invalid Email
        val invalidEmail = "abcdefg"
        // Valid password derived from email hash for testing
        val password = invalidEmail.hashCode().toString()

        // Set the values for the Field LiveData
        signUpViewModel.nameField.value = name
        signUpViewModel.emailField.value = invalidEmail
        signUpViewModel.passwordField.value = password

        // Start the SignUp
        signUpViewModel.onSignUp()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Resource for Name validation results LiveData
        val expectedNameValidationResource = Resource.Success<Int>()
        // Assert that the Name validation results LiveData is set to the expected Resource
        assert(signUpViewModel.nameValidation.value == expectedNameValidationResource)
        // Verify that the Name validation results LiveData Observer has received the expected Resource
        verify(nameValidationObserver).onChanged(expectedNameValidationResource)

        // Expected Error Resource for Invalid Email
        val expectedEmailErrorResource =
            Resource.Error(R.string.error_login_sign_up_email_field_invalid)
        // Assert that the Email validation results LiveData is set to the expected Invalid Error Resource
        assert(signUpViewModel.emailValidation.value == expectedEmailErrorResource)
        // Verify that the Email validation results LiveData Observer has received the expected Invalid Error Resource
        verify(emailValidationObserver).onChanged(expectedEmailErrorResource)

        // Expected Resource for Password validation results LiveData
        val expectedPasswordValidationResource = Resource.Success<Int>()
        // Assert that the Password validation results LiveData is set to the expected Resource
        assert(signUpViewModel.passwordValidation.value == expectedPasswordValidationResource)
        // Verify that the Password validation results LiveData Observer has received the expected Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordValidationResource)

        // Verify that there was no interactions with the UserRepository
        Mockito.verifyNoInteractions(userRepository)
        // Verify that the SignUp Progress LiveData Observer never received any value
        Mockito.verifyNoInteractions(signUpProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        Mockito.verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenInvalidPassword_whenSignUp_shouldDispatchPasswordInvalidError() {
        // Valid Name
        val name = "test"
        // Valid Email
        val email = "test@mindorks.com"
        // Invalid Password (less than 6 min-character-length)
        val invalidPassword = "abc"

        // Set the values for the Field LiveData
        signUpViewModel.nameField.value = name
        signUpViewModel.emailField.value = email
        signUpViewModel.passwordField.value = invalidPassword

        // Start the SignUp
        signUpViewModel.onSignUp()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Resource for Name validation results LiveData
        val expectedNameValidationResource = Resource.Success<Int>()
        // Assert that the Name validation results LiveData is set to the expected Resource
        assert(signUpViewModel.nameValidation.value == expectedNameValidationResource)
        // Verify that the Name validation results LiveData Observer has received the expected Resource
        verify(nameValidationObserver).onChanged(expectedNameValidationResource)

        // Expected Resource for Email validation results LiveData
        val expectedEmailValidationResource = Resource.Success<Int>()
        // Assert that the Email validation results LiveData is set to the expected Resource
        assert(signUpViewModel.emailValidation.value == expectedEmailValidationResource)
        // Verify that the Email validation results LiveData Observer has received the expected Resource
        verify(emailValidationObserver).onChanged(expectedEmailValidationResource)

        // Expected Error Resource for Invalid Password
        val expectedPasswordErrorResource =
            Resource.Error(R.string.error_login_sign_up_password_field_small_length)
        // Assert that the Password validation results LiveData is set to the expected Invalid Error Resource
        assert(signUpViewModel.passwordValidation.value == expectedPasswordErrorResource)
        // Verify that the Password validation results LiveData Observer has received the expected Invalid Error Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordErrorResource)

        // Verify that there was no interactions with the UserRepository
        Mockito.verifyNoInteractions(userRepository)
        // Verify that the SignUp Progress LiveData Observer never received any value
        Mockito.verifyNoInteractions(signUpProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        Mockito.verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenAllEmptyFields_whenSignUp_shouldDispatchEmptyFieldsError() {
        // Set empty values for the Field LiveData
        signUpViewModel.nameField.value = ""
        signUpViewModel.emailField.value = ""
        signUpViewModel.passwordField.value = ""

        // Start the SignUp
        signUpViewModel.onSignUp()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Expected Error Resource for Empty Name
        val expectedNameErrorResource = Resource.Error(R.string.error_sign_up_name_field_empty)
        // Assert that the Name validation results LiveData is set to the expected Error Resource
        assert(signUpViewModel.nameValidation.value == expectedNameErrorResource)
        // Verify that the Name validation results LiveData Observer has received the expected Error Resource
        verify(nameValidationObserver).onChanged(expectedNameErrorResource)

        // Expected Error Resource for Empty Email
        val expectedEmailErrorResource =
            Resource.Error(R.string.error_login_sign_up_email_field_empty)
        // Assert that the Email validation results LiveData is set to the expected Error Resource
        assert(signUpViewModel.emailValidation.value == expectedEmailErrorResource)
        // Verify that the Email validation results LiveData Observer has received the expected Error Resource
        verify(emailValidationObserver).onChanged(expectedEmailErrorResource)

        // Expected Error Resource for Empty Password
        val expectedPasswordErrorResource =
            Resource.Error(R.string.error_login_sign_up_password_field_empty)
        // Assert that the Password validation results LiveData is set to the expected Error Resource
        assert(signUpViewModel.passwordValidation.value == expectedPasswordErrorResource)
        // Verify that the Password validation results LiveData Observer has received the expected Error Resource
        verify(passwordValidationObserver).onChanged(expectedPasswordErrorResource)

        // Verify that there was no interactions with the UserRepository
        Mockito.verifyNoInteractions(userRepository)
        // Verify that the SignUp Progress LiveData Observer never received any value
        Mockito.verifyNoInteractions(signUpProgressObserver)
        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        Mockito.verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenNoInternet_whenSignUp_shouldShowNetworkError() {
        // Create Dummy User
        val user = DataModelObjectProvider.signedInUser
        // Password for the User (derived from email hash for testing)
        val password = user.email.hashCode().toString()

        // Set the values for the Field LiveData
        signUpViewModel.nameField.value = user.name
        signUpViewModel.emailField.value = user.email
        signUpViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Disconnected
        doReturn(false).`when`(networkHelper).isNetworkConnected()

        // Start the SignUp
        signUpViewModel.onSignUp()

        // Expected Network Error Resource
        val expectedNetworkErrorMessage = Resource.Error(R.string.error_network_connection_issue)
        // Assert that expected Network Error is set on the Resource messages LiveData
        assert(signUpViewModel.messageStringId.value == expectedNetworkErrorMessage)
        // Verify that the Resource messages LiveData Observer received the expected Network Error Resource
        verify(messageStringIdObserver).onChanged(expectedNetworkErrorMessage)
    }

    @Test
    fun whenLoginWithEmail_shouldLaunchLoginActivity() {
        // Initiate the "Login with Email" event
        signUpViewModel.onLoginWithEmail()

        // Expected Event for LoginActivity
        val expectedLoginEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the LoginActivity Launch Event LiveData is triggered
        assert(signUpViewModel.launchLogin.value == expectedLoginEvent)
        // Verify that the LoginActivity Launch Event LiveData Observer received the expected Event
        verify(launchLoginObserver).onChanged(expectedLoginEvent)
    }

}