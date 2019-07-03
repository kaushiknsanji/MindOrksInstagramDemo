package com.mindorks.kaushiknsanji.instagram.demo.ui.home

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor

/**
 * [BaseViewModel] subclass for [HomeFragment]
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @property postRepository [PostRepository] instance for [Post] data.
 *
 * @author Kaushik N Sanji
 */
class HomeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the data loading progress indication
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the paginated List of Posts
    val paginatedPosts: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()!!

    // Stores the List of All Posts retrieved till last request
    private val allPostList: MutableList<Post> = mutableListOf()

    // Instance of PublishProcessor that supplies new List of Posts for multiple requests
    private val paginator: PublishProcessor<Pair<String?, String?>> = PublishProcessor.create()

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
                    { postList: List<Post> ->
                        // Save new page List of Posts retrieved in the List of All Posts
                        allPostList.addAll(postList)
                        // Stop the [loadingProgress] indication
                        loadingProgress.postValue(false)
                        // Update the LiveData with the new page List of Posts retrieved
                        paginatedPosts.postValue(Resource.success(postList))
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
        // Load the initial list of Posts
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
        // Get the Id of the first post in the current list of all posts; 'null' if the list is empty
        val firstPostId: String? = allPostList.takeIf { it.isNotEmpty() }?.let { it[0].id }
        // Get the Id of the last post in the current list of all posts; 'null' if the list is empty
        val lastPostId: String? = allPostList.takeIf { it.size > 1 }?.let { it[it.size - 1].id }

        if (checkInternetConnectionWithMessage()) {
            // When we have the network connectivity, trigger the [paginator] to load more [Post]s
            paginator.onNext(firstPostId to lastPostId)
        }
    }

}