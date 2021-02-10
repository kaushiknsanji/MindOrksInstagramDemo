package com.mindorks.kaushiknsanji.instagram.demo.ui.like

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityPostLikeBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeNonNull
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.putExtrasFromMap
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ThemeUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalListItemSpacingDecoration
import java.io.Serializable
import javax.inject.Inject

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_post_like' to show the list of Users who
 * have Liked, for the Post launched.
 * [PostLikeViewModel] is the Primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class PostLikeActivity : BaseActivity<PostLikeViewModel>() {

    // LinearLayoutManager instance provided by Dagger
    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    // PostLikesAdapter instance provided by Dagger
    @Inject
    lateinit var postLikesAdapter: PostLikesAdapter

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityPostLikeBinding::inflate)

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the [Root View][View] for the Activity
     * inflated using `Android ViewBinding`.
     */
    override fun provideContentView(): View = binding.root

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {
        // Ensure the activity is started with the required Post ID and then load the corresponding Post details
        intent.getStringExtra(EXTRA_POST_ID)!!.let { postId: String -> viewModel.onLoadPostDetails(postId) }

        // Initialize Toolbar
        binding.toolbarPostLike.apply {
            // Set Title
            title = getString(R.string.title_post_like_toolbar)
            // Set Close Icon as the Navigation Icon
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_close)
            // Register click listener on close button of the toolbar
            setNavigationOnClickListener { viewModel.onClose() }

            // Inflate the Toolbar menu
            inflateMenu(R.menu.toolbar_menu_post_like)
            // Set click listener on Menu items
            setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    // For "Like/Unlike" button
                    R.id.action_post_like -> {
                        viewModel.onLikeAction()
                        return@setOnMenuItemClickListener true
                    }
                    else -> false
                }
            }
        }

        // Setting up RecyclerView
        binding.rvPostLikes.apply {
            // Set the Layout Manager to LinearLayoutManager
            layoutManager = linearLayoutManager
            // Set the Adapter for RecyclerView
            adapter = postLikesAdapter
            // Add List Spacing Decoration
            addItemDecoration(
                VerticalListItemSpacingDecoration(
                    resources.getDimensionPixelSize(R.dimen.all_list_items_spacing),
                    0,
                    ThemeUtils.getThemedActionBarHeightInPixels(context.applicationContext)
                )
            )
        }

        // Register click listener on the "No Likes Heart Image" to enable user to click and "Be the first to Like"
        binding.includePostLikeEmpty.imageNoLikes.setOnClickListener { viewModel.onLikeAction() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Post data loading progress to show/hide the Progress Circle
        viewModel.loadingProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            binding.progressPostLike.showWhen(started)
        }

        // Register an observer on the Post Likes LiveData to load the Adapter with the List of all Post Likes
        viewModel.postLikes.observeNonNull(this) { likes: List<Post.User> ->
            // Load the Adapter with new data
            postLikesAdapter.submitList(likes)
        }

        // Register an observer on the Post Likes' Presence LiveData to set the visibility of views accordingly
        viewModel.postHasLikes.observe(this) { hasLikes: Boolean ->
            with(binding) {
                // When there are Likes, show the RecyclerView and the Bottom App Bar
                rvPostLikes.showWhen(hasLikes)
                bottomAppBarPostLike.showWhen(hasLikes)
                // When there are NO Likes, show the Empty view
                includePostLikeEmpty.root.showWhen(!hasLikes)
            }
        }

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        viewModel.likesCount.observe(this) { likesCount ->
            binding.textPostLikeCount.text = likesCount.toString()
        }

        // Register an observer on the User Like Menu button LiveData to change the "Heart" Image accordingly
        viewModel.hasUserLiked.observe(this) { hasLiked: Boolean ->
            // Lookup for the Heart Menu item
            binding.toolbarPostLike.menu.findItem(R.id.action_post_like)
                ?.let { menuItem: MenuItem ->
                    // Change the Heart Icon based on whether User had liked the post or not
                    if (hasLiked) {
                        menuItem.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_heart_selected)
                    } else {
                        menuItem.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_heart_unselected)
                    }
                }
        }

        // Register an observer for close action events, to close this Activity and update the Calling Activity
        // with the status of User's Like on the Post
        viewModel.closeWithLikeStatus.observeEvent(this) { intentMap: Map<String, Serializable> ->
            // Set the Result
            setResult(
                RESULT_LIKE_POST,
                Intent().putExtrasFromMap(intentMap)
            )
            // Terminate the Activity
            finish()
        }
    }

    /**
     * Takes care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        // Delegate to the ViewModel to handle the back key navigation
        viewModel.onClose()
    }

    companion object {
        // Intent Extra constant for the Id of the Post whose details are to be loaded
        @JvmField
        val EXTRA_POST_ID = PostLikeActivity::class.java.`package`?.toString() + "extra.POST_ID"

        // Request code used by the activity that calls this activity for result
        const val REQUEST_POST_LIKE = 400 // 401 is reserved for the result of this request

        // Custom Result code for Like operation
        const val RESULT_LIKE_POST = REQUEST_POST_LIKE + Activity.RESULT_FIRST_USER

        // Intent Extra constant for passing the Id of the Post Liked/Unliked
        @JvmField
        val EXTRA_RESULT_LIKE_POST_ID =
            PostLikeActivity::class.java.`package`?.toString() + "extra.LIKE_POST_ID"

        // Intent Extra constant for passing the Like status of the Post
        @JvmField
        val EXTRA_RESULT_LIKE_POST_STATE =
            PostLikeActivity::class.java.`package`?.toString() + "extra.LIKE_POST_STATE"
    }

}