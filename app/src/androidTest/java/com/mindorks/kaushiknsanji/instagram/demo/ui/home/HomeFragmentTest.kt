package com.mindorks.kaushiknsanji.instagram.demo.ui.home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.TestComponentRule
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.action.RecyclerViewItemActions
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher.ImageMatchers
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.matcher.RecyclerViewMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * Instrumented Test on [HomeFragment].
 *
 * @author Kaushik N Sanji
 */
@LargeTest
class HomeFragmentTest {

    // Get the Application Context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * [org.junit.rules.TestRule] for Dagger setup.
     */
    private val componentRule = TestComponentRule(appContext)

    /**
     * Getter for the [org.junit.rules.TestRule], which returns the [RuleChain] of TestRules
     * to be applied in the order of [componentRule]
     */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(componentRule)

    @Before
    fun setUp() {
        // Since this HomeFragment is launched only when the user is logged-in,
        // we will have a dummy User signed-in for testing

        // Get the UserRepository to save active user's information
        val userRepository = componentRule.getComponent().getUserRepository()

        // Get User "User_10" and save it as the signed-in user, via the UserRepository
        userRepository.saveCurrentUser(DataModelObjectProvider.createUser("10"))

        // Launch HomeFragment in Container for testing
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.AppTheme)
    }

    @After
    fun tearDown() {
        // Remove the signed-in User information from UserRepository
        componentRule.getComponent().getUserRepository().removeCurrentTestUser()
    }

    @Test
    fun givenAvailablePosts_onLaunch_shouldDisplayPosts() {
        // Create User "User_10"
        val user10 = DataModelObjectProvider.createUser("10")

        // Create User "User_20"
        val user20 = DataModelObjectProvider.createUser("20")

        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check if the Post item at Position 0 is posted by user "User_10"
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        0,
                        ViewMatchers.withText(user10.name),
                        R.id.text_home_item_post_creator_name
                    )
                )
            )

        // Check if the Post item at Position 5 is posted by user "User_20"
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            // Perform scroll to bring up the item on screen
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        5,
                        ViewMatchers.withText(user20.name),
                        R.id.text_home_item_post_creator_name
                    )
                )
            )
    }

    // @Test //(TODO: Not working as expected)
    fun givenAvailablePosts_onClickOfPost_shouldLaunchPostDetailActivity() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Create User "User_20"
        val user20 = DataModelObjectProvider.createUser("20")

        // Click on the Post item at Position 5 which is posted by user "User_20",
        // in order to launch PostDetailActivity
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    RecyclerViewItemActions.actionOnViewInItem(
                        RecyclerViewItemActions.ROOT_VIEW_ID,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // At this moment, PostDetailActivity should have launched.
        // Assert it by checking if the "Liked by" Title Text is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.text_post_detail_likes_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Also, assert if the Photo launch image icon view is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.image_post_detail_photo_launch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Assert if the Post launched in PostDetailActivity is for the Post created by user "User_20"
        Espresso.onView(ViewMatchers.withId(R.id.text_post_detail_creator_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(user20.name)))
    }

    @Test
    fun givenAvailablePosts_onDoubleTapOfPostPhoto_shouldLikeUnlikeThePost() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Double-Tap on the Photo of the Post item at Position 5 which is posted
        // by user "User_20", in order to like the Post
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.image_home_item_post_photo,
                        R.id.rv_home_posts,
                        ViewActions.doubleClick()
                    )
                )
            )

        // Assert that the Post item at Position 5 has been liked by User "User_20", by verifying
        // that the corresponding Heart Image Button is showing the selected state image
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        5,
                        ImageMatchers.hasImageDrawable(R.drawable.ic_heart_selected),
                        R.id.imgbtn_home_item_toggle_post_like
                    )
                )
            )

        // Double-Tap again on the Photo of the Post item at Position 5 which is posted
        // by user "User_20", in order to Unlike the Post
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.image_home_item_post_photo,
                        R.id.rv_home_posts,
                        ViewActions.doubleClick()
                    )
                )
            )

        // Assert that the Post item at Position 5 has been unliked by User "User_20", by verifying
        // that the corresponding Heart Image Button is showing the unselected state image
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        5,
                        ImageMatchers.hasImageDrawable(R.drawable.ic_heart_unselected),
                        R.id.imgbtn_home_item_toggle_post_like
                    )
                )
            )
    }

    // @Test //(TODO: Not working as expected)
    fun givenAvailablePosts_onClickOfLikesCount_shouldLaunchPostLikeActivity() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the Likes Count Text of the Post item at Position 0 which is posted
        // by user "User_10", in order to launch the PostLikeActivity
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.text_home_item_post_like_count,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // At this moment, PostLikeActivity should have launched.
        // Assert it by checking if the Bottom App Bar for likes count is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.bottom_app_bar_post_like))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun givenAvailablePosts_onClickOfHeartImageButton_shouldLikeUnlikeThePost() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the Heart Image Button of the Post item at Position 0 which is posted
        // by user "User_10", in order to like the Post
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.imgbtn_home_item_toggle_post_like,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // Assert that the Post item at Position 0 has been liked by User "User_10", by verifying
        // that the corresponding Heart Image Button is showing the selected state image
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        0,
                        ImageMatchers.hasImageDrawable(R.drawable.ic_heart_selected),
                        R.id.imgbtn_home_item_toggle_post_like
                    )
                )
            )

        // Click again on the Heart Image Button of the Post item at Position 0 which is posted
        // by user "User_10", in order to Unlike the Post
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    RecyclerViewItemActions.actionOnViewInItem(
                        R.id.imgbtn_home_item_toggle_post_like,
                        R.id.rv_home_posts,
                        ViewActions.click()
                    )
                )
            )

        // Assert that the Post item at Position 0 has been unliked by User "User_10", by verifying
        // that the corresponding Heart Image Button is showing the unselected state image
        Espresso.onView(ViewMatchers.withId(R.id.rv_home_posts))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                ViewAssertions.matches(
                    RecyclerViewMatchers.hasViewInItemAtPosition(
                        0,
                        ImageMatchers.hasImageDrawable(R.drawable.ic_heart_unselected),
                        R.id.imgbtn_home_item_toggle_post_like
                    )
                )
            )
    }
}