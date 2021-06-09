package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.truth.content.IntentSubject
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.local.rule.UserSessionRule
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.FakeNetworkService
import com.mindorks.kaushiknsanji.instagram.demo.di.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivityResultContracts
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.action.BottomNavigationViewActions
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.action.RecyclerViewItemActions
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.activity.TestActivityResultRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * Instrumented test on [MainActivity].
 *
 * @author Kaushik N Sanji
 */
class MainActivityTest {

    // Get the Application Context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val componentRule = TestComponentRule(appContext)

    /**
     * [org.junit.rules.TestRule] for User session.
     */
    private val userSessionRule = UserSessionRule(componentRule)

    /**
     * [ActivityScenarioRule] for [MainActivity] setup, which launches MainActivity
     * for every test method.
     */
    private val mainActivityScenarioRule =
        ActivityScenarioRule<MainActivity>(Intent(appContext, MainActivity::class.java))

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule], followed by [userSessionRule],
     * followed by [mainActivityScenarioRule]
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)
        .around(userSessionRule)
        .around(mainActivityScenarioRule)

    @Before
    fun setUp() {
        // Start to capture Intents
        Intents.init()

        // Delay each test by a second, in order to allow the layout to load completely
        SystemClock.sleep(1000)
    }

    @After
    fun tearDown() {
        // Stop and release all the Intents captured
        Intents.release()
    }

    @Test
    fun testCheckBottomNavMenuHomeFragmentViewsDisplay() {
        // First assert MainActivity is launched by checking if the Bottom Navigation View is displayed
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the Home button of the Bottom Navigation Menu to display
        // HomeFragment in the fragment container
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .perform(BottomNavigationViewActions.navigateTo(R.id.action_main_bottom_nav_home))

        // Assert HomeFragment is loaded by checking if the RecyclerView for Posts is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckBottomNavMenuPhotoFragmentViewsDisplay() {
        // First assert MainActivity is launched by checking if the Bottom Navigation View is displayed
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the "Add Photos" button of the Bottom Navigation Menu to display
        // PhotoFragment in the fragment container
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .perform(BottomNavigationViewActions.navigateTo(R.id.action_main_bottom_nav_photo))

        // Assert PhotoFragment is loaded by checking if the Camera and Gallery Options are displayed
        Espresso.onView(ViewMatchers.withId(R.id.image_photo_option_camera))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image_photo_option_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckBottomNavMenuProfileFragmentViewsDisplay() {
        // First assert MainActivity is launched by checking if the Bottom Navigation View is displayed
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the Profile button of the Bottom Navigation Menu to display
        // ProfileFragment in the fragment container
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .perform(BottomNavigationViewActions.navigateTo(R.id.action_main_bottom_nav_profile))

        // Assert ProfileFragment is loaded by checking if the Edit Profile button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.button_profile_edit))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenAvailablePostsInHomeBottomNavMenu_onClickOfPost_shouldLaunchPostDetailActivity() {
        // Load HomeFragment and verify
        testCheckBottomNavMenuHomeFragmentViewsDisplay()

        // Block PostDetailActivity start to capture Intents
        InstrumentationRegistry.getInstrumentation().addMonitor(
            Instrumentation.ActivityMonitor(PostDetailActivity::class.java.name, null, true)
        )

        // Click on the Post item at Position 5 which is posted by user "User_20",
        // in order to launch PostDetailActivity
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    ViewActions.click()
                )
            )

        // Whenever an activity is launched, current activity goes into the STOPPED state.
        // Hence simulate the same so that we can capture the Intents and verify them. This
        // prevents from resetting the Intents captured when PostDetailActivity is being launched.
        mainActivityScenarioRule.scenario.moveToState(Lifecycle.State.STARTED)

        // Verify that the activity launched via the Intent was PostDetailActivity
        Intents.intended(IntentMatchers.hasComponent(PostDetailActivity::class.java.name))

        // Assert that the number of Intents captured till last action was 2.
        // This includes the Intent used for launching MainActivity at the start of the test.
        Truth.assertThat(Intents.getIntents()).hasSize(2)

        Intents.getIntents().first { intent: Intent ->
            // Filter only the Intent meant for PostDetailActivity
            intent.component?.className?.contains(PostDetailActivity::class.java.simpleName)
                ?: false
        }.run {
            // Assert this Intent is for PostDetailActivity
            IntentSubject.assertThat(this).hasComponentClass(PostDetailActivity::class.java.name)
            // Assert this Intent has extras containing the "PostDetailActivity.EXTRA_POST_ID" Key
            // needed for launching PostDetailActivity for a particular Post that was clicked.
            IntentSubject.assertThat(this).extras().containsKey(PostDetailActivity.EXTRA_POST_ID)
        }

    }

    @Test
    fun givenAvailablePostsInHomeBottomNavMenu_onClickOfPost_shouldLaunchPostDetailActivityAndReturnResultForPostLike() {
        // Id of the Post that will be Liked
        val expectedIdOfPostLiked = "5"
        // State of the Like action that will be published
        val expectedStateOfPostLiked = true

        // TestActivityResultRegistry instance to provide the result of user Like action on Post
        // from PostDetailActivity
        val testActivityResultRegistry = TestActivityResultRegistry(
            Bundle().apply {
                putInt(
                    BaseActivityResultContracts.BUNDLE_KEY_RESULT_CODE,
                    PostDetailActivity.RESULT_LIKE_POST
                )
                putString(PostDetailActivity.EXTRA_RESULT_LIKE_POST_ID, expectedIdOfPostLiked)
                putBoolean(
                    PostDetailActivity.EXTRA_RESULT_LIKE_POST_STATE,
                    expectedStateOfPostLiked
                )
            }
        )

        mainActivityScenarioRule.scenario.apply {
            // Move to CREATED state in order to re-register all the ActivityResultLaunchers of MainActivity
            moveToState(Lifecycle.State.CREATED)

            // On the MainActivity, forcefully re-register all the ActivityResultLaunchers with the
            // TestActivityResultRegistry instance for ActivityResultRegistry in order to
            // stub the PostDetailActivity result
            onActivity { activity: MainActivity ->
                activity.activityResultObserver.initResultLaunchers(
                    testActivityResultRegistry,
                    activity,
                    activity
                )
            }

            // After stubbing, move to STARTED and RESUMED state
            moveToState(Lifecycle.State.STARTED)
            moveToState(Lifecycle.State.RESUMED)
        }

        // Load HomeFragment and verify
        testCheckBottomNavMenuHomeFragmentViewsDisplay()

        // Click on the Post item at Position 5 which is posted by user "User_20",
        // in order to launch PostDetailActivity
        // (Note: Since the result is stubbed, PostDetailActivity will not be launched
        // but will be called upon to return the stubbed result)
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    ViewActions.click()
                )
            )

        // Wait a while to process and receive the result
        SystemClock.sleep(3000)

        // Assert that the PostDetailActivity was called and launched for result
        assertTrue(testActivityResultRegistry.isActivityLaunched)

        mainActivityScenarioRule.scenario.onActivity { activity: MainActivity ->
            // On the MainActivity, assert that the Shared ViewModel received the stubbed result
            // in "postLikeUpdateToHome" LiveData
            assertEquals(
                expectedIdOfPostLiked to expectedStateOfPostLiked,
                activity.mainSharedViewModel.postLikeUpdateToHome.value?.peekContent()
            )
        }

    }

    @Test
    fun givenAvailablePostsInHomeBottomNavMenu_onClickOfPost_shouldLaunchPostDetailActivityAndReturnResultForPostDelete() {
        // Id of the Post that will be Deleted
        val expectedIdOfPostDeleted = "5"
        // Message that will be published on success of Post Delete action
        val expectedDeleteSuccessMessage = FakeNetworkService.RESPONSE_POST_DELETED

        // TestActivityResultRegistry instance to provide the result of user Delete action on Post
        // from PostDetailActivity
        val testActivityResultRegistry = TestActivityResultRegistry(
            Bundle().apply {
                putInt(
                    BaseActivityResultContracts.BUNDLE_KEY_RESULT_CODE,
                    PostDetailActivity.RESULT_DELETE_POST_SUCCESS
                )
                putString(PostDetailActivity.EXTRA_RESULT_DELETED_POST_ID, expectedIdOfPostDeleted)
                putString(
                    PostDetailActivity.EXTRA_RESULT_DELETE_POST_SUCCESS,
                    expectedDeleteSuccessMessage
                )
            }
        )

        mainActivityScenarioRule.scenario.apply {
            // Move to CREATED state in order to re-register all the ActivityResultLaunchers of MainActivity
            moveToState(Lifecycle.State.CREATED)

            // On the MainActivity, forcefully re-register all the ActivityResultLaunchers with the
            // TestActivityResultRegistry instance for ActivityResultRegistry in order to
            // stub the PostDetailActivity result
            onActivity { activity: MainActivity ->
                activity.activityResultObserver.initResultLaunchers(
                    testActivityResultRegistry,
                    activity,
                    activity
                )
            }

            // After stubbing, move to STARTED and RESUMED state
            moveToState(Lifecycle.State.STARTED)
            moveToState(Lifecycle.State.RESUMED)
        }

        // Click on the Profile button of the Bottom Navigation Menu to display
        // ProfileFragment in the fragment container. This is required since we need to later test
        // if the Post Deleted event is also sent to ProfileFragment or not.
        Espresso.onView(ViewMatchers.withId(R.id.bottom_nav_main))
            .perform(BottomNavigationViewActions.navigateTo(R.id.action_main_bottom_nav_profile))

        // Load HomeFragment and verify
        testCheckBottomNavMenuHomeFragmentViewsDisplay()

        // Wait a while to allow the layout to load completely
        SystemClock.sleep(3000)

        // Click on the Post item at Position 5 which is posted by user "User_20",
        // in order to launch PostDetailActivity
        // (Note: Since the result is stubbed, PostDetailActivity will not be launched
        // but will be called upon to return the stubbed result)
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    ViewActions.click()
                )
            )

        // Check if the message "Post Deleted Successfully" is shown by a Toast
        Espresso.onView(ViewMatchers.withId(android.R.id.message))
            .inRoot(RootMatchers.isSystemAlertWindow())
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedDeleteSuccessMessage)))

        // Wait a while to process and receive the result
        SystemClock.sleep(3000)

        // Assert that the PostDetailActivity was called and launched for result
        assertTrue(testActivityResultRegistry.isActivityLaunched)

        mainActivityScenarioRule.scenario.onActivity { activity: MainActivity ->
            // On the MainActivity

            // Assert that the Shared ViewModel received the stubbed result
            // in "postDeletedEventToHome" and "postDeletedEventToProfile" LiveData
            assertEquals(
                expectedIdOfPostDeleted,
                activity.mainSharedViewModel.postDeletedEventToHome.value?.peekContent()
            )
            assertEquals(
                expectedIdOfPostDeleted,
                activity.mainSharedViewModel.postDeletedEventToProfile.value?.peekContent()
            )

        }

    }

    @Test
    fun givenAvailablePostsInHomeBottomNavMenu_onClickOfPostLikesCount_shouldLaunchPostLikeActivity() {
        // Load HomeFragment and verify
        testCheckBottomNavMenuHomeFragmentViewsDisplay()

        // Block PostLikeActivity start to capture Intents
        InstrumentationRegistry.getInstrumentation().addMonitor(
            Instrumentation.ActivityMonitor(PostLikeActivity::class.java.name, null, true)
        )

        // Click on the Likes Count Text of the Post item at Position 5 which is posted
        // by user "User_20", in order to launch the PostLikeActivity
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(6),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.text_home_item_post_like_count,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // Whenever an activity is launched, current activity goes into the STOPPED state.
        // Hence simulate the same so that we can capture the Intents and verify them. This
        // prevents from resetting the Intents captured when PostLikeActivity is being launched.
        mainActivityScenarioRule.scenario.moveToState(Lifecycle.State.STARTED)

        // Verify that the activity launched via the Intent was PostLikeActivity
        Intents.intended(IntentMatchers.hasComponent(PostLikeActivity::class.java.name))

        // Assert that the number of Intents captured till last action was 2.
        // This includes the Intent used for launching MainActivity at the start of the test.
        Truth.assertThat(Intents.getIntents()).hasSize(2)

        Intents.getIntents().first { intent: Intent ->
            // Filter only the Intent meant for PostLikeActivity
            intent.component?.className?.contains(PostLikeActivity::class.java.simpleName)
                ?: false
        }.run {
            // Assert this Intent is for PostLikeActivity
            IntentSubject.assertThat(this).hasComponentClass(PostLikeActivity::class.java.name)
            // Assert this Intent has extras containing the "PostLikeActivity.EXTRA_POST_ID" Key
            // needed for launching PostLikeActivity for a particular Post's Like count that was clicked.
            IntentSubject.assertThat(this).extras().containsKey(PostLikeActivity.EXTRA_POST_ID)
        }

    }

    @Test
    fun givenAvailablePostsInHomeBottomNavMenu_onClickOfPostLikesCount_shouldLaunchPostLikeActivityAndReturnResultForPostLike() {
        // Id of the Post that will be Liked
        val expectedIdOfPostLiked = "5"
        // State of the Like action that will be published
        val expectedStateOfPostLiked = true

        // TestActivityResultRegistry instance to provide the result of user Like action on Post
        // from PostLikeActivity
        val testActivityResultRegistry = TestActivityResultRegistry(
            Bundle().apply {
                putInt(
                    BaseActivityResultContracts.BUNDLE_KEY_RESULT_CODE,
                    PostLikeActivity.RESULT_LIKE_POST
                )
                putString(PostLikeActivity.EXTRA_RESULT_LIKE_POST_ID, expectedIdOfPostLiked)
                putBoolean(PostLikeActivity.EXTRA_RESULT_LIKE_POST_STATE, expectedStateOfPostLiked)
            }
        )

        mainActivityScenarioRule.scenario.apply {
            // Move to CREATED state in order to re-register all the ActivityResultLaunchers of MainActivity
            moveToState(Lifecycle.State.CREATED)

            // On the MainActivity, forcefully re-register all the ActivityResultLaunchers with the
            // TestActivityResultRegistry instance for ActivityResultRegistry in order to
            // stub the PostLikeActivity result
            onActivity { activity: MainActivity ->
                activity.activityResultObserver.initResultLaunchers(
                    testActivityResultRegistry,
                    activity,
                    activity
                )
            }

            // After stubbing, move to STARTED and RESUMED state
            moveToState(Lifecycle.State.STARTED)
            moveToState(Lifecycle.State.RESUMED)
        }

        // Load HomeFragment and verify
        testCheckBottomNavMenuHomeFragmentViewsDisplay()

        // Click on the Likes Count Text of the Post item at Position 5 which is posted
        // by user "User_20", in order to launch the PostLikeActivity
        // (Note: Since the result is stubbed, PostLikeActivity will not be launched
        // but will be called upon to return the stubbed result)
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(6),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.text_home_item_post_like_count,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // Wait a while to process and receive the result
        SystemClock.sleep(3000)

        // Assert that the PostLikeActivity was called and launched for result
        assertTrue(testActivityResultRegistry.isActivityLaunched)

        mainActivityScenarioRule.scenario.onActivity { activity: MainActivity ->
            // On the MainActivity, assert that the Shared ViewModel received the stubbed result
            // in "postLikeUpdateToHome" LiveData
            assertEquals(
                expectedIdOfPostLiked to expectedStateOfPostLiked,
                activity.mainSharedViewModel.postLikeUpdateToHome.value?.peekContent()
            )
        }
    }

    @Test
    fun givenMyPostsInProfileBottomNavMenu_onClickOfPost_shouldLaunchPostDetailActivity() {
        // Load ProfileFragment and verify
        testCheckBottomNavMenuProfileFragmentViewsDisplay()

        // Block PostDetailActivity start to capture Intents
        InstrumentationRegistry.getInstrumentation().addMonitor(
            Instrumentation.ActivityMonitor(PostDetailActivity::class.java.name, null, true)
        )

        // Click on the Post item at Position 5 in order to launch PostDetailActivity
        Espresso.onView(ViewMatchers.withId(R.id.rv_profile_my_posts))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5),
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    ViewActions.click()
                )
            )

        // Whenever an activity is launched, current activity goes into the STOPPED state.
        // Hence simulate the same so that we can capture the Intents and verify them. This
        // prevents from resetting the Intents captured when PostDetailActivity is being launched.
        mainActivityScenarioRule.scenario.moveToState(Lifecycle.State.STARTED)

        // Verify that the activity launched via the Intent was PostDetailActivity
        Intents.intended(IntentMatchers.hasComponent(PostDetailActivity::class.java.name))

        // Assert that the number of Intents captured till last action was 2.
        // This includes the Intent used for launching MainActivity at the start of the test.
        Truth.assertThat(Intents.getIntents()).hasSize(2)

        Intents.getIntents().first { intent: Intent ->
            // Filter only the Intent meant for PostDetailActivity
            intent.component?.className?.contains(PostDetailActivity::class.java.simpleName)
                ?: false
        }.run {
            // Assert this Intent is for PostDetailActivity
            IntentSubject.assertThat(this).hasComponentClass(PostDetailActivity::class.java.name)
            // Assert this Intent has extras containing the "PostDetailActivity.EXTRA_POST_ID" Key
            // needed for launching PostDetailActivity for a particular Post that was clicked.
            IntentSubject.assertThat(this).extras().containsKey(PostDetailActivity.EXTRA_POST_ID)
        }
    }

    @Test
    fun givenProfileBottomNavMenu_onClickOfEditProfile_shouldLaunchEditProfileActivity() {
        // Load ProfileFragment and verify
        testCheckBottomNavMenuProfileFragmentViewsDisplay()

        // Block EditProfileActivity start to capture Intents
        InstrumentationRegistry.getInstrumentation().addMonitor(
            Instrumentation.ActivityMonitor(EditProfileActivity::class.java.name, null, true)
        )

        // Click on the "Edit Profile" button in order to launch EditProfileActivity
        Espresso.onView(ViewMatchers.withId(R.id.button_profile_edit))
            .perform(ViewActions.click())

        // Whenever an activity is launched, current activity goes into the STOPPED state.
        // Hence simulate the same so that we can capture the Intents and verify them. This
        // prevents from resetting the Intents captured when EditProfileActivity is being launched.
        mainActivityScenarioRule.scenario.moveToState(Lifecycle.State.STARTED)

        // Verify that the activity launched via the Intent was EditProfileActivity
        Intents.intended(IntentMatchers.hasComponent(EditProfileActivity::class.java.name))

        // Assert that the number of Intents captured till last action was 2.
        // This includes the Intent used for launching MainActivity at the start of the test.
        Truth.assertThat(Intents.getIntents()).hasSize(2)

        Intents.getIntents().first { intent: Intent ->
            // Filter only the Intent meant for EditProfileActivity
            intent.component?.className?.contains(EditProfileActivity::class.java.simpleName)
                ?: false
        }.run {
            // Assert this Intent is for EditProfileActivity
            IntentSubject.assertThat(this).hasComponentClass(EditProfileActivity::class.java.name)
            // Assert this Intent has No extras
            IntentSubject.assertThat(this).extras().isNull()
        }
    }

    @Test
    fun givenProfileBottomNavMenu_onClickOfEditProfile_shouldLaunchEditProfileActivityAndReturnResultForEditsApplied() {
        // Reload event triggered to Home and Profile Fragments when some Profile Edits are applied
        val expectedReloadEventTriggered = true
        // Message that will be published on success of some Profile Edits applied
        val expectedProfileEditSuccessMessage = FakeNetworkService.RESPONSE_INFO_UPDATED

        // TestActivityResultRegistry instance to provide the result of Profile edits
        // from EditProfileActivity
        val testActivityResultRegistry = TestActivityResultRegistry(
            Bundle().apply {
                putInt(
                    BaseActivityResultContracts.BUNDLE_KEY_RESULT_CODE,
                    EditProfileActivity.RESULT_EDIT_PROFILE_SUCCESS
                )
                putString(
                    EditProfileActivity.EXTRA_RESULT_EDIT_SUCCESS,
                    expectedProfileEditSuccessMessage
                )
            }
        )

        mainActivityScenarioRule.scenario.apply {
            // Move to CREATED state in order to re-register all the ActivityResultLaunchers of MainActivity
            moveToState(Lifecycle.State.CREATED)

            // On the MainActivity, forcefully re-register all the ActivityResultLaunchers with the
            // TestActivityResultRegistry instance for ActivityResultRegistry in order to
            // stub the EditProfileActivity result
            onActivity { activity: MainActivity ->
                activity.activityResultObserver.initResultLaunchers(
                    testActivityResultRegistry,
                    activity,
                    activity
                )
            }

            // After stubbing, move to STARTED and RESUMED state
            moveToState(Lifecycle.State.STARTED)
            moveToState(Lifecycle.State.RESUMED)
        }

        // Load ProfileFragment and verify
        testCheckBottomNavMenuProfileFragmentViewsDisplay()

        // Click on the "Edit Profile" button in order to launch EditProfileActivity
        // (Note: Since the result is stubbed, EditProfileActivity will not be launched
        // but will be called upon to return the stubbed result)
        Espresso.onView(ViewMatchers.withId(R.id.button_profile_edit))
            .perform(ViewActions.click())

        // Check if the message "Profile information has been updated" is shown by a Toast
        Espresso.onView(ViewMatchers.withId(android.R.id.message))
            .inRoot(RootMatchers.isSystemAlertWindow())
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedProfileEditSuccessMessage)))

        // Wait a while to process and receive the result
        SystemClock.sleep(3000)

        // Assert that the EditProfileActivity was called and launched for result
        assertTrue(testActivityResultRegistry.isActivityLaunched)

        mainActivityScenarioRule.scenario.onActivity { activity: MainActivity ->
            // On the MainActivity

            // Assert that the Shared ViewModel received the stubbed result
            // in "userProfileInfoUpdateToHome" and "userProfileInfoUpdateToProfile" LiveData
            assertEquals(
                expectedReloadEventTriggered,
                activity.mainSharedViewModel.userProfileInfoUpdateToHome.value?.peekContent()
            )
            assertEquals(
                expectedReloadEventTriggered,
                activity.mainSharedViewModel.userProfileInfoUpdateToProfile.value?.peekContent()
            )

        }
    }

    @Test
    fun givenProfileBottomNavMenu_onClickOfEditProfile_shouldLaunchEditProfileActivityAndReturnResultForNoModification() {
        // Message that will be published when responded with no edits being made
        val expectedNoProfileEditMessage =
            appContext.resources.getString(R.string.message_edit_profile_no_change)

        // TestActivityResultRegistry instance to provide the result of Profile edits
        // from EditProfileActivity
        val testActivityResultRegistry = TestActivityResultRegistry(
            Bundle().apply {
                putInt(
                    BaseActivityResultContracts.BUNDLE_KEY_RESULT_CODE,
                    EditProfileActivity.RESULT_EDIT_PROFILE_NO_ACTION
                )
            }
        )

        mainActivityScenarioRule.scenario.apply {
            // Move to CREATED state in order to re-register all the ActivityResultLaunchers of MainActivity
            moveToState(Lifecycle.State.CREATED)

            // On the MainActivity, forcefully re-register all the ActivityResultLaunchers with the
            // TestActivityResultRegistry instance for ActivityResultRegistry in order to
            // stub the EditProfileActivity result
            onActivity { activity: MainActivity ->
                activity.activityResultObserver.initResultLaunchers(
                    testActivityResultRegistry,
                    activity,
                    activity
                )
            }

            // After stubbing, move to STARTED and RESUMED state
            moveToState(Lifecycle.State.STARTED)
            moveToState(Lifecycle.State.RESUMED)
        }

        // Load ProfileFragment and verify
        testCheckBottomNavMenuProfileFragmentViewsDisplay()

        // Click on the "Edit Profile" button in order to launch EditProfileActivity
        // (Note: Since the result is stubbed, EditProfileActivity will not be launched
        // but will be called upon to return the stubbed result)
        Espresso.onView(ViewMatchers.withId(R.id.button_profile_edit))
            .perform(ViewActions.click())

        // Check if the expected message is shown by a Toast
        Espresso.onView(ViewMatchers.withId(android.R.id.message))
            .inRoot(RootMatchers.isSystemAlertWindow())
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedNoProfileEditMessage)))

        // Wait a while to process and receive the result
        SystemClock.sleep(3000)

        // Assert that the EditProfileActivity was called and launched for result
        assertTrue(testActivityResultRegistry.isActivityLaunched)
    }

}