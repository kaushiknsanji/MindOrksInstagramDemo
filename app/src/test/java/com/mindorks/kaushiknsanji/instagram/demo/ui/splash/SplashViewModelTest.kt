package com.mindorks.kaushiknsanji.instagram.demo.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
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
import java.util.concurrent.TimeUnit

/**
 * Local Unit Test on [SplashViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {

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

    // LiveData Observer for MainActivity launch events
    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for LoginActivity launch events
    @Mock
    private lateinit var launchLoginObserver: Observer<Event<Map<String, String>>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        // Create the SplashViewModel
        splashViewModel = SplashViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository
        )

        // Register the LiveData Observers forever
        splashViewModel.launchMain.observeForever(launchMainObserver)
        splashViewModel.launchLogin.observeForever(launchLoginObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        splashViewModel.launchMain.removeObserver(launchMainObserver)
        splashViewModel.launchLogin.removeObserver(launchLoginObserver)
    }

    @Test
    fun givenNoUser_shouldLaunchLoginActivity() {
        // Mock UserRepository to provide NO user when requested for current user information
        doReturn(null).`when`(userRepository).getCurrentUserOrNull()

        // Start the onCreate process
        splashViewModel.onCreate()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Advance Scheduler clock by a second since the SingleSource uses a timer of 1 second
        // in order to show the splash at start
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Expected Event for LoginActivity
        val expectedLoginEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the LoginActivity Launch Event LiveData is triggered
        assert(splashViewModel.launchLogin.value == expectedLoginEvent)
        // Verify that the LoginActivity Launch Event LiveData Observer received the expected Event
        verify(launchLoginObserver).onChanged(expectedLoginEvent)

        // Verify that the MainActivity Launch Event LiveData Observer never received any Event
        verifyNoInteractions(launchMainObserver)
    }

    @Test
    fun givenRegisteredUser_shouldLaunchMainActivity() {
        // Mock UserRepository to provide a logged-in User when requested for current user information
        doReturn(DataModelObjectProvider.signedInUser).`when`(userRepository).getCurrentUserOrNull()

        // Start the onCreate process
        splashViewModel.onCreate()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Advance Scheduler clock by a second since the SingleSource uses a timer of 1 second
        // in order to show the splash at start
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Expected Event for MainActivity
        val expectedMainEvent = Event<Map<String, String>>(emptyMap())
        // Assert that the MainActivity Launch Event LiveData is triggered
        assert(splashViewModel.launchMain.value == expectedMainEvent)
        // Verify that the MainActivity Launch Event LiveData Observer received the expected Event
        verify(launchMainObserver).onChanged(expectedMainEvent)

        // Verify that the LoginActivity Launch Event LiveData Observer never received any Event
        verifyNoInteractions(launchLoginObserver)
    }
}