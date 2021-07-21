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

package com.mindorks.kaushiknsanji.instagram.demo.utils.test

import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.MyPostItem
import java.util.*
import kotlin.random.Random

/**
 * Utility object that provides dummy data Model instances for testing (Instrumented and/or Local Unit Tests)
 * and functions to facilitate working with them.
 *
 * @author Kaushik N Sanji
 */
object DataModelObjectProvider {

    // Constants for the dimensions of Images
    private const val IMAGE_WIDTH = 400
    private const val IMAGE_HEIGHT = 400

    // Constant for last Id that is internally used by Random function for Post and User
    private const val LAST_RANDOM_ID = 1000

    /**
     * Random Id generator for [Post] and [User].
     */
    private val nextId: Int get() = Random.nextInt(until = LAST_RANDOM_ID)

    /**
     * Default Signed in User "User_10" for testing
     */
    val signedInUser: User = createUser("10")

    /**
     * Generates a [Sequence] of dummy [Users][User].
     */
    private val userGenerator = sequence {
        // Yield dummy Users
        while (true) {
            yield(createUser(nextId.toString()))
        }
    }

    /**
     * Generates a [Sequence] of dummy [Posts][Post].
     */
    private val postGenerator = sequence {
        // Yield dummy Posts
        while (true) {
            yield(createPost(nextId.toString()))
        }
    }

    /**
     * Generates a [Sequence] of dummy [Posts][Post] created by the given [user][User].
     */
    private val postsOfUserGenerator: (user: User) -> Sequence<Post> = { user: User ->
        sequence {
            // Yield dummy Posts created by the given User
            while (true) {
                yield(createPostOfUser(nextId.toString(), user))
            }
        }
    }

    /**
     * Creates and returns a [User] for the given [id].
     */
    fun createUser(id: String): User = User(
        id = id,
        name = "User_$id",
        email = "user$id@mindorks.com",
        accessToken = "accessToken",
        refreshToken = "refreshToken",
        profilePicUrl = "profilePicUrl_$id",
        tagline = "I am user $id!"
    )

    /**
     * Creates and returns a [Post] for the given [id].
     *
     * @param creationDate [Date] to be used for [Post.createdAt] timestamp. Defaulted to current timestamp.
     */
    fun createPost(
        id: String,
        creationDate: Date = Date(System.currentTimeMillis())
    ): Post = Post(
        id = id,
        imageUrl = "imageUrl_$id",
        imageWidth = IMAGE_WIDTH,
        imageHeight = IMAGE_HEIGHT,
        createdAt = creationDate,
        creator = Post.User(
            // Uses same Post Id as User id
            id = id,
            name = "User_$id",
            profilePicUrl = "profilePicUrl_$id"
        ),
        likedBy = mutableListOf()
    )

    /**
     * Creates and returns a [Post] for the given [id] using the given [User].
     *
     * @param creationDate [Date] to be used for [Post.createdAt] timestamp. Defaulted to current timestamp.
     */
    fun createPostOfUser(
        id: String,
        user: User,
        creationDate: Date = Date(System.currentTimeMillis())
    ): Post = Post(
        id = id,
        imageUrl = "imageUrl_$id",
        imageWidth = IMAGE_WIDTH,
        imageHeight = IMAGE_HEIGHT,
        createdAt = creationDate,
        creator = user.toPostUser(),
        likedBy = mutableListOf()
    )

    /**
     * Provides a [List] of random [users][User] for the required [count].
     */
    fun getRandomUsers(count: Int): List<User> =
        userGenerator.take(count * 2).shuffled().take(count).toList()

    /**
     * Provides a single random [user][User].
     */
    fun getSingleRandomUser(): User = getRandomUsers(1)[0]

    /**
     * Provides a [List] of random [posts][Post] for the required [count].
     */
    fun getRandomPosts(count: Int): List<Post> =
        postGenerator.take(count * 2).shuffled().take(count).toList()

    /**
     * Provides a [List] of random [posts][Post] for the required [count],
     * excluding those authored by the given [user].
     */
    fun getRandomPostsExcludingUser(user: User, count: Int): List<Post> =
        getRandomPosts(count).dropPostsOfUser(user)

    /**
     * Provides a single random [post][Post].
     */
    fun getSingleRandomPost(): Post = getRandomPosts(1)[0]

    /**
     * Provides a [List] of random [posts][Post] created by the given [user], for the required [count].
     */
    fun getRandomPostsOfUser(user: User, count: Int): List<Post> =
        postsOfUserGenerator(user).take(count * 2).shuffled().take(count).toList()

    /**
     * Drops [Post]s authored by the given [user] from the receiver [List] of [Post]s.
     *
     * @param user The [User] whose [Post]s needs to be removed from the given list.
     *
     * @return [List] of [Post]s excluding the given [user]'s [Post]s.
     */
    fun List<Post>.dropPostsOfUser(user: User): List<Post> = this.toMutableList().apply {
        removeAll { post: Post -> post.creator.id == user.id }
    }

    /**
     * Creates a [List] of [Post]s authored by the given [user], using the [List] of [Post.id]s
     * and Post creation [Date]s given as [Pair]s.
     *
     * @param user The [User] information needed for creating [Post]s.
     * @param postIdCreationDatePairs A [List] of [Pair]s of [Post.id]s with Post creation [Date]s
     * to be used for creating [Post]s with the same Id and creation timestamp.
     *
     * @return [List] of [Post]s authored by the given [user].
     */
    fun createPostsOfUserFromPostIdCreationDatePairs(
        user: User,
        postIdCreationDatePairs: List<Pair<String, Date>>
    ): List<Post> =
        postIdCreationDatePairs.map { (postId: String, creationDate: Date) ->
            createPostOfUser(postId, user, creationDate)
        }

    /**
     * Provides a single random [post][Post] created by the given [user].
     */
    fun getSingleRandomPostOfUser(user: User): Post = getRandomPostsOfUser(user, 1)[0]

    /**
     * Adds [users] to [liked by list][Post.likedBy] of [targetPosts].
     *
     * @param excludeUsers [List] of [User] which should not like the [targetPosts].
     * Defaulted to an empty list.
     */
    fun addUsersToLikedByListOfPosts(
        users: List<User>,
        targetPosts: List<Post>,
        excludeUsers: List<User> = emptyList()
    ): Unit =
        users.toMutableList().apply {
            // Remove users from [excludeUsers] to be excluded from liking the Posts
            removeAll(excludeUsers)
        }.map { user: User ->
            // Transform [User] to [Post.User] for loading it later into the likedBy property
            // of each Post
            user.toPostUser()
        }.run {
            // On each Post, update the likedBy property by merging with the list of [Post.User]s
            targetPosts.mergeLikedByUsers(this)
        }

    /**
     * Adds [users] to [liked by list][Post.likedBy] of selected [posts] filtered by [targetPostIds].
     *
     * @param targetPostIds [List] of [Post.id]s to filter [posts] for liked by list update.
     */
    fun addUsersToLikedByListOfSelectedPosts(
        users: List<User>,
        posts: List<Post>,
        targetPostIds: List<String>
    ): Unit =
        users.map { user: User ->
            // Transform [User] to [Post.User] for loading it later into the likedBy property
            // of each Post
            user.toPostUser()
        }.run {
            // On each of the selected Post, update the likedBy property by merging with the list of [Post.User]s
            posts.filter { post: Post -> post.id in targetPostIds }
                .mergeLikedByUsers(this)
        }

    /**
     * Remove [users] from [liked by list][Post.likedBy] of selected [posts] filtered by [targetPostIds].
     *
     * @param targetPostIds [List] of [Post.id]s to filter [posts] for liked by list update.
     */
    fun removeUsersFromLikedByListOfSelectedPosts(
        users: List<User>,
        posts: List<Post>,
        targetPostIds: List<String>
    ): Unit =
        users.map { user: User ->
            // Transform [User] to [Post.User] for loading it later into the likedBy property
            // of each Post
            user.toPostUser()
        }.run {
            // On each of the selected Post, update the likedBy property by removing the list of [Post.User]s
            posts.filter { post: Post -> post.id in targetPostIds }
                .removeLikedByUsers(this)
        }

    /**
     * Copies [liked by list][Post.likedBy] of [srcPosts] to [destPosts]. Prior to copying over
     * the liked by list of [srcPosts] to [destPosts], the liked by list of [destPosts] will be erased.
     * For copying of liked by list, each [Post] of [destPosts], should correspond to a [Post]
     * in [srcPosts], else for that particular [Post] no action will be taken.
     */
    fun copyLikedByListOfPosts(
        srcPosts: List<Post>,
        destPosts: List<Post>
    ) {
        destPosts.forEach { destPost: Post ->
            srcPosts.firstOrNull { srcPost: Post -> srcPost.id == destPost.id }
                ?.likedBy
                ?.let { srcPostLikedBy: MutableList<Post.User> ->
                    destPost.likedBy?.apply {
                        // Clear the current list prior to copying over the likedBy list from source
                        clear()
                        addAll(srcPostLikedBy)
                    }
                }
        }
    }

    /**
     * Merges [liked by list][Post.likedBy] of [srcPosts] to [destPosts].
     * For merging of liked by list, each [Post] of [destPosts], should correspond to a [Post]
     * in [srcPosts], else for that particular [Post] no action will be taken.
     */
    fun mergeLikedByListOfPosts(
        srcPosts: List<Post>,
        destPosts: List<Post>
    ) {
        destPosts.forEach { destPost: Post ->
            srcPosts.firstOrNull { srcPost: Post -> srcPost.id == destPost.id }
                ?.likedBy
                ?.let { srcPostLikedBy: MutableList<Post.User> ->
                    // Merge the likedBy list from source with the destination
                    destPost.mergeLikedByUsers(srcPostLikedBy)
                }
        }
    }

    /**
     * Merges [liked by list][Post.likedBy] of the receiver's [List] of [Post]s with the given
     * [List] of [likedByUsers].
     *
     * @param likedByUsers [List] of [Post.User]s to merge with the liked by list
     * in each of the receiver's [List] of [Post]s.
     */
    fun List<Post>.mergeLikedByUsers(likedByUsers: List<Post.User>) {
        // Get the Id's of the Users to be merged
        val likedByUsersIdsToMerge = likedByUsers.map { user: Post.User -> user.id }
        // For each Post of the receiver, merge the likedBy list with the given list of users
        this.forEach { post: Post ->
            post.mergeLikedByUsers(likedByUsers, likedByUsersIdsToMerge)
        }
    }

    /**
     * Merges [liked by list][Post.likedBy] of the receiver's [Post] with the given
     * [List] of [likedByUsers].
     *
     * @param likedByUsers [List] of [Post.User]s to merge with the liked by list of [Post].
     * @param likedByUsersIds [List] of selected [Post.User.id]s from [likedByUsers] whose
     * corresponding user entry in the receiver's [Post.likedBy] needs to be removed for merging
     * with the [likedByUsers] data. Defaulted to all the [Post.User.id]s of the given [likedByUsers].
     */
    fun Post.mergeLikedByUsers(
        likedByUsers: List<Post.User>,
        likedByUsersIds: List<String> = likedByUsers.map { user: Post.User -> user.id }
    ) {
        likedBy?.let { likedByList: MutableList<Post.User> ->
            // When likedByList is present, remove all the users that needs merging
            likedByList.removeAll { likedByUser: Post.User -> likedByUser.id in likedByUsersIds }
            // Add all the users that needs to be merged with
            likedByList.addAll(likedByUsers)
        }
    }

    /**
     * Removes the given [List] of [likedByUsers] from each of the receiver's [List] of [Post]s.
     *
     * @param likedByUsers [List] of [Post.User]s to be removed from each of the
     * receiver's [List] of [Post]s.
     */
    fun List<Post>.removeLikedByUsers(likedByUsers: List<Post.User>) {
        // Get the Id's of the Users to be removed
        val likedByUsersIdsToRemove = likedByUsers.map { user: Post.User -> user.id }
        // Remove the users from the likedBy list of each Post of the receiver
        this.forEach { post: Post ->
            post.removeLikedByUsers(likedByUsersIdsToRemove)
        }
    }

    /**
     * Removes liked by users from the receiver [Post] for the given list of Id's of users
     * that needs to be removed.
     *
     * @param likedByUsersIdsToRemove [List] of [String] representing the Id's of users that needs
     * to be removed from the liked by users list of the receiver [Post].
     */
    fun Post.removeLikedByUsers(
        likedByUsersIdsToRemove: List<String>
    ) {
        likedBy?.let { likedByList: MutableList<Post.User> ->
            // When likedByList is present, remove all the users that corresponds to the given
            // list of User Ids
            likedByList.removeAll { likedByUser: Post.User -> likedByUser.id in likedByUsersIdsToRemove }
        }
    }

    /**
     * Converts receiver [Post] to [MyPostItem] and returns it.
     */
    fun Post.toMyPostItem(): MyPostItem = MyPostItem(
        id, imageUrl, imageWidth, imageHeight, createdAt
    )

    /**
     * Converts receiver [User] to [Post.User] and returns it.
     */
    fun User.toPostUser(): Post.User = Post.User(
        id, name, profilePicUrl
    )

    /**
     * Copies receiver [List] of [Post]s and returns it. Each of the [Post] copied to
     * the resulting [List] will contain a shallow copy of [Post.likedBy] list.
     */
    fun List<Post>.makeCopy(): List<Post> = this.map { post: Post -> post.shallowCopy() }

    /**
     * Provides the Id of the latest [Post] from the receiver [List] of [Post]s
     * based on max [Post.createdAt] timestamp.
     *
     * Excludes [Post]s not generated using the generator, i.e, whose Id is not in the
     * range of 0..[LAST_RANDOM_ID]. Hence termed 'Internal'.
     *
     * Can return `null` if there is no such [Post] or the receiver [List] is empty.
     */
    fun List<Post>.findInternalLatestPostId(): String? =
        this.takeIf { it.isNotEmpty() }
            ?.filter { post: Post -> post.id.toInt() <= LAST_RANDOM_ID }
            ?.maxByOrNull { post: Post -> post.createdAt.time }?.id

    /**
     * Provides the Id of the oldest [Post] from the receiver [List] of [Post]s
     * based on min [Post.createdAt] timestamp.
     *
     * Excludes [Post]s not generated using the generator, i.e, whose Id is not in the
     * range of 0..[LAST_RANDOM_ID]. Hence termed 'Internal'.
     *
     * Can return `null` if there is no such [Post] or the receiver [List] is empty.
     */
    fun List<Post>.findInternalOldestPostId(): String? =
        this.takeIf { it.isNotEmpty() }
            ?.filter { post: Post -> post.id.toInt() <= LAST_RANDOM_ID }
            ?.minByOrNull { post: Post -> post.createdAt.time }?.id

    /**
     * Converts receiver [List] of [Post]s to [List] of [Post.id]s.
     */
    fun List<Post>.mapToPostIds(): List<String> = this.map { post: Post -> post.id }

    /**
     * Converts receiver [List] of [Post]s to [List] of [Pair]s of [Post.id]s with Post
     * creation [Date]s which can be used for creating [Post]s with the same Id and
     * creation timestamp.
     */
    fun List<Post>.mapToPostIdCreationDatePairs(): List<Pair<String, Date>> =
        this.map { post: Post -> post.id to post.createdAt }

    /**
     * Auto updates modifiable [User] information with repeated [User.name] and [User.profilePicUrl]
     * through [User.copy] and returns it.
     */
    fun User.autoUpdateInfoCopy(): User = copy(
        name = "$name. $name",
        profilePicUrl = "$profilePicUrl. $profilePicUrl"
    )

}