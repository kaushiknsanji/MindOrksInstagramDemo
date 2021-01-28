package com.mindorks.kaushiknsanji.instagram.demo.ui.home


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts.PostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalListItemSpacingDecoration
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * [BaseFragment] subclass that inflates the layout 'R.layout.fragment_home' to show a feed of Instagram Posts
 * from all users.
 * [HomeViewModel] is the primary [androidx.lifecycle.ViewModel] of this Fragment.
 *
 * @author Kaushik N Sanji
 */
class HomeFragment : BaseFragment<HomeViewModel>(), PostsAdapter.Listener {

    // LinearLayoutManager instance provided by Dagger
    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    // PostsAdapter instance provided by Dagger
    @Inject
    lateinit var postsAdapter: PostsAdapter

    // Instance of the ViewModel shared with the MainActivity provided by Dagger
    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    /**
     * Injects dependencies exposed by [FragmentComponent] into Fragment.
     */
    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Fragment.
     */
    override fun provideLayoutId(): Int = R.layout.fragment_home

    /**
     * Initializes the Layout of the Fragment.
     */
    override fun setupView(view: View, savedInstanceState: Bundle?) {

        // Setting up RecyclerView
        rv_home_posts.apply {
            // Set the Layout Manager to LinearLayoutManager
            layoutManager = linearLayoutManager
            // Set the Adapter for RecyclerView
            adapter = postsAdapter
            // Add List Spacing Decoration
            addItemDecoration(
                VerticalListItemSpacingDecoration(
                    resources.getDimensionPixelSize(R.dimen.home_item_list_spacing)
                )
            )
            // Register OnScrollListener to load more data when the user scrolls to the bottom of the list
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                /**
                 * Callback method to be invoked when the RecyclerView has been scrolled. This will be
                 * called after the scroll has completed.
                 *
                 * This callback will also be called if visible item range changes after a layout
                 * calculation. In that case, dx and dy will be 0.
                 *
                 * @param recyclerView The RecyclerView which scrolled.
                 * @param dx The amount of horizontal scroll.
                 * @param dy The amount of vertical scroll.
                 */
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    (layoutManager as? LinearLayoutManager)?.run {
                        if (findLastVisibleItemPosition() + childCount >= itemCount) {
                            // When the last Post in the list is reached, delegate to the ViewModel to load more
                            viewModel.onLoadMore()
                        }
                    }
                }
            })
        }

    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Posts download progress to show/hide the Progress horizontal
        viewModel.loadingProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress horizontal when [started], else leave it hidden
            progress_horizontal_home.setVisibility(started)
        })

        // Register an observer on the Post creation LiveData to add the New Post to the top of the List
        mainSharedViewModel.postPublishUpdateToHome.observeEvent(this) { newPost: Post ->
            // Delegate to the ViewModel to handle
            viewModel.onNewPost(newPost)
        }

        // Register an observer on the List of All Posts LiveData to reload the adapter
        // with the list of All Posts
        viewModel.reloadAllPosts.observeResource(this) { _, posts: List<Post>? ->
            posts?.run {
                // Load the Adapter with the new data
                postsAdapter.submitList(this)
            }
        }

        // Register an observer on the Scroll Event to scroll to the Top of the List
        viewModel.scrollToTop.observeEvent(this) { scroll: Boolean ->
            if (scroll) {
                // Scroll immediately to the top of the RecyclerView
                rv_home_posts.scrollToPosition(0)
            }
        }

        // Register an observer for Profile information update events
        mainSharedViewModel.userProfileInfoUpdateToHome.observeEvent(this) { reload: Boolean ->
            if (reload) {
                // On reload, delegate to the ViewModel to refresh logged-in User information on User's activity
                viewModel.onRefreshUserInfo()
            }
        }

        // Register an observer for Post Deleted events
        mainSharedViewModel.postDeletedEventToHome.observeEvent(this) { postId: String ->
            // Delegate to the ViewModel to remove the Post from the list
            viewModel.onPostDeleted(postId)
        }

        // Register an observer for Post Like Update events
        mainSharedViewModel.postLikeUpdateToHome.observeEvent(this) { (postId: String, likeStatus: Boolean) ->
            // Delegate to the ViewModel to refresh the Post Item with new Like Status
            viewModel.onPostLikeUpdated(postId, likeStatus)
        }
    }

    /**
     * Callback Method of [PostsAdapter.Listener] invoked when the user clicks on the TextView that
     * displays the number of Likes on the Post.
     *
     * @param itemData Data of the Adapter Item which is an instance of [Post].
     */
    override fun onLikesCountClick(itemData: Post) {
        // Delegate to the MainActivity via the Shared ViewModel
        mainSharedViewModel.onPostLikesCountClick(itemData)
    }

    /**
     * Callback Method of [com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter.DefaultListener] invoked when
     * the user clicks on the Adapter Item.
     *
     * @param itemData Data of the Adapter Item which is an instance of [Post].
     */
    override fun onItemClick(itemData: Post) {
        // Delegate to the MainActivity via the Shared ViewModel
        mainSharedViewModel.onPostItemClick(itemData)
    }

    /**
     * Callback Method of [PostsAdapter.Listener] invoked when the user likes/unlikes the Post.
     *
     * @param itemData The updated data of Adapter Item which is an instance of [Post].
     */
    override fun onLikeUnlikeSync(itemData: Post) {
        // Delegate to the ViewModel to sync up the change
        viewModel.onLikeUnlikeSync(itemData)
    }

    companion object {

        // Constant used as Fragment Tag and also for logs
        const val TAG = "HomeFragment"

        /**
         * Factory method to create a new instance of this fragment.
         *
         * @return A new instance of fragment [HomeFragment].
         */
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle()
            }
    }

}