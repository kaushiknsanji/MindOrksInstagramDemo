package com.mindorks.kaushiknsanji.instagram.demo.utils.test

import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import java.util.*
import kotlin.random.Random

/**
 * Utility object that provides dummy data Models for testing (Integration and/or Unit Tests).
 *
 * @author Kaushik N Sanji
 */
object DataModelObjectProvider {

    // Constants for the dimensions of Images
    private const val IMAGE_WIDTH = 400
    private const val IMAGE_HEIGHT = 400

    /**
     * Random Id generator for [Post] and [User]
     */
    private val nextId: Int get() = Random.nextInt(until = 1000)

    /**
     * Generates a [Sequence] of dummy [Users][User]
     */
    private val userGenerator = sequence {
        // Yield dummy Users
        while (true) {
            yield(createUser(nextId.toString()))
        }
    }

    /**
     * Generates a [Sequence] of dummy [Posts][Post]
     */
    private val postGenerator = sequence {
        // Yield dummy Posts
        while (true) {
            yield(createPost(nextId.toString()))
        }
    }

    /**
     * Generates a [Sequence] of dummy [Posts][Post] created by the given [user][User]
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
     */
    fun createPost(id: String): Post = Post(
        id = id,
        imageUrl = "imageUrl_$id",
        imageWidth = IMAGE_WIDTH,
        imageHeight = IMAGE_HEIGHT,
        createdAt = Date(System.currentTimeMillis()),
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
     */
    fun createPostOfUser(id: String, user: User): Post = Post(
        id = id,
        imageUrl = "imageUrl_$id",
        imageWidth = IMAGE_WIDTH,
        imageHeight = IMAGE_HEIGHT,
        createdAt = Date(System.currentTimeMillis()),
        creator = Post.User(
            // Uses the User details of the User given
            id = user.id,
            name = user.name,
            profilePicUrl = user.profilePicUrl
        ),
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
     * Provides a single random [post][Post].
     */
    fun getSingleRandomPost(): Post = getRandomPosts(1)[0]

    /**
     * Provides a [List] of random [posts][Post] created by the given [user], for the required [count].
     */
    fun getRandomPostsOfUser(user: User, count: Int): List<Post> =
        postsOfUserGenerator(user).take(count * 2).shuffled().take(count).toList()

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
            Post.User(
                id = user.id,
                name = user.name,
                profilePicUrl = user.profilePicUrl
            )
        }.run {
            // On each Post, update the likedBy property with the list of [Post.User]s
            targetPosts.forEach { post: Post ->
                post.likedBy?.addAll(this)
            }
        }
}