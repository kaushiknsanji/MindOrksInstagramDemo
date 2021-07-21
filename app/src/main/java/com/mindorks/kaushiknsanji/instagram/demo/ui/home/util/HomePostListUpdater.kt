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

/**
 * Utility object that facilitates the management of the [List] of [Post]s
 * shown in [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment].
 *
 * @author Kaushik N Sanji
 */
object HomePostListUpdater {

    /**
     * Rewrites only the User's Posts with updated [User] info.
     *
     * @param allPostList [List] of [Post]s loaded till now.
     * @param updatedUser Updated [User] information to be written onto the User's Posts.
     */
    fun rewriteUserPostsWithUpdatedUserInfo(
        allPostList: MutableList<Post>,
        updatedUser: User
    ) {
        allPostList
            .takeIf { it.isNotEmpty() }
            ?.withIndex()
            ?.filter { (_, post: Post) ->
                // Filter for User's Posts
                post.creator.id == updatedUser.id
            }
            ?.forEach { (index, post: Post) ->
                // Replace User info with the updated User info on each of User's Posts
                allPostList[index] = post.copy(
                    creator = post.creator.copy(
                        name = updatedUser.name,
                        profilePicUrl = updatedUser.profilePicUrl
                    )
                )
            }
    }

    /**
     * Rewrites only the User's likes on Posts with updated [User] info.
     *
     * @param allPostList [List] of [Post]s loaded till now.
     * @param updatedUser Updated [User] information to be written onto the User's likes on Posts.
     */
    fun rewriteUserLikesOnPostsWithUpdatedUserInfo(
        allPostList: MutableList<Post>,
        updatedUser: User
    ) {
        allPostList
            .takeIf { it.isNotEmpty() }
            ?.withIndex()
            ?.filter { (_, post: Post) ->
                // Filter for any Posts where User has liked
                post.likedBy?.any { likedByUser: Post.User -> likedByUser.id == updatedUser.id } != null
            }
            ?.forEach { (index, post: Post) ->
                // Replace User info with the updated User info on each of User's likes on Posts
                allPostList[index] = post.copy(
                    likedBy = post.likedBy?.apply {
                        withIndex()
                            .filter { (_, likedByUser: Post.User) -> likedByUser.id == updatedUser.id }
                            .forEach { (index, likedByUser: Post.User) ->
                                this[index] = likedByUser.copy(
                                    name = updatedUser.name,
                                    profilePicUrl = updatedUser.profilePicUrl
                                )
                            }
                    }
                )
            }
    }

    /**
     * Removes deleted post with [postId] from the [List] of [Post]s loaded till now.
     *
     * @param allPostList [List] of [Post]s loaded till now.
     * @param postId [Post.id] of the [Post] to be deleted from [allPostList].
     *
     * @return Returns `true` if the requested [Post] was present and has been successfully
     * removed from [allPostList]; otherwise `false`. Can also return `false` when [allPostList]
     * is found to be empty.
     */
    fun removeDeletedPost(allPostList: MutableList<Post>, postId: String): Boolean =
        allPostList.takeIf { it.isNotEmpty() }?.run {
            removeAll { post: Post -> post.id == postId }
        } ?: false // Returning false when the Post list was empty

    /**
     * Refreshes User's like status on a [Post] identified by its [postId].
     *
     * @param allPostList [List] of [Post]s loaded till now.
     * @param user Current [User] information.
     * @param postId [Post.id] of the [Post] to be refreshed with User like status.
     * @param likeStatus [Boolean] of the new/updated User's like status on a [Post]
     * identified by its [postId]. `true` for Like and `false` for Unlike.
     *
     * @return Returns `true` if the User's like status on a [Post] identified by its [postId]
     * is updated successfully. Returns `false` when there is no need to update the like status and
     * also when [allPostList] is found to be empty.
     */
    fun refreshUserLikeStatusOnPost(
        allPostList: MutableList<Post>,
        user: User,
        postId: String,
        likeStatus: Boolean
    ): Boolean = allPostList.takeIf { it.isNotEmpty() }?.let { posts: MutableList<Post> ->
        posts.firstOrNull { post: Post ->
            // Filter for the Post with [postId] that has a different user liked status with the current one,
            // which needs an update
            post.id == postId && (post.likedBy?.any { likedByUser: Post.User -> likedByUser.id == user.id } != likeStatus)
        }?.likedBy?.run {
            // When we have a Post and its liked list needs an update

            // Update liked list and return its result
            if (likeStatus) {
                // If the Post has been liked by the logged-in User from other activities,
                // then add an entry into the liked list for the logged-in User
                add(
                    Post.User(
                        id = user.id,
                        name = user.name,
                        profilePicUrl = user.profilePicUrl
                    )
                )
            } else {
                // If the Post has been unliked by the logged-in User from other activities,
                // then remove the entry from the liked list
                removeAll { likedByUser: Post.User -> likedByUser.id == user.id }
            }
        } ?: false // Returning false when the Liked list needed no update
    } ?: false // Returning false when the Post list was empty

    /**
     * Synchronizes Post on changes triggered by User like/unlike action on the Post item directly.
     * Copies the given [updatedPost] at its position in [allPostList] to reflect the change.
     *
     * @param allPostList [List] of [Post]s loaded till now.
     * @param updatedPost [Post] with updated information of User's like status.
     */
    fun syncPostOnLikeUnlikeAction(allPostList: MutableList<Post>, updatedPost: Post) {
        // Find the position of the Post in the list
        allPostList.indexOfFirst { post: Post -> post.id == updatedPost.id }.takeIf { it > -1 }
            ?.let { index: Int ->
                // Save the copy of the [updatedPost] at the [index]
                allPostList[index] = updatedPost.shallowCopy()
            }
    }

}