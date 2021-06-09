package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Local Unit Test on [MainViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

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

    // LiveData Observer for HomeFragment Navigation events
    @Mock
    private lateinit var navigateHomeObserver: Observer<Event<Boolean>>

    // LiveData Observer for PhotoFragment Navigation events
    @Mock
    private lateinit var navigatePhotoObserver: Observer<Event<Boolean>>

    // LiveData Observer for ProfileFragment Navigation events
    @Mock
    private lateinit var navigateProfileObserver: Observer<Event<Boolean>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        // Create the MainViewModel
        mainViewModel = MainViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper
        )

        // Register the LiveData Observers forever
        mainViewModel.navigateHome.observeForever(navigateHomeObserver)
        mainViewModel.navigatePhoto.observeForever(navigatePhotoObserver)
        mainViewModel.navigateProfile.observeForever(navigateProfileObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        mainViewModel.navigateHome.removeObserver(navigateHomeObserver)
        mainViewModel.navigatePhoto.removeObserver(navigatePhotoObserver)
        mainViewModel.navigateProfile.removeObserver(navigateProfileObserver)
    }

    @Test
    fun onLaunch_shouldNavigateToHome() {
        // Expected Event for HomeFragment Navigation
        val expectedHomeNavigationEvent = Event(true)
        // Assert that the HomeFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateHome.value == expectedHomeNavigationEvent)
        // Verify that the HomeFragment Navigation Event LiveData Observer received the expected Event
        verify(navigateHomeObserver).onChanged(expectedHomeNavigationEvent)

        // Verify that the PhotoFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigatePhotoObserver)
        // Verify that the ProfileFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigateProfileObserver)
    }

    @Test
    fun onHomeSelection_shouldNavigateToHome() {
        // When MainViewModel is first initialized, HomeFragment is loaded by default.
        // Expected Event for HomeFragment Navigation
        val expectedHomeNavigationEvent = Event(true)
        // Assert that the HomeFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateHome.value == expectedHomeNavigationEvent)
        // Verify that the HomeFragment Navigation Event LiveData Observer received the expected Event
        verify(navigateHomeObserver).onChanged(expectedHomeNavigationEvent)

        // Navigate to HomeFragment again but manually
        mainViewModel.onHomeSelected()
        // Assert that the HomeFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateHome.value == expectedHomeNavigationEvent)
        // Verify that the HomeFragment Navigation Event LiveData Observer received 2 expected Events in total
        // (This is the second event. First event is received when MainViewModel is first initialized)
        verify(navigateHomeObserver, times(2)).onChanged(expectedHomeNavigationEvent)

        // Verify that the PhotoFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigatePhotoObserver)
        // Verify that the ProfileFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigateProfileObserver)
    }

    @Test
    fun onPhotoSelection_shouldNavigateToPhoto() {
        // When MainViewModel is first initialized, HomeFragment is loaded by default.
        // Expected Event for HomeFragment Navigation
        val expectedHomeNavigationEvent = Event(true)
        // Assert that the HomeFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateHome.value == expectedHomeNavigationEvent)
        // Verify that the HomeFragment Navigation Event LiveData Observer received the expected Event
        verify(navigateHomeObserver).onChanged(expectedHomeNavigationEvent)

        // Navigate to PhotoFragment
        mainViewModel.onPhotoSelected()
        // Expected Event for PhotoFragment Navigation
        val expectedPhotoNavigationEvent = Event(true)
        // Assert that the PhotoFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigatePhoto.value == expectedPhotoNavigationEvent)
        // Verify that the PhotoFragment Navigation Event LiveData Observer received the expected Event
        verify(navigatePhotoObserver).onChanged(expectedPhotoNavigationEvent)

        // Verify that the ProfileFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigateProfileObserver)
    }

    @Test
    fun onProfileSelection_shouldNavigateToProfile() {
        // When MainViewModel is first initialized, HomeFragment is loaded by default.
        // Expected Event for HomeFragment Navigation
        val expectedHomeNavigationEvent = Event(true)
        // Assert that the HomeFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateHome.value == expectedHomeNavigationEvent)
        // Verify that the HomeFragment Navigation Event LiveData Observer received the expected Event
        verify(navigateHomeObserver).onChanged(expectedHomeNavigationEvent)

        // Navigate to ProfileFragment
        mainViewModel.onProfileSelected()
        // Expected Event for ProfileFragment Navigation
        val expectedProfileNavigationEvent = Event(true)
        // Assert that the ProfileFragment Navigation Event LiveData is triggered
        assert(mainViewModel.navigateProfile.value == expectedProfileNavigationEvent)
        // Verify that the ProfileFragment Navigation Event LiveData Observer received the expected Event
        verify(navigateProfileObserver).onChanged(expectedProfileNavigationEvent)

        // Verify that the PhotoFragment Navigation Event LiveData Observer never received any Event
        verifyNoInteractions(navigatePhotoObserver)
    }
}