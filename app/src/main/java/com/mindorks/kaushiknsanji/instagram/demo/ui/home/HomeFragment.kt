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
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * [BaseFragment] subclass that inflates the layout 'R.layout.fragment_home' to show a feed of Instagram Posts
 * from all users.
 * [HomeViewModel] is the primary [androidx.lifecycle.ViewModel] of this Fragment.
 *
 * @author Kaushik N Sanji
 */
class HomeFragment : BaseFragment<HomeViewModel>() {

    // LinearLayoutManager instance provided by Dagger
    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    // PostsAdapter instance provided by Dagger
    @Inject
    lateinit var postsAdapter: PostsAdapter

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

        // Register an observer on Paginated Posts LiveData to load the adapter with new list of Posts
        viewModel.paginatedPosts.observe(this, Observer { resourceWrapper: Resource<List<Post>> ->
            resourceWrapper.data?.run {
                postsAdapter.appendMore(this)
            }
        })

        // Register an observer on Posts download progress to show/hide the Progress horizontal
        viewModel.loadingProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress horizontal when [started], else leave it hidden
            progress_horizontal_home.visibility = if (started) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

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