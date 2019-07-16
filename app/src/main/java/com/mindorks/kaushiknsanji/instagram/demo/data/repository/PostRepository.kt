package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.MyPostItem
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.LikePostRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.PostCreationRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.UnlikePostRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.AllPostsListResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.MyPostsListResponse
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.PostCreationResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Instagram Post data management.
 *
 * @property networkService Instance of Retrofit API Service [NetworkService] provided by Dagger.
 * @property databaseService Instance of RoomDatabase [DatabaseService] provided by Dagger.
 * @constructor Instance of [PostRepository] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Singleton
class PostRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService
) {

    /**
     * Performs "All Posts List" request with the Remote API and returns a [Single] of the List of [Post]s
     * from the response.
     *
     * @param firstPostId [String] representing the id of the first [Post] in the paginated request. Can be null.
     * @param lastPostId [String] representing the id of the last [Post] in the paginated request. Can be null.
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of the List of [Post]s from the response.
     */
    fun getAllPostsList(firstPostId: String?, lastPostId: String?, user: User): Single<List<Post>> =
        networkService.doAllPostsListCall(
            firstPostId,
            lastPostId,
            user.id,
            user.accessToken
        ).map { response: AllPostsListResponse ->
            // Transforming [AllPostsListResponse] to List of [Post]
            response.posts
        }

    /**
     * Performs a Like on the [post] via the Remote API and returns a [Single] of the [post] recorded with "likedBy"
     * entry for the logged-in [user].
     *
     * @param post [Post] being liked by the [user]
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of the [Post] recorded with "likedBy" entry for the logged-in [user].
     */
    fun likePost(post: Post, user: User): Single<Post> =
        networkService.doLikePostCall(
            LikePostRequest(post.id),
            user.id,
            user.accessToken
        ).map {
            // Return the [post] after updating its "likedBy" entry
            post.apply {
                likedBy?.let { likedByList: MutableList<Post.User> ->
                    // Updating the "likedBy" list with logged-in User info if not already liked by this User
                    likedByList.find { userEmbedded: Post.User -> userEmbedded.id == user.id } ?: likedByList.add(
                        Post.User(
                            id = user.id,
                            name = user.name,
                            profilePicUrl = user.profilePicUrl
                        )
                    )
                }
            }
        }

    /**
     * Performs unLike on the [post] via the Remote API and returns a [Single] of the [post] with the "likedBy"
     * entry deleted for the logged-in [user].
     *
     * @param post [Post] being unliked by the [user]
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of the [Post] with the "likedBy" entry deleted for the logged-in [user].
     */
    fun unLikePost(post: Post, user: User): Single<Post> =
        networkService.doUnlikePostCall(
            UnlikePostRequest(post.id),
            user.id,
            user.accessToken
        ).map {
            // Return the [post] after updating its "likedBy" entry
            post.apply {
                likedBy?.let { likedByList: MutableList<Post.User> ->
                    // Updating the "likedBy" list by deleting the logged-in User entry if present
                    likedByList.removeAll { userEmbedded: Post.User -> userEmbedded.id == user.id }
                }
            }
        }

    /**
     * Performs Post creation via the Remote API and returns a [Single] of the [Post] created, from the response.
     *
     * @param imageUrl [String] representing the URL of the uploaded Image.
     * @param imageWidth [Int] representing the width of the uploaded Image.
     * @param imageHeight [Int] representing the height the uploaded Image.
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of the [Post] created, from the response.
     */
    fun createPost(imageUrl: String, imageWidth: Int, imageHeight: Int, user: User): Single<Post> =
        networkService.doPostCreationCall(
            PostCreationRequest(imageUrl, imageWidth, imageHeight),
            user.id,
            user.accessToken
        ).map { response: PostCreationResponse ->
            // Transforming the [PostCreationResponse] to [Post]
            Post(
                id = response.post.id,
                imageUrl = response.post.imageUrl,
                imageWidth = response.post.imageWidth,
                imageHeight = response.post.imageHeight,
                createdAt = response.post.createdAt,
                creator = Post.User(
                    id = user.id,
                    name = user.name,
                    profilePicUrl = user.profilePicUrl
                ),
                likedBy = mutableListOf()
            )
        }

    /**
     * Performs [user]'s Posts List request via the Remote API and returns a [Single] of the List of [Post]s
     * from the response.
     *
     * @param user Instance of logged-in [User] information.
     * @return A [Single] of the List of [Post]s created by the logged-in [user], from the response.
     */
    fun getUserPostsList(user: User): Single<List<Post>> =
        networkService.doMyPostsListCall(user.id, user.accessToken)
            .map { response: MyPostsListResponse ->
                // Transforming [MyPostsListResponse] to List of [Post]
                response.posts?.run {
                    map { item: MyPostItem ->
                        Post(
                            id = item.id,
                            imageUrl = item.imageUrl,
                            imageWidth = item.imageWidth,
                            imageHeight = item.imageHeight,
                            createdAt = item.createdAt,
                            creator = Post.User(
                                id = user.id,
                                name = user.name,
                                profilePicUrl = user.profilePicUrl
                            ),
                            likedBy = mutableListOf()
                        )
                    }
                }
            }
}