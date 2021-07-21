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

package com.mindorks.kaushiknsanji.instagram.demo.ui.home.util

import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.autoUpdateInfoCopy
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.makeCopy
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.mapToPostIdCreationDatePairs
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.mapToPostIds
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Local Unit Test on [HomePostListUpdater].
 *
 * @author Kaushik N Sanji
 */
class HomePostListUpdaterTest {

    // The logged-in User information
    private lateinit var user: User

    @Before
    fun setUp() {
        // Let User "User_10" be the logged-in User for testing
        user = DataModelObjectProvider.signedInUser
    }

    @After
    fun tearDown() {
        // No-op
    }

    @Test
    fun givenSomePostsByUser_onUserInfoUpdateForPosts_shouldReflectNewUserInfoInCorrespondingPosts() {
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

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

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
            DataModelObjectProvider.copyLikedByListOfPosts(allRandomPosts.makeCopy(), this)
        }
        // Build expected list of Posts authored with this new User information: END

        // Initiate action passing in "allRandomPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserPostsWithUpdatedUserInfo(
            allRandomPosts, updatedUserInfo
        )

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsNotByUser_onUserInfoUpdateForPosts_shouldTakeNoAction() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5).toMutableList()

        // Make all Posts to be liked by Users other than the logged-in User
        DataModelObjectProvider.addUsersToLikedByListOfPosts(
            DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like
            randomPosts,
            listOf(user) // Exclude logged-in User from liking the Posts
        )

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Expected list of Posts will be a copy of the "randomPosts"
        val expectedPosts = randomPosts.makeCopy()

        // Initiate action passing in "randomPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserPostsWithUpdatedUserInfo(
            randomPosts, updatedUserInfo
        )

        // Assert that there is no change in "randomPosts" after update
        assertEquals(expectedPosts, randomPosts)
    }

    @Test
    fun givenNoPosts_onUserInfoUpdateForPosts_shouldTakeNoAction() {
        // Create empty list of Posts for actual and expected
        val actualPosts = mutableListOf<Post>()
        val expectedPosts = mutableListOf<Post>()

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Initiate action passing in "actualPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserPostsWithUpdatedUserInfo(
            actualPosts, updatedUserInfo
        )

        // Assert that there is no change in "actualPosts" after update
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun givenSomePostsByUserAndPostsLikedByUser_onUserInfoUpdateForLikesOnPosts_shouldReflectNewUserInfoInCorrespondingPostsForLikes() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(10).toMutableList()

        // Make all Posts to be liked by Users other than the logged-in User
        DataModelObjectProvider.addUsersToLikedByListOfPosts(
            DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like Posts
            randomPosts,
            listOf(user) // Exclude logged-in User from liking the Posts
        )

        // Ids of the Post to be liked by logged-in User
        val postIdsLikedByLoggedInUser = randomPosts.mapToPostIds().shuffled().take(3)

        // Make all randomly selected Posts to be liked by logged-in User
        DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
            listOf(user), // Logged-in User to like the Posts
            randomPosts,
            postIdsLikedByLoggedInUser // Ids of the Posts to be liked by logged-in User
        )

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Build expected list of Posts
        val expectedPosts = randomPosts.makeCopy().apply {
            // Overwrite the likes in Posts which were selectively liked by logged-in User,
            // with the User's updated information
            DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
                listOf(updatedUserInfo),
                this,
                postIdsLikedByLoggedInUser
            )
        }

        // Initiate action passing in "randomPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserLikesOnPostsWithUpdatedUserInfo(
            randomPosts, updatedUserInfo
        )

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, randomPosts)
    }

    @Test
    fun givenSomePostsNotLikedByUser_onUserInfoUpdateForLikesOnPosts_shouldTakeNoAction() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(10).toMutableList()

        // Make all Posts to be liked by Users other than the logged-in User
        DataModelObjectProvider.addUsersToLikedByListOfPosts(
            DataModelObjectProvider.getRandomUsers(5), // 5 random Users to like Posts
            randomPosts,
            listOf(user) // Exclude logged-in User from liking the Posts
        )

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Expected list of Posts will be a copy of the "randomPosts"
        val expectedPosts = randomPosts.makeCopy()

        // Initiate action passing in "randomPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserLikesOnPostsWithUpdatedUserInfo(
            randomPosts, updatedUserInfo
        )

        // Assert that there is no change in "randomPosts" after update
        assertEquals(expectedPosts, randomPosts)
    }

    @Test
    fun givenNoPosts_onUserInfoUpdateForLikesOnPosts_shouldTakeNoAction() {
        // Create empty list of Posts for actual and expected
        val actualPosts = mutableListOf<Post>()
        val expectedPosts = mutableListOf<Post>()

        // Modify the logged-in User information
        val updatedUserInfo = user.autoUpdateInfoCopy()

        // Initiate action passing in "actualPosts" and "updatedUserInfo"
        HomePostListUpdater.rewriteUserLikesOnPostsWithUpdatedUserInfo(
            actualPosts, updatedUserInfo
        )

        // Assert that there is no change in "actualPosts" after update
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun givenSomePostsWithIdOfExistingPostDeleted_onRemoveDeletedPost_shouldRemoveDeletedPostFromGivenPostsAndReturnTrue() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another Post which will be added now and deleted later for test
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()

        // Combine all Posts
        val allRandomPosts = randomPosts.toMutableList().apply {
            add(postDeleted)
        }

        // Expected list of Posts will be a copy of the "randomPosts"
        val expectedPosts = randomPosts.makeCopy()

        // Initiate action passing in "allRandomPosts" and the Id of the Post deleted
        val result =
            HomePostListUpdater.removeDeletedPost(allRandomPosts, postDeleted.id)

        // Assert that the action on "allRandomPosts" to delete the given Post was successful
        assertTrue(result)

        // Assert that the expected and actual Posts are equal after delete
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsWithIdOfNonExistingPostDeleted_onRemoveDeletedPost_shouldTakeNoActionAndReturnFalse() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5).toMutableList()

        // Get another Post which will be NOT added to "randomPosts" but tried for delete later
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()

        // Expected list of Posts will be a copy of the "randomPosts"
        val expectedPosts = randomPosts.makeCopy()

        // Initiate action passing in "randomPosts" and the Id of the Non existing Post deleted
        val result =
            HomePostListUpdater.removeDeletedPost(randomPosts, postDeleted.id)

        // Assert that no action was taken on "randomPosts"
        assertFalse(result)

        // Assert that there is no change in "randomPosts" after delete
        assertEquals(expectedPosts, randomPosts)
    }

    @Test
    fun givenNoPosts_onRemoveDeletedPost_shouldTakeNoActionAndReturnFalse() {
        // Create empty list of Posts for actual and expected
        val actualPosts = mutableListOf<Post>()
        val expectedPosts = mutableListOf<Post>()

        // Get another Post which will be NOT added to "actualPosts" but tried for delete later
        val postDeleted = DataModelObjectProvider.getSingleRandomPost()

        // Initiate action passing in "actualPosts" and the Id of the Non existing Post deleted
        val result =
            HomePostListUpdater.removeDeletedPost(actualPosts, postDeleted.id)

        // Assert that no action was taken on "actualPosts"
        assertFalse(result)

        // Assert that there is no change in "actualPosts" after delete
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun givenSomePostsWithUserLikeStatusOnAPost_whenUserLikedThePost_shouldReflectLikedStatusOnThePostInGivenPostsAndReturnTrue() {
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

        // Initiate action passing in "allRandomPosts" and the Id of the Post liked with its status
        val result = HomePostListUpdater.refreshUserLikeStatusOnPost(
            allRandomPosts,
            user,
            postLikedByUser.id,
            true
        )

        // Assert that the action on "allRandomPosts" to like the given Post was successful
        assertTrue(result)

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsWithUserLikeStatusOnAPost_whenUserUnlikedThePost_shouldReflectUnlikedStatusOnThePostInGivenPostsAndReturnTrue() {
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

        // Initiate action passing in "allRandomPosts" and the Id of the Post unliked with its status
        val result = HomePostListUpdater.refreshUserLikeStatusOnPost(
            allRandomPosts,
            user,
            postUnlikedByUser.id,
            false
        )

        // Assert that the action on "allRandomPosts" to unlike the given Post was successful
        assertTrue(result)

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsWithUserLikeStatusOnAPost_whenUserDidNotLikeOrUnlikeThePost_shouldTakeNoActionAndReturnFalse() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5)

        // Get another random Post which is to be liked by the User and tested again for same
        // like action in order to simulate the case where user takes no action on the Post,
        // thereby the like status of Post remains unchanged.
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

        // Expected list of Posts will be a copy of the "allRandomPosts"
        val expectedPosts = allRandomPosts.makeCopy()

        // Initiate action passing in "allRandomPosts" and the Id of the Post with its current like status
        // to simulate no action
        val result = HomePostListUpdater.refreshUserLikeStatusOnPost(
            allRandomPosts,
            user,
            postLikedByUser.id,
            true
        )

        // Assert that no action was taken on "allRandomPosts"
        assertFalse(result)

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenNoPosts_whenUserLikedOrUnlikedNonExistingPost_shouldTakeNoActionAndReturnFalse() {
        // Create empty list of Posts for actual and expected
        val actualPosts = mutableListOf<Post>()
        val expectedPosts = mutableListOf<Post>()

        // Get another Post which will be NOT added to "actualPosts" but tried for like action later
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Initiate action passing in "actualPosts" and the Id of the Non existing Post
        // with its current like status
        val result = HomePostListUpdater.refreshUserLikeStatusOnPost(
            actualPosts,
            user,
            postLikedByUser.id,
            true
        )

        // Assert that no action was taken on "actualPosts"
        assertFalse(result)

        // Assert that the expected and actual Posts are equal after update
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun givenSomePostsAndLikedPostDetails_onSyncLikedPost_shouldSyncGivenPostInTheListOfAllPosts() {
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

        // Initiate action passing in "allRandomPosts" and the liked Post from expected list of
        // Posts to sync the update
        HomePostListUpdater.syncPostOnLikeUnlikeAction(
            allRandomPosts,
            expectedPosts.first { post: Post -> post.id == postLikedByUser.id }
        )

        // Assert that the expected and actual Posts are equal after sync
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsAndUnlikedPostDetails_onSyncUnlikedPost_shouldSyncGivenPostInTheListOfAllPosts() {
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

        // Initiate action passing in "allRandomPosts" and the unliked Post from expected list of
        // Posts to sync the update
        HomePostListUpdater.syncPostOnLikeUnlikeAction(
            allRandomPosts,
            expectedPosts.first { post: Post -> post.id == postUnlikedByUser.id }
        )

        // Assert that the expected and actual Posts are equal after sync
        assertEquals(expectedPosts, allRandomPosts)
    }

    @Test
    fun givenSomePostsAndNonExistingPostDetails_onSyncAnyStatusPost_shouldTakeNoAction() {
        // Get some random Posts
        val randomPosts = DataModelObjectProvider.getRandomPosts(5).toMutableList()

        // Get another Post which will be liked by the User, but NOT added to "randomPosts"
        // when tried for sync later
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Make the above Post to be liked by User and some other random Users
        DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
            DataModelObjectProvider.getRandomUsers(5).toMutableList().apply {
                add(user)
            },
            listOf(postLikedByUser), // Post to be liked
            listOf(postLikedByUser.id) // Id of the Post to be liked
        )

        // Expected list of Posts will be a copy of the "randomPosts"
        val expectedPosts = randomPosts.makeCopy()

        // Initiate action passing in "randomPosts" and the liked Post which is not part of the list
        HomePostListUpdater.syncPostOnLikeUnlikeAction(
            randomPosts,
            postLikedByUser
        )

        // Assert that there is no change in "randomPosts" after sync
        assertEquals(expectedPosts, randomPosts)
    }

    @Test
    fun givenNoPostsAndNonExistingPostDetails_onSyncAnyStatusPost_shouldTakeNoAction() {
        // Create empty list of Posts for actual and expected
        val actualPosts = mutableListOf<Post>()
        val expectedPosts = mutableListOf<Post>()

        // Get another Post which will be liked by the User, but NOT added to "actualPosts"
        // when tried for sync later
        val postLikedByUser = DataModelObjectProvider.getSingleRandomPost()

        // Make the above Post to be liked by User and some other random Users
        DataModelObjectProvider.addUsersToLikedByListOfSelectedPosts(
            DataModelObjectProvider.getRandomUsers(5).toMutableList().apply {
                add(user)
            },
            listOf(postLikedByUser), // Post to be liked
            listOf(postLikedByUser.id) // Id of the Post to be liked
        )

        // Initiate action passing in "actualPosts" and the liked Post which is not part of the list
        HomePostListUpdater.syncPostOnLikeUnlikeAction(
            actualPosts,
            postLikedByUser
        )

        // Assert that there is no change in "actualPosts" after sync
        assertEquals(expectedPosts, actualPosts)
    }
}