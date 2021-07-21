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

package com.mindorks.kaushiknsanji.instagram.demo.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.TestSchedulerProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.autoUpdateInfoCopy
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.findInternalLatestPostId
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.findInternalOldestPostId
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.makeCopy
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.mapToPostIdCreationDatePairs
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.mapToPostIds
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subscribers.TestSubscriber
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
 * Local Unit Test on [HomeViewModel].
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

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

    // PostRepository instance
    @Mock
    private lateinit var postRepository: PostRepository

    // LiveData Observer for Resource messages
    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

    // LiveData Observer for the data loading progress indication
    @Mock
    private lateinit var loadingProgressObserver: Observer<Boolean>

    // LiveData Observer for the List of All Posts to be reloaded
    @Mock
    private lateinit var reloadAllPostsObserver: Observer<Resource<List<Post>>>

    // Scroll Event LiveData Observer for when the RecyclerView needs to be scrolled to Top item
    @Mock
    private lateinit var scrollToTopObserver: Observer<Event<Boolean>>

    // Scroll Event LiveData Observer for when the RecyclerView needs to be
    // smooth scrolled to Top item
    @Mock
    private lateinit var smoothScrollToTopObserver: Observer<Event<Boolean>>

    // RxJava Test Schedulers
    private lateinit var testScheduler: TestScheduler

    // The ViewModel under test
    private lateinit var homeViewModel: HomeViewModel

    // The logged-in User information
    private lateinit var user: User

    @Before
    fun setUp() {
        // Create the CompositeDisposable
        val compositeDisposable = CompositeDisposable()
        // Create the TestScheduler
        testScheduler = TestScheduler()
        // Create SchedulerProvider for Testing
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        // Get the logged-in User information
        user = DataModelObjectProvider.signedInUser
        // Mock the UserRepository to provide logged-in User information
        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        // Create the HomeViewModel
        homeViewModel = HomeViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository
        )

        // Register the LiveData Observers forever
        homeViewModel.messageStringId.observeForever(messageStringIdObserver)
        homeViewModel.loadingProgress.observeForever(loadingProgressObserver)
        homeViewModel.reloadAllPosts.observeForever(reloadAllPostsObserver)
        homeViewModel.scrollToTop.observeForever(scrollToTopObserver)
        homeViewModel.smoothScrollToTop.observeForever(smoothScrollToTopObserver)
    }

    @After
    fun tearDown() {
        // Remove Registered LiveData Observers
        homeViewModel.messageStringId.removeObserver(messageStringIdObserver)
        homeViewModel.loadingProgress.removeObserver(loadingProgressObserver)
        homeViewModel.reloadAllPosts.removeObserver(reloadAllPostsObserver)
        homeViewModel.scrollToTop.removeObserver(scrollToTopObserver)
        homeViewModel.smoothScrollToTop.removeObserver(smoothScrollToTopObserver)
    }

    //@Test (TODO: Testcase not working as expected)
    fun givenServerResponse200_onLaunch_shouldLoadNewPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Subscribe to the Posts Flowable for tests
        val testResultPostsFlowSubscriber = homeViewModel.getResultPostsFlowable().test()

        // Mock the Network Connectivity to stay Connected
        doReturn(true).`when`(networkHelper).isNetworkConnected()

        // Get some random Posts which are expected to be returned for the pagination batch
        val expectedPosts = DataModelObjectProvider.getRandomPosts(10)
        // Mock the PostRepository to provide a SingleSource of the expected Posts
        doReturn(Single.just(expectedPosts))
            .`when`(postRepository)
            .getAllPostsList(null, null, user)

        // Initiate action
        homeViewModel.onCreate()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Paginator is subscribed and its value is a Pair of (null, null)
        testPaginatorSubscriber.assertValuesOnly(null to null)
        // Assert that the Posts Flowable is subscribed and its value is that of the expected Posts
        testResultPostsFlowSubscriber.assertValuesOnly(expectedPosts)
        // Assert that the Post List in ViewModel received the same expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Assert that the Loading Progress LiveData is set to false
        assert(homeViewModel.loadingProgress.value == false)
        // Verify that the Loading Progress LiveData Observer received a value of true
        verify(loadingProgressObserver).onChanged(true)
        // Verify that the Loading Progress LiveData Observer received a value of false (after true)
        verify(loadingProgressObserver).onChanged(false)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)
    }

    @Test
    fun givenPostsAlreadyLoadedInPreviousRequest_onRelaunch_shouldLoadAlreadyLoadedPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(10)

        // Set a copy of this List of Posts on the ViewModel, to simulate the
        // situation of Posts already loaded
        homeViewModel.getAllPostList().addAll(randomPosts.makeCopy())

        // Initiate action
        homeViewModel.onCreate()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Post List in ViewModel remains unchanged
        assertThat(homeViewModel.getAllPostList()).isEqualTo(randomPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(randomPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenNoInternet_onLaunch_shouldShowNetworkError() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Mock the Network Connectivity to stay Disconnected
        doReturn(false).`when`(networkHelper).isNetworkConnected()

        // Initiate action
        homeViewModel.onCreate()

        // Expected Resource for Network Error
        val expectedErrorResource = Resource.Error(R.string.error_network_connection_issue)
        // Assert that the Expected Network Error Resource is set on the Resource messages LiveData
        assert(homeViewModel.messageStringId.value == expectedErrorResource)
        // Verify that the Resource messages LiveData Observer received the Expected Error Resource
        verify(messageStringIdObserver).onChanged(expectedErrorResource)

        // Verify that there were no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenLoadingInProgress_onLoadMore_shouldNotLoadMorePosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Set the initial loading state to "in progress" for Loading Progress LiveData
        homeViewModel.loadingProgress.value = true

        // Initiate action
        homeViewModel.onLoadMore()

        // Since loading is in progress, no more posts should be requested for download

        // Verify that there were no interactions with the NetworkHelper
        verifyNoInteractions(networkHelper)

        // Verify that there were no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenLastLoadHadFinished_onLoadMore_shouldLoadMorePosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Set the initial loading state to "NOT in progress" for Loading Progress LiveData
        homeViewModel.loadingProgress.value = false

        // Mock the Network Connectivity to stay Connected
        doReturn(true).`when`(networkHelper).isNetworkConnected()

        // Initiate action
        homeViewModel.onLoadMore()

        // Since loading is NOT in progress, more posts should now be requested for download

        // Assert that the Paginator is subscribed and its value is a Pair of (null, null)
        testPaginatorSubscriber.assertValuesOnly(null to null)
    }

    @Test
    fun givenNewPostCreated_onNewPost_shouldShowNewPostAtTheTopOfAllPosts() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(10)

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(randomPosts.makeCopy())

        // Create a new Post for the test
        val newPost = DataModelObjectProvider.getSingleRandomPost()
        // Initiate action passing in the new Post created
        homeViewModel.onNewPost(newPost)

        // Build the expected list of Posts by combining all Posts with the New Post
        // at the top of the list
        val expectedPosts = randomPosts.toMutableList().apply { add(0, newPost) }

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Expected Scroll to Top Event
        val expectedScrollToTopEvent = Event(true)
        // Assert that the Scroll to Top Event LiveData is set to the expected Event
        assert(homeViewModel.scrollToTop.value == expectedScrollToTopEvent)
        // Verify that the Scroll to Top Event LiveData Observer has received the expected Event
        verify(scrollToTopObserver).onChanged(expectedScrollToTopEvent)

        // Expected Resource for Post Creation Message
        val expectedPostCreationMessage = Resource.Success(R.string.message_home_post_published)
        // Assert that the Resource messages LiveData is set to the expected Resource
        assert(homeViewModel.messageStringId.value == expectedPostCreationMessage)
        // Verify that the Resource messages LiveData Observer has received the expected Resource
        verify(messageStringIdObserver).onChanged(expectedPostCreationMessage)

        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)
    }

    @Test
    fun whenUserReselectedHomeMenu_shouldSmoothScrollToTheTopPost() {
        // Initiate the action
        homeViewModel.onHomeMenuReselected()

        // Expected Smooth Scroll to Top Event
        val expectedSmoothScrollToTopEvent = Event(true)
        // Assert that the Smooth Scroll to Top Event LiveData is set to the expected Event
        assert(homeViewModel.smoothScrollToTop.value == expectedSmoothScrollToTopEvent)
        // Verify that the Smooth Scroll to Top Event LiveData Observer has received the expected Event
        verify(smoothScrollToTopObserver).onChanged(expectedSmoothScrollToTopEvent)

        // Verify that the List of Posts reloading LiveData Observer never received any value
        verifyNoInteractions(reloadAllPostsObserver)
        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
    }

    @Test
    fun givenSomePostsByUser_onRefreshUserInfo_shouldReflectNewUserInfoInCorrespondingPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts authored by the logged-in User
        val randomPostsOfUser = DataModelObjectProvider.getRandomPostsOfUser(user, 8)

        // Get some random Posts not authored by the logged-in User
        val randomPostsExcludingUser = DataModelObjectProvider.getRandomPostsExcludingUser(user, 2)

        // Combine all Posts and make them to be liked by Users other than the logged-in User
        val allRandomPosts = randomPostsOfUser.toMutableList().apply {
            // Combine all Posts by using a copy of "randomPostsExcludingUser"
            addAll(randomPostsExcludingUser.makeCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Mock the UserRepository to provide this new User information
        doReturn(updatedUserInfo)
            .`when`(userRepository)
            .getCurrentUser()

        // Initiate action
        homeViewModel.onRefreshUserInfo()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Build expected list of Posts authored with this new User information: START
        // Make Posts using the same Ids and Creation Date of Posts
        val newPostsOfUser = DataModelObjectProvider.createPostsOfUserFromPostIdCreationDatePairs(
            updatedUserInfo, randomPostsOfUser.mapToPostIdCreationDatePairs()
        )
        // Expected list of Posts
        val expectedPosts = newPostsOfUser.toMutableList().apply {
            // Combine all Posts by using a copy of "randomPostsExcludingUser"
            addAll(randomPostsExcludingUser.makeCopy())
            // Copy over liked by list of all Posts
            DataModelObjectProvider.copyLikedByListOfPosts(allRandomPosts, this)
        }
        // Build expected list of Posts authored with this new User information: END

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there were no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenSomePostsByUserAndPostsLikedByUser_onRefreshUserInfo_shouldReflectNewUserInfoInCorrespondingPostsAndLikes() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts authored by the logged-in User
        val randomPostsOfUser = DataModelObjectProvider.getRandomPostsOfUser(user, 8)

        // Get some random Posts not authored by the logged-in User
        val randomPostsExcludingUser = DataModelObjectProvider.getRandomPostsExcludingUser(user, 2)

        // Combine all Posts and make them to be liked by Users other than the logged-in User
        val allRandomPosts = randomPostsOfUser.toMutableList().apply {
            // Combine all Posts by using a copy of "randomPostsExcludingUser"
            addAll(randomPostsExcludingUser.makeCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like Posts
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
        }

        // Ids of the Post to be liked by logged-in User
        val postIdsLikedByLoggedInUser = allRandomPosts.mapToPostIds().shuffled().take(3)

        // Make all randomly selected Posts to be liked by logged-in User
        DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
            listOf(user), // Logged-in User to like the Posts
            allRandomPosts,
            postIdsLikedByLoggedInUser // Ids of the Posts to be liked by logged-in User
        )

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Mock the UserRepository to provide this new User information
        doReturn(updatedUserInfo)
            .`when`(userRepository)
            .getCurrentUser()

        // Initiate action
        homeViewModel.onRefreshUserInfo()
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Build expected list of Posts authored with this new User information: START
        // Make Posts using the same Ids and Creation Date of Posts
        val newPostsOfUser = DataModelObjectProvider.createPostsOfUserFromPostIdCreationDatePairs(
            updatedUserInfo, randomPostsOfUser.mapToPostIdCreationDatePairs()
        )
        // Expected list of Posts
        val expectedPosts = newPostsOfUser.toMutableList().apply {
            // Combine all Posts by using a copy of "randomPostsExcludingUser"
            addAll(randomPostsExcludingUser.makeCopy())
            // Copy over liked by list of all Posts
            DataModelObjectProvider.copyLikedByListOfPosts(allRandomPosts, this)
            // Overwrite the likes in Posts which were selectively liked by logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(updatedUserInfo),
                this,
                postIdsLikedByLoggedInUser
            )
        }
        // Build expected list of Posts authored with this new User information: END

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenIdOfExistingPostDeleted_onPostDeleted_shouldRemoveDeletedPostFromLoadedPostsAndReloadAllPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another Post which will be added now and deleted later for test
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            add(postDeleted)
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Initiate action passing in the Id of the Post deleted
        homeViewModel.onPostDeleted(postDeleted.id)
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(randomPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(randomPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun givenIdOfNonExistingPostDeleted_onPostDeleted_shouldTakeNoAction() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another Post which will be NOT added to "randomPosts" but tried for delete later
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(randomPosts.makeCopy())

        // Initiate action passing in the Id of the Non existing Post deleted
        homeViewModel.onPostDeleted(postDeleted.id)
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Verify that the List of Posts reloading LiveData Observer never received any value
        verifyNoInteractions(reloadAllPostsObserver)
        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun whenUserLikedPostInAnotherActivity_shouldRefreshLikeStatusOnPostAndReloadAllPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which will be liked by User and tested
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            // Include a copy of the Post that will be liked by the User
            add(postLikedByUser.shallowCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
        }

        // Build expected list of Posts from the copy of "allRandomPosts"
        val expectedPosts = allRandomPosts.makeCopy().apply {
            // Make the expected Post to be liked by logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to like the Posts
                this,
                listOf(postLikedByUser.id) // Ids of the Posts to be liked by logged-in User
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Initiate action passing in the Id of the Post liked with its status
        homeViewModel.onPostLikeUpdated(postLikedByUser.id, true)
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun whenUserUnlikedPostInAnotherActivity_shouldRefreshLikeStatusOnPostAndReloadAllPosts() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which will be unliked by User and tested
        val postUnlikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            // Include a copy of the Post that will be unliked by the User
            add(postUnlikedByUser.shallowCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
            // Make the selected Post to be liked by logged-in User, for testing unlike action later
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to like the Posts
                this,
                listOf(postUnlikedByUser.id) // Ids of the Posts to be liked by logged-in User
            )
        }

        // Build expected list of Posts from the copy of "allRandomPosts"
        val expectedPosts = allRandomPosts.makeCopy().apply {
            // Make the expected Post to be unliked by logged-in User
            DataModelObjectProvider.removeUsersFromLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to unlike the Posts
                this,
                listOf(postUnlikedByUser.id) // Ids of the Posts to be unliked by logged-in User
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Initiate action passing in the Id of the Post unliked with its status
        homeViewModel.onPostLikeUpdated(postUnlikedByUser.id, false)
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Expected Resource for List of Posts
        val expectedPostsResource = Resource.Success(expectedPosts)
        // Assert that the List of Posts reloading LiveData is set to the expected Resource
        assert(homeViewModel.reloadAllPosts.value == expectedPostsResource)
        // Verify that the List of Posts reloading LiveData Observer has received the expected Resource
        verify(reloadAllPostsObserver).onChanged(expectedPostsResource)

        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun whenUserDidNotLikeOrUnlikePostInAnotherActivity_shouldTakeNoAction() {
        // Subscribe to the Paginator for tests
        val testPaginatorSubscriber = homeViewModel.getPaginator().test()

        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which is to be liked by the User and tested again for same
        // like action in order to simulate the case where user takes no action on the Post
        // launched in another activity, thereby the like status of Post remains unchanged.
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            // Include a copy of the Post that will be liked by the User
            add(postLikedByUser.shallowCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
            // Make the selected Post to be liked by logged-in User, for testing no action later
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to like the Posts
                this,
                listOf(postLikedByUser.id) // Ids of the Posts to be liked by logged-in User
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Initiate action passing in the Id of the Post with its current like status
        // to simulate no action
        homeViewModel.onPostLikeUpdated(postLikedByUser.id, true)
        // Trigger the Schedulers to complete pending actions
        testScheduler.triggerActions()

        // Assert that the Post List in ViewModel remains unchanged
        assertThat(homeViewModel.getAllPostList()).isEqualTo(allRandomPosts)

        // Verify that the List of Posts reloading LiveData Observer never received any value
        verifyNoInteractions(reloadAllPostsObserver)
        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)

        // Verify that there we no interactions with the Paginator
        testPaginatorSubscriber.assertEmpty()
    }

    @Test
    fun whenUserLikedPostDirectlyOnPostItem_shouldSyncGivenPostInTheListOfAllPosts() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which will be liked by User and tested
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            // Include a copy of the Post that will be liked by the User
            add(postLikedByUser.shallowCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Build expected list of Posts from the copy of "allRandomPosts"
        val expectedPosts = allRandomPosts.makeCopy().apply {
            // Make the expected Post to be liked by logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to like the Posts
                this,
                listOf(postLikedByUser.id) // Ids of the Posts to be liked by logged-in User
            )
        }

        // Initiate action passing in the liked Post from expected list of
        // Posts to sync the update
        homeViewModel.onLikeUnlikeSync(expectedPosts.first { post: Post -> post.id == postLikedByUser.id })

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Verify that the List of Posts reloading LiveData Observer never received any value
        verifyNoInteractions(reloadAllPostsObserver)
        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)
    }

    @Test
    fun whenUserUnlikedPostDirectlyOnPostItem_shouldSyncGivenPostInTheListOfAllPosts() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which will be unliked by User and tested
        val postUnlikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            // Include a copy of the Post that will be unliked by the User
            add(postUnlikedByUser.shallowCopy())
            // Make all Posts to be liked by Users other than the logged-in User
            DataModelObjectProvider.addUsersToLikedByListOfPosts(
                DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
                this,
                listOf(user) // Exclude logged-in User from liking the Posts
            )
            // Make the selected Post to be liked by logged-in User, for testing unlike action later
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to like the Posts
                this,
                listOf(postUnlikedByUser.id) // Ids of the Posts to be liked by logged-in User
            )
        }

        // Set a copy of this List of Posts on the ViewModel
        homeViewModel.getAllPostList().addAll(allRandomPosts.makeCopy())

        // Build expected list of Posts from the copy of "allRandomPosts"
        val expectedPosts = allRandomPosts.makeCopy().apply {
            // Make the expected Post to be unliked by logged-in User
            DataModelObjectProvider.removeUsersFromLikedByListOfSelectedPosts(
                listOf(user), // Logged-in User to unlike the Posts
                this,
                listOf(postUnlikedByUser.id) // Ids of the Posts to be unliked by logged-in User
            )
        }

        // Initiate action passing in the unliked Post from expected list of
        // Posts to sync the update
        homeViewModel.onLikeUnlikeSync(expectedPosts.first { post: Post -> post.id == postUnlikedByUser.id })

        // Assert that the Post List in ViewModel is updated in the same way as expected Posts
        assertThat(homeViewModel.getAllPostList()).isEqualTo(expectedPosts)

        // Verify that the List of Posts reloading LiveData Observer never received any value
        verifyNoInteractions(reloadAllPostsObserver)
        // Verify that the Loading Progress LiveData Observer never received any value
        verifyNoInteractions(loadingProgressObserver)
        // Verify that the Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(scrollToTopObserver)
        // Verify that the Smooth Scroll to Top LiveData Observer never received any value
        verifyNoInteractions(smoothScrollToTopObserver)
    }

    @Test
    fun testPaginationFidelityWithTestPings() {
        /**
         * Test written to evaluate the fidelity of [PublishProcessor] used for pagination and
         * to rule out any stale behavior of [PublishProcessor] by introducing some dummy items at
         * intervals when there are no actual items being emitted by the upstream.
         */

        // Get the Paginator from ViewModel for signaling pagination events
        val paginator: PublishProcessor<Pair<String?, String?>> = homeViewModel.getPaginator()

        // Subscribe to the Posts Flowable for tests
        val testResultPostsFlowSubscriber: TestSubscriber<List<Post>> =
            homeViewModel.getResultPostsFlowable().test()

        // The Test Ping which will occur at particular intervals of no pagination events detected
        val testPing: Pair<String, String> = "Ping1" to "Ping2"

        // List of Posts output for stubbing the event with Test Ping
        val testPingPostList: List<Post> = listOf(DataModelObjectProvider.createPost("1111"))

        // New Flow of "Pagination Events with Test Pings" to test the fidelity of PublishProcessor
        // used for Pagination
        val paginationEventsWithTestPings: Flowable<Pair<String?, String?>> = withTestPings(
            paginator,
            testPing,
            testScheduler
        )

        // Subscribe to the Flow of "Pagination Events with Test Pings"
        val testPaginatorWithTestPingsSubscriber: TestSubscriber<Pair<String?, String?>> =
            paginationEventsWithTestPings.test()

        // List to store all the events signaled for pagination
        val allEvents: MutableList<Pair<String?, String?>> = mutableListOf()
        // List to store all the Posts received during pagination
        val allPosts: MutableList<Post> = mutableListOf()

        // Function type to signal Next Page Event
        val signalNextPage: () -> Unit = {
            // Next event for pagination
            val requestPage: Pair<String?, String?> =
                allPosts.findInternalLatestPostId() to allPosts.findInternalOldestPostId()

            // Get 3 random Posts
            val responsePagePosts: List<Post> = DataModelObjectProvider.getRandomPosts(3)

            // Mock the PostRepository to provide a SingleSource of the next Page of Posts
            doReturn(Single.just(responsePagePosts))
                .`when`(postRepository)
                .getAllPostsList(requestPage.first, requestPage.second, user)

            // Signal the next event of pagination
            paginator.onNext(requestPage)

            // Rebuild Events and Posts list
            allEvents.add(requestPage)
            allPosts.addAll(responsePagePosts)
        }

        // Function type to receive Next Test Ping Event
        val receiveNextTestPing: () -> Unit = {
            // Mock the PostRepository to provide a SingleSource of the next Page containing a Test Post
            /*doReturn(Single.just(testPingPostList))
                .`when`(postRepository)
                .getAllPostsList(testPing.first, testPing.second, user)*/
            // TODO: Uncomment the above when the Posts Flowable and its TestSubscriber is working as expected

            // Rebuild Events and Posts list
            allEvents.add(testPing)
            allPosts.addAll(testPingPostList)
        }

        // Function type to test all TestSubscribers
        val testSubscribers: () -> Unit = {
            // Test subscription and values in the Flow of "Pagination Events with Test Pings"
            testPaginatorWithTestPingsSubscriber.assertValuesOnly(*allEvents.toTypedArray())
            // Test subscription and values in Posts Flowable
            //testResultPostsFlowSubscriber.assertValuesOnly(allPosts)
            // TODO: Uncomment the above when the Posts Flowable and its TestSubscriber is working as expected
        }

        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Test all subscribers
        testSubscribers()

        // Advance clock by a millisecond, for a Test Ping to appear since there
        // was no event during the last second
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        // Receive Test Ping
        receiveNextTestPing()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Test all subscribers
        testSubscribers()

        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()

        // Advance clock by a second, for a Test Ping to appear since there
        // was no event during this second
        testScheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS)
        // Receive Test Ping
        receiveNextTestPing()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Test all subscribers
        testSubscribers()

        // Advance clock by a millisecond, for a Test Ping to appear since there
        // was no event during the last second
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        // Receive Test Ping
        receiveNextTestPing()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Test all subscribers
        testSubscribers()

        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()

        // Advance clock by almost a second
        testScheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // Test all subscribers
        testSubscribers()

        // Advance clock by a millisecond, for a Test Ping to appear since there
        // was no event during the last second
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        // Receive Test Ping
        receiveNextTestPing()
        // Test all subscribers
        testSubscribers()

        // Advance clock by 3 seconds, for 3 Test Pings to appear since there
        // was no event during the last 3 seconds
        testScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
        // Receive 3 Test Pings
        repeat(3) {
            receiveNextTestPing()
        }
        // Test all subscribers
        testSubscribers()

        // Signal the next event of pagination
        signalNextPage()
        // Test all subscribers
        testSubscribers()
    }

    /**
     * Merges [events] with [testPing] to produce a [Flowable] containing [testPing] at every second
     * interval when the upstream [events] emits no items for a second.
     *
     * @param T The Type of items emitted by [events].
     * @param events The [Flowable] to listen to for emitting [testPing] when there are no upstream
     * events for a second.
     * @param testPing A dummy item of type [T] emitted by the resultant merged [Flowable].
     * @param clock A [Scheduler] to internally manage the timers for timeout of items and also to
     * wait on before item emission.
     *
     * @return A [Flowable] that emits all items emitted by [events] along with [testPing] items
     * which are emitted every second only in the absence of [events] for a second until the
     * next item emitted by [events].
     */
    private fun <T : Any> withTestPings(
        events: Flowable<T>,
        testPing: T,
        clock: Scheduler
    ): Flowable<T> =
        events.mergeWith(
            events.debounce(1, TimeUnit.SECONDS, clock)
                .flatMap {
                    // If the upstream emits no items for a second, start emitting items of [testPing]
                    // for every second until the upstream publishes a new item
                    Flowable.interval(0, 1, TimeUnit.SECONDS, clock)
                        .map { testPing }
                        .takeUntil(events)
                }
        )

}