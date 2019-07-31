package com.mindorks.kaushiknsanji.instagram.demo.ui.like

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.ThemeUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalListItemSpacingDecoration
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
import kotlinx.android.synthetic.main.activity_post_like.*
import kotlinx.android.synthetic.main.layout_no_likes.*
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

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_post_like

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {
        // Ensure the activity is started with the required Post ID and then load the corresponding Post details
        intent.getStringExtra(EXTRA_POST_ID)!!.let { postId: String -> viewModel.onLoadPostDetails(postId) }

        // Initialize Toolbar
        toolbar_post_like.apply {
            // Set Title
            title = getString(R.string.title_post_like_toolbar)
            // Set Close Icon as the Navigation Icon
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_close)
            // Register click listener on close button of the toolbar
            setNavigationOnClickListener { viewModel.onClose() }

            // Inflate the Toolbar menu
            inflateMenu(R.menu.menu_post_like)
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
        rv_post_likes.apply {
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
        image_no_likes.setOnClickListener { viewModel.onLikeAction() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Post data loading progress to show/hide the Progress Circle
        viewModel.loadingProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_post_like.setVisibility(started)
        })

        // Register an observer on the Post Likes LiveData to load the Adapter with the List of all Post Likes
        viewModel.postLikes.observe(this, Observer { likes: List<Post.User>? ->
            likes?.run {
                // Reset the Adapter data with the new data (as the logged-in User can like/unlike on this screen)
                postLikesAdapter.resetData(this)
            }
        })

        // Register an observer on the Post Likes' Presence LiveData to set the visibility of views accordingly
        viewModel.postHasLikes.observe(this, Observer { hasLikes: Boolean ->
            if (hasLikes) {
                // When there are Likes, show the RecyclerView and the Bottom App Bar, and hide the Empty view
                rv_post_likes.visibility = View.VISIBLE
                include_post_like_empty.visibility = View.GONE
                bottom_app_bar_post_like.visibility = View.VISIBLE
            } else {
                // When there are NO Likes, show the Empty view, and hide the RecyclerView and the Bottom App Bar
                rv_post_likes.visibility = View.GONE
                include_post_like_empty.visibility = View.VISIBLE
                bottom_app_bar_post_like.visibility = View.GONE
            }
        })

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        viewModel.likesCount.observe(this, Observer { likesCount ->
            text_post_like_count.text = likesCount.toString()
        })

        // Register an observer on the User Like Menu button LiveData to change the "Heart" Image accordingly
        viewModel.hasUserLiked.observe(this, Observer { hasLiked: Boolean ->
            // Lookup for the Heart Menu item
            toolbar_post_like.menu.findItem(R.id.action_post_like)?.let { menuItem: MenuItem ->
                // Change the Heart Icon based on whether User had liked the post or not
                if (hasLiked) {
                    menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_heart_selected)
                } else {
                    menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_heart_unselected)
                }
            }
        })

        // Register an observer for close action events, to close this Activity
        viewModel.closeAction.observeEvent(this) { close: Boolean ->
            if (close) {
                // Terminate the Activity on [close]
                finish()
            }
        }
    }

    companion object {
        // Intent Extra constant for the Id of the Post whose details are to be loaded
        @JvmField
        val EXTRA_POST_ID = PostLikeActivity::class.java.`package`.toString() + "extra.POST_ID"
    }

}