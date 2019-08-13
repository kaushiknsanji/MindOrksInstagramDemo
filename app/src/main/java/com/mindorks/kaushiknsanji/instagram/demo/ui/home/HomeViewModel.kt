package com.mindorks.kaushiknsanji.instagram.demo.ui.home

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor

/**
 * [BaseViewModel] subclass for [HomeFragment]
 *
 * @property userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 *
 * @author Kaushik N Sanji
 */
class HomeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the data loading progress indication
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the List of All Posts to be reloaded
    val reloadAllPosts: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    // Scroll Event LiveData when the RecyclerView needs to be scrolled to the Top
    val scrollToTop: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Stores the List of All Posts retrieved till last request
    private val allPostList: MutableList<Post> = mutableListOf()

    // Function reference that creates a copy of [allPostList] for publishing to [reloadAllPosts]
    private val allPostListCopy: (MutableList<Post>) -> List<Post> = { posts: MutableList<Post> ->
        posts.map { post: Post -> post.shallowCopy() }
    }

    // Instance of PublishProcessor that supplies new List of Posts for multiple requests
    private val paginator: PublishProcessor<Pair<String?, String?>> = PublishProcessor.create()

    // Stores the Post Id of the latest Post for pagination
    private var firstPostId: String? = null
    // Stores the Post Id of the oldest Post for pagination
    private var lastPostId: String? = null

    init {
        // Construct the PublishProcessor and save its disposable
        compositeDisposable.add(
            paginator
                .onBackpressureDrop() // Backpressure strategy that discards items on overflow
                .doOnNext {
                    // Called when onNext() is invoked. Will start the [loadingProgress] indication
                    loadingProgress.postValue(true)
                }
                .distinct()
                .concatMapSingle { pageIdPair: Pair<String?, String?> ->
                    // Working on all requests in a synchronised manner

                    // Make the API Call to get the list of All Posts for the subsequent page
                    postRepository.getAllPostsList(pageIdPair.first, pageIdPair.second, user)
                        .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                        // (Handle error for each request immediately so that the paginator can be re-used in case of failures)
                        .doOnError { throwable: Throwable? ->
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { paginatedPostList: List<Post> ->
                        // Save new page List of Posts retrieved in the List of All Posts
                        allPostList.addAll(paginatedPostList)
                        // Refresh the Pagination Ids
                        firstPostId = allPostList.maxBy { post: Post -> post.createdAt.time }?.id
                        lastPostId = allPostList.minBy { post: Post -> post.createdAt.time }?.id
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(false)
                        // Trigger List of All Posts to be reloaded
                        reloadAllPosts.postValue(Resource.success(allPostListCopy(allPostList)))
                    },
                    // OnError
                    { throwable: Throwable? ->
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(false)
                        // Handle and display the appropriate error
                        handleNetworkError(throwable)
                    }
                )
        )

    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        // Restore all Posts into the Adapter if we have already downloaded some posts
        if (allPostList.size > 0) {
            // Trigger List of All Posts to be reloaded
            reloadAllPosts.postValue(Resource.success(allPostListCopy(allPostList)))
        }
        // Load the following list of Posts
        loadMorePosts()
    }

    /**
     * Called when the user has scrolled to the bottom of the current list on screen.
     * More [Post]s will be loaded only when we are not busy loading the previous request (which is
     * based on the value of [loadingProgress])
     */
    fun onLoadMore() = loadingProgress.value?.takeIf { !it }?.let { loadMorePosts() }

    /**
     * Loads the current page details into the [paginator] to fetch the subsequent page List of [Post]s
     */
    private fun loadMorePosts() {
        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, trigger the [paginator] to load more [Post]s
            paginator.onNext(firstPostId to lastPostId)
        }
    }

    /**
     * Called when a new Instagram Post is created, to load the [newPost] to the Top of All Posts shown.
     *
     * @param newPost [Post] instance containing the information of the new Post created.
     */
    fun onNewPost(newPost: Post) {
        // Update the new Post to the Top of All Posts
        allPostList.add(0, newPost)
        // Trigger New List of All Posts to be reloaded
        reloadAllPosts.postValue(Resource.success(allPostListCopy(allPostList)))
        // Trigger an Event to scroll to the Top of the list
        scrollToTop.postValue(Event(true))
        // Display post creation success message
        messageStringId.postValue(Resource.success(R.string.message_home_post_published))
    }

    /**
     * Called when the User Profile information has been updated
     * successfully through [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity].
     *
     * Updates logged-in User information on User's activity and reloads all Post list.
     */
    fun onRefreshUserInfo() {
        // Construct a Single and save the resulting disposable
        compositeDisposable.add(
            // Create a Single from the logged-in User information retrieved from the repository
            Single.fromCallable {
                // Get updated user information from preferences
                userRepository.getCurrentUser()
            }
                .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                .subscribe(
                    // OnSuccess
                    { updatedUser: User? ->
                        updatedUser?.run {
                            // When we have the Updated user information, filter and update only the user's posts loaded till now
                            allPostList.takeIf { it.isNotEmpty() }?.forEachIndexed { index, post: Post ->
                                if (post.creator.id == id) {
                                    allPostList[index] = Post(
                                        id = post.id,
                                        imageUrl = post.imageUrl,
                                        imageWidth = post.imageWidth,
                                        imageHeight = post.imageHeight,
                                        createdAt = post.createdAt,
                                        likedBy = post.likedBy,
                                        creator = Post.User(
                                            id = post.creator.id,
                                            name = name,
                                            profilePicUrl = profilePicUrl
                                        )
                                    )
                                }
                            }

                            // Trigger List of All Posts to be reloaded
                            reloadAllPosts.postValue(Resource.success(allPostListCopy(allPostList)))
                        }
                    },
                    // OnError
                    { throwable: Throwable? ->
                        // Log the error
                        throwable?.let {
                            Logger.e(
                                TAG,
                                "Failed while updating current list of Posts with new User information\nError: ${it.message}"
                            )
                        }
                        // Handle and display the appropriate network error if any
                        handleNetworkError(throwable)
                    }
                )
        )

    }

    /**
     * Called when the User Deletes the Post from the current Fragment or
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity].
     *
     * Removes the Post with the [postId] from the [allPostList] and updates [reloadAllPosts] to reload the list.
     */
    fun onPostDeleted(postId: String) =
        allPostList.takeIf { it.isNotEmpty() }?.apply {
            removeAll { post: Post -> post.id == postId }
        }?.run {
            // Trigger List of All Posts to be reloaded
            reloadAllPosts.postValue(Resource.success(allPostListCopy(this)))
        }

    /**
     * Called after the launch and completion of [com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity]
     * and [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity] for the Post with [postId].
     *
     * The Status of the logged-in User's Like on the Post may or may not be changed by the User in those activities.
     * Hence its status will be checked before refreshing the Post Item in the List to be reloaded.
     *
     * @param postId The Id of the [Post] that was launched in the activities.
     * @param likeStatus The new status of logged-in User's Like on the [Post].
     * `true` if User has liked; `false` otherwise.
     */
    fun onPostLikeUpdated(postId: String, likeStatus: Boolean) {
        allPostList.takeIf { it.isNotEmpty() }?.takeIf { posts: MutableList<Post> ->
            posts.firstOrNull { post: Post ->
                // Filter for the Post with [postId] that has a different user liked status with the current one,
                // which needs an update
                post.id == postId && (post.likedBy?.any { likedBy: Post.User -> likedBy.id == user.id } != likeStatus)
            }?.likedBy?.run {
                // When we have a Post and its liked list needs an update

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
                    removeAll { likedBy: Post.User -> likedBy.id == user.id }
                }
                // Returning true when the Liked list has been updated
                true
            } ?: false // Returning false when the Liked list needed no update
        }?.run {
            // Trigger List of All Posts to be reloaded
            reloadAllPosts.postValue(Resource.success(allPostListCopy(this)))
        }
    }

    /**
     * Called when the logged-in User likes/unlikes a Post. Loads the given [updatedPost] at its position
     * in [allPostList] to reflect the change.
     */
    fun onLikeUnlikeSync(updatedPost: Post) {
        // Find the position of the Post in the list
        allPostList.indexOfFirst { post: Post -> post.id == updatedPost.id }.takeIf { it > -1 }?.let { index: Int ->
            // Save the copy of the [updatedPost] at the [index]
            allPostList[index] = updatedPost.shallowCopy()
        }
    }

    companion object {
        // Constant used for logs
        const val TAG = "HomeViewModel"
    }

}