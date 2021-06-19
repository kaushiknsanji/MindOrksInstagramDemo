package com.mindorks.kaushiknsanji.instagram.demo.ui.home

import androidx.annotation.RestrictTo
import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.util.HomePostListUpdater
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Event
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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
    private val user: User = userRepository.getCurrentUser()

    // Stores the List of All Posts retrieved till last request
    private val allPostList: MutableList<Post> = mutableListOf()

    // Function type that creates a copy of [allPostList] for publishing to [reloadAllPosts]
    private val allPostListCopy: (MutableList<Post>) -> List<Post> = { posts: MutableList<Post> ->
        posts.map { post: Post -> post.shallowCopy() }
    }

    // Stores the Post Id of the latest Post for pagination
    private var firstPostId: String? = null

    // Stores the Post Id of the oldest Post for pagination
    private var lastPostId: String? = null

    // Thread-safe set of Pages requested by the paginator
    private val requestedPageSet: MutableSet<Pair<String?, String?>> =
        Collections.newSetFromMap(ConcurrentHashMap(2))

    // Instance of PublishProcessor that supplies new List of Posts for multiple requests
    private val paginator: PublishProcessor<Pair<String?, String?>> = PublishProcessor.create()

    // Flowable List of Posts resulting from each request of the paginator
    private val resultPostsFlowable: Flowable<List<Post>> = paginator
        .onBackpressureDrop() // Backpressure strategy that discards items on overflow
        .doOnNext {
            // Called when onNext() is invoked. Will start the [loadingProgress] indication
            loadingProgress.postValue(true)
        }
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

    init {
        // Subscribe to the Flowable List of Posts and save its disposable
        compositeDisposable.add(
            resultPostsFlowable
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    // OnNext
                    { paginatedPostList: List<Post> ->
                        // Save new page List of Posts retrieved in the List of All Posts
                        allPostList.addAll(paginatedPostList)
                        // Refresh the Pagination Ids
                        firstPostId =
                            allPostList.maxByOrNull { post: Post -> post.createdAt.time }?.id
                        lastPostId =
                            allPostList.minByOrNull { post: Post -> post.createdAt.time }?.id
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(false)
                        // Trigger List of All Posts to be reloaded
                        reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
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
        if (allPostList.isNotEmpty()) {
            // Trigger List of All Posts to be reloaded if we have already downloaded some posts
            reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
        } else {
            // Load a new page list of Posts when no Posts are loaded yet
            loadMorePosts()
        }
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

            // Next page to be requested
            val requestedPage = firstPostId to lastPostId

            // Try requesting the page to be loaded
            if (requestedPageSet.add(requestedPage)) {
                // If this is a new page, then trigger the [paginator] to load more [Post]s
                paginator.onNext(requestedPage)
            }
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
        reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
        // Trigger an Event to scroll to the Top of the list
        scrollToTop.postValue(Event(true))
        // Display post creation success message
        messageStringId.postValue(Resource.Success(R.string.message_home_post_published))
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
                    { updatedUser: User ->
                        // When we have the Updated user information

                        // Filter and update only the user's posts loaded till now
                        HomePostListUpdater.rewriteUserPostsWithUpdatedUserInfo(
                            allPostList,
                            updatedUser
                        )

                        // Filter and update only the user's likes on self and others' posts
                        HomePostListUpdater.rewriteUserLikesOnPostsWithUpdatedUserInfo(
                            allPostList,
                            updatedUser
                        )

                        // Trigger List of All Posts to be reloaded
                        reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
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
     * Called when the User deletes the Post from the current Fragment or
     * [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity].
     *
     * Removes the Post with the [postId] from the [allPostList] and updates [reloadAllPosts] to reload the list.
     */
    fun onPostDeleted(postId: String) {
        if (HomePostListUpdater.removeDeletedPost(allPostList, postId)) {
            // Trigger List of All Posts to be reloaded when the Post was found and deleted successfully
            reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
        }
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
        if (HomePostListUpdater.refreshUserLikeStatusOnPost(
                allPostList,
                user,
                postId,
                likeStatus
            )
        ) {
            // Trigger List of All Posts to be reloaded when the Liked list has been updated
            reloadAllPosts.postValue(Resource.Success(allPostListCopy(allPostList)))
        }
    }

    /**
     * Called when the logged-in User likes/unlikes a Post, directly on the Post item.
     * Loads the given [updatedPost] at its position in [allPostList] to reflect the change.
     */
    fun onLikeUnlikeSync(updatedPost: Post) {
        HomePostListUpdater.syncPostOnLikeUnlikeAction(allPostList, updatedPost)
    }

    /**
     * Exposes Mutable [List] of All [Post]s retrieved till last request, for testing.
     *
     * @return Returns [allPostList].
     */
    @RestrictTo(value = [RestrictTo.Scope.TESTS])
    fun getAllPostList(): MutableList<Post> = allPostList

    /**
     * Exposes [PublishProcessor] that handles multiple requests
     * to supply new List of Posts, for testing.
     *
     * @return Returns [paginator].
     */
    @RestrictTo(value = [RestrictTo.Scope.TESTS])
    fun getPaginator(): PublishProcessor<Pair<String?, String?>> = paginator

    /**
     * Exposes the [Flowable] [List] of [Post]s resulting from each request
     * of the [paginator], for testing.
     *
     * @return Returns [resultPostsFlowable].
     */
    @RestrictTo(value = [RestrictTo.Scope.TESTS])
    fun getResultPostsFlowable(): Flowable<List<Post>> = resultPostsFlowable

    companion object {
        // Constant used for logs
        const val TAG = "HomeViewModel"
    }

}