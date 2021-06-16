package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity
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
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.Serializable

/**
 * Local Unit Test on [MainSharedViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class MainSharedViewModelTest {

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

    // LiveData Observer for HomeFragment redirection events
    @Mock
    private lateinit var redirectHomeObserver: Observer<Event<Boolean>>

    // LiveData Observer for EditProfileActivity launch events
    @Mock
    private lateinit var launchEditProfileObserver: Observer<Event<Map<String, String>>>

    // LiveData Observer for PostDetailActivity launch events
    @Mock
    private lateinit var launchPostDetailObserver: Observer<Event<Map<String, Serializable>>>

    // LiveData Observer for PostLikeActivity launch events
    @Mock
    private lateinit var launchPostLikeObserver: Observer<Event<Map<String, Serializable>>>

    // LiveData Observer for new Post publish events to HomeFragment
    @Mock
    private lateinit var postPublishUpdateToHomeObserver: Observer<Event<Post>>

    // LiveData Observer for new Post publish events to ProfileFragment
    @Mock
    private lateinit var postPublishUpdateToProfileObserver: Observer<Event<Post>>

    // LiveData Observer for user profile info update events to HomeFragment
    @Mock
    private lateinit var userProfileInfoUpdateToHomeObserver: Observer<Event<Boolean>>

    // LiveData Observer for user profile info update events to ProfileFragment
    @Mock
    private lateinit var userProfileInfoUpdateToProfileObserver: Observer<Event<Boolean>>

    // LiveData Observer for Post deleted events to HomeFragment
    @Mock
    private lateinit var postDeletedEventToHomeObserver: Observer<Event<String>>

    // LiveData Observer for Post deleted events to ProfileFragment
    @Mock
    private lateinit var postDeletedEventToProfileObserver: Observer<Event<String>>

    // LiveData Observer for Post like update events to HomeFragment
    @Mock
    private lateinit var postLikeUpdateToHomeObserver: Observer<Event<Pair<String, Boolean>>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var mainSharedViewModel: MainSharedViewModel

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        // Create the MainSharedViewModel
        mainSharedViewModel = MainSharedViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper
        )

        // Register the LiveData Observers forever
        mainSharedViewModel.redirectHome.observeForever(redirectHomeObserver)
        mainSharedViewModel.launchEditProfile.observeForever(launchEditProfileObserver)
        mainSharedViewModel.launchPostDetail.observeForever(launchPostDetailObserver)
        mainSharedViewModel.launchPostLike.observeForever(launchPostLikeObserver)
        mainSharedViewModel.postPublishUpdateToHome.observeForever(postPublishUpdateToHomeObserver)
        mainSharedViewModel.postPublishUpdateToProfile.observeForever(
            postPublishUpdateToProfileObserver
        )
        mainSharedViewModel.userProfileInfoUpdateToHome.observeForever(
            userProfileInfoUpdateToHomeObserver
        )
        mainSharedViewModel.userProfileInfoUpdateToProfile.observeForever(
            userProfileInfoUpdateToProfileObserver
        )
        mainSharedViewModel.postDeletedEventToHome.observeForever(postDeletedEventToHomeObserver)
        mainSharedViewModel.postDeletedEventToProfile.observeForever(
            postDeletedEventToProfileObserver
        )
        mainSharedViewModel.postLikeUpdateToHome.observeForever(postLikeUpdateToHomeObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        mainSharedViewModel.redirectHome.removeObserver(redirectHomeObserver)
        mainSharedViewModel.launchEditProfile.removeObserver(launchEditProfileObserver)
        mainSharedViewModel.launchPostDetail.removeObserver(launchPostDetailObserver)
        mainSharedViewModel.launchPostLike.removeObserver(launchPostLikeObserver)
        mainSharedViewModel.postPublishUpdateToHome.removeObserver(postPublishUpdateToHomeObserver)
        mainSharedViewModel.postPublishUpdateToProfile.removeObserver(
            postPublishUpdateToProfileObserver
        )
        mainSharedViewModel.userProfileInfoUpdateToHome.removeObserver(
            userProfileInfoUpdateToHomeObserver
        )
        mainSharedViewModel.userProfileInfoUpdateToProfile.removeObserver(
            userProfileInfoUpdateToProfileObserver
        )
        mainSharedViewModel.postDeletedEventToHome.removeObserver(postDeletedEventToHomeObserver)
        mainSharedViewModel.postDeletedEventToProfile.removeObserver(
            postDeletedEventToProfileObserver
        )
        mainSharedViewModel.postLikeUpdateToHome.removeObserver(postLikeUpdateToHomeObserver)
    }

    @Test
    fun `on Post creation should publish Post to Home and Profile Fragments and then navigate to HomeFragment`() {
        // Create a dummy Post for Test
        val postCreated = DataModelObjectProvider.getSingleRandomPost()
        // Initiate the action
        mainSharedViewModel.onPostCreated(postCreated)

        // Expected event to be received on Post Creation
        val expectedPostCreationEvent = Event(postCreated)
        // Assert that the Post Publish LiveData of HomeFragment is set to the expected event
        assert(mainSharedViewModel.postPublishUpdateToHome.value == expectedPostCreationEvent)
        // Verify that the Post Publish LiveData Observer of HomeFragment has received the expected event
        verify(postPublishUpdateToHomeObserver).onChanged(expectedPostCreationEvent)
        // Assert that the Post Publish LiveData of ProfileFragment is set to the expected event
        assert(mainSharedViewModel.postPublishUpdateToProfile.value == expectedPostCreationEvent)
        // Verify that the Post Publish LiveData Observer of ProfileFragment has received the expected event
        verify(postPublishUpdateToProfileObserver).onChanged(expectedPostCreationEvent)

        // Expected event received for redirection
        val expectedRedirectionEvent = Event(true)
        // Assert that the Redirection LiveData of HomeFragment is set to the expected event
        assert(mainSharedViewModel.redirectHome.value == expectedRedirectionEvent)
        // Verify that the Redirection LiveData Observer of HomeFragment has received the expected event
        verify(redirectHomeObserver).onChanged(expectedRedirectionEvent)
    }

    @Test
    fun `on click of Edit Profile button in ProfileFragment should launch EditProfileActivity`() {
        // Initiate the action
        mainSharedViewModel.onEditProfileRequest()

        // Expected event to be received on click of Edit Profile button
        val expectedEvent = Event<Map<String, String>>(emptyMap())

        // Assert that the EditProfileActivity launch event LiveData is set to the expected event
        assert(mainSharedViewModel.launchEditProfile.value == expectedEvent)
        // Verify that the EditProfileActivity launch event LiveData Observer has received the expected event
        verify(launchEditProfileObserver).onChanged(expectedEvent)
    }

    @Test
    fun `on user profile info update via EditProfileActivity should reload profile info in Home and Profile Fragments`() {
        // Initiate the action
        mainSharedViewModel.onEditProfileSuccess()

        // Expected event to be received on user profile update from EditProfileActivity
        val expectedEvent = Event(true)

        // Assert that the User profile update LiveData of HomeFragment is set to the expected event
        assert(mainSharedViewModel.userProfileInfoUpdateToHome.value == expectedEvent)
        // Verify that the User profile update LiveData Observer of HomeFragment has received the expected event
        verify(userProfileInfoUpdateToHomeObserver).onChanged(expectedEvent)
        // Assert that the User profile update LiveData of ProfileFragment is set to the expected event
        assert(mainSharedViewModel.userProfileInfoUpdateToProfile.value == expectedEvent)
        // Verify that the User profile update LiveData Observer of ProfileFragment has received the expected event
        verify(userProfileInfoUpdateToProfileObserver).onChanged(expectedEvent)
    }

    @Test
    fun `on click of a Post in Home or Profile Fragments should launch PostDetailActivity for that Post`() {
        // Create a dummy Post for Test
        val postClicked = DataModelObjectProvider.getSingleRandomPost()
        // Initiate the action
        mainSharedViewModel.onPostItemClick(postClicked)

        // Expected event to be received on click of a Post in Home or Profile Fragments
        val expectedEvent = Event(ArrayMap<String, Serializable>().apply {
            put(PostDetailActivity.EXTRA_POST_ID, postClicked.id)
        })

        // Assert that the PostDetailActivity launch event LiveData is set to the expected event
        assert(mainSharedViewModel.launchPostDetail.value == expectedEvent)
        // Verify that the PostDetailActivity launch event LiveData Observer has received the expected event
        verify(launchPostDetailObserver).onChanged(expectedEvent)
    }

    @Test
    fun `on click of likes count in a Post of HomeFragment should launch PostLikeActivity for that Post`() {
        // Create a dummy Post for Test
        val postClicked = DataModelObjectProvider.getSingleRandomPost()
        // Initiate the action
        mainSharedViewModel.onPostLikesCountClick(postClicked)

        // Expected event to be received on click of a Likes count in a Post of HomeFragment
        val expectedEvent = Event(ArrayMap<String, Serializable>().apply {
            put(PostLikeActivity.EXTRA_POST_ID, postClicked.id)
        })

        // Assert that the PostLikeActivity launch event LiveData is set to the expected event
        assert(mainSharedViewModel.launchPostLike.value == expectedEvent)
        // Verify that the PostLikeActivity launch event LiveData Observer has received the expected event
        verify(launchPostLikeObserver).onChanged(expectedEvent)
    }

    @Test
    fun `on Post deletion either from HomeFragment or PostDetailActivity should publish event to reload Posts in both Home and Profile Fragments`() {
        // Create a dummy Post for Test
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()
        // Initiate the action
        mainSharedViewModel.onPostItemDeleted(postDeleted.id)

        // Expected event to be received on delete of a Post either from HomeFragment or PostDetailActivity
        val expectedEvent = Event(postDeleted.id)

        // Assert that the Post deletion event LiveData of HomeFragment is set to the expected event
        assert(mainSharedViewModel.postDeletedEventToHome.value == expectedEvent)
        // Verify that the Post deletion event LiveData Observer of HomeFragment has received the expected event
        verify(postDeletedEventToHomeObserver).onChanged(expectedEvent)
        // Assert that the Post deletion event LiveData of ProfileFragment is set to the expected event
        assert(mainSharedViewModel.postDeletedEventToProfile.value == expectedEvent)
        // Verify that the Post deletion event LiveData Observer of ProfileFragment has received the expected event
        verify(postDeletedEventToProfileObserver).onChanged(expectedEvent)
    }

    @Test
    fun `on result of PostLikeActivity and PostDetailActivity should publish event for HomeFragment to reload the corresponding Post with new status of logged-in User's like on the Post`() {
        // Create a dummy Post for Test
        val postLiked = DataModelObjectProvider.getSingleRandomPost()
        // Initiate the action
        mainSharedViewModel.onPostLikeUpdate(postLiked.id, true)

        // Expected event to be received on result of PostLikeActivity and PostDetailActivity
        val expectedEvent = Event(postLiked.id to true)

        // Assert that the Post Like update LiveData of HomeFragment is set to the expected event
        assert(mainSharedViewModel.postLikeUpdateToHome.value == expectedEvent)
        // Verify that the Post Like update LiveData Observer of HomeFragment has received the expected event
        verify(postLikeUpdateToHomeObserver).onChanged(expectedEvent)
    }

}