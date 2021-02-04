package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
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

    // LiveData Observer for login progress
    @Mock
    private lateinit var loginProgressObserver: Observer<Boolean>

    // LiveData Observer for Main Activity launch events
    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for Resource messages
    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

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
        loginViewModel.loginProgress.observeForever(loginProgressObserver)
        loginViewModel.launchMain.observeForever(launchMainObserver)
        loginViewModel.messageStringId.observeForever(messageStringIdObserver)
    }

    @Test
    fun givenServerResponse200_whenLogin_shouldLaunchMainActivity() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Create Dummy User
        val user = User(
            id = "id",
            name = "test",
            email = email,
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = email
        loginViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Connected
        doReturn(true).`when`(networkHelper).isNetworkConnected()

        // Mock the UserRepository Login Action to provide the SingleSource of the User instance created
        doReturn(Single.just(user)).`when`(userRepository).doUserLogin(email, password)

        // Start the Login
        loginViewModel.onLogin()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Verify that there was a call to save the user information in the preferences
        verify(userRepository).saveCurrentUser(user)
        // Assert that the Login Progress indication LiveData has stopped
        assert(loginViewModel.loginProgress.value == false)
        // Verify that the Login Progress LiveData Observer received both Start and Stop values
        verify(loginProgressObserver).onChanged(true)
        verify(loginProgressObserver).onChanged(false)
        // Assert that the MainActivity Launch Event LiveData is triggered
        assert(loginViewModel.launchMain.value == Event(emptyMap<String, String>()))
        // Verify that the MainActivity Launch Event LiveData Observer received the Event
        verify(launchMainObserver).onChanged(Event(emptyMap()))
    }

    @Test
    fun givenNoInternet_whenLogin_shouldShowNetworkError() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Set the values for the Field LiveData
        loginViewModel.emailField.value = email
        loginViewModel.passwordField.value = password

        // Mock the Network Connectivity to stay Disconnected
        doReturn(false).`when`(networkHelper).isNetworkConnected()

        // Start the Login
        loginViewModel.onLogin()

        // Assert that Network Error is set on the Resource messages LiveData
        assert(loginViewModel.messageStringId.value == Resource.Error(R.string.error_network_connection_issue))
        // Verify that the Resource messages LiveData Observer received the Error message
        verify(messageStringIdObserver).onChanged(Resource.Error(R.string.error_network_connection_issue))
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        loginViewModel.loginProgress.removeObserver(loginProgressObserver)
        loginViewModel.launchMain.removeObserver(launchMainObserver)
        loginViewModel.messageStringId.removeObserver(messageStringIdObserver)
    }

}