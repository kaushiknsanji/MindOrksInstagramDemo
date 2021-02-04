package com.mindorks.kaushiknsanji.instagram.demo.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.SharedConfirmOptionDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.SharedProgressTextViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo.ImmersivePhotoActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalListItemSpacingDecoration
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.layout_no_likes.*
import java.io.Serializable
import javax.inject.Inject

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_post_detail' to show the details screen for the Post launched.
 * [PostDetailViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class PostDetailActivity : BaseActivity<PostDetailViewModel>() {

    // LinearLayoutManager instance provided by Dagger
    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    // PostLikesAdapter instance provided by Dagger
    @Inject
    lateinit var postLikesAdapter: PostLikesAdapter

    // SharedProgressTextViewModel instance provided by Dagger
    @Inject
    lateinit var sharedProgressTextViewModel: SharedProgressTextViewModel

    // SharedConfirmOptionDialogViewModel instance provided by Dagger
    @Inject
    lateinit var sharedConfirmOptionDialogViewModel: SharedConfirmOptionDialogViewModel

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_post_detail

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Ensure the activity is started with the required Post ID and then load the corresponding Post details
        intent.getStringExtra(EXTRA_POST_ID)!!.let { postId: String -> viewModel.onLoadPostDetails(postId) }

        // Initialize Toolbar
        toolbar_post_detail.apply {
            // Set Title
            title = getString(R.string.title_post_detail_toolbar)
            // Set Previous Icon as the Navigation Icon
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_prev)
            // Register click listener on the Up button of the toolbar
            setNavigationOnClickListener { viewModel.onNavigateUp() }

            // Inflate the Toolbar menu
            inflateMenu(R.menu.toolbar_menu_post_detail)
            // Set click listener on Menu items
            setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    // For "Like/Unlike" button
                    R.id.action_post_detail_like -> {
                        viewModel.onLikeAction()
                        return@setOnMenuItemClickListener true
                    }
                    // For "Delete" button
                    R.id.action_post_detail_delete -> {
                        viewModel.onDeleteRequest()
                        return@setOnMenuItemClickListener true
                    }
                    else -> false
                }
            }
        }

        // Setting up RecyclerView
        rv_post_detail_likes.apply {
            // Set the Layout Manager to LinearLayoutManager
            layoutManager = linearLayoutManager
            // Set the Adapter for RecyclerView
            adapter = postLikesAdapter
            // Add List Spacing Decoration
            addItemDecoration(
                VerticalListItemSpacingDecoration(
                    resources.getDimensionPixelSize(R.dimen.all_list_items_spacing),
                    0,
                    resources.getDimensionPixelSize(R.dimen.post_detail_likes_list_bottom_offset)
                )
            )
        }

        // Register click listener on Post Photo
        image_post_detail_photo.setOnClickListener { viewModel.onLaunchPhoto() }

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
        viewModel.loadingProgress.observe(this) { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_post_detail.setVisibility(started)
        })

        // Register an observer on Delete progress LiveData to show/hide the Delete Progress Dialog
        viewModel.deleteProgress.observe(this) { resourceWrapper ->
            // Show/hide the Progress Dialog based on the Resource Status
            when (resourceWrapper.status) {
                Status.LOADING -> {
                    // When Loading, show the Progress Dialog immediately and update the Status Text via its ViewModel
                    dialogManager.showDialogNow(
                        ProgressTextDialogFragment::class.java,
                        ProgressTextDialogFragment.Companion::newInstance
                    )
                    // Update the Status Text
                    sharedProgressTextViewModel.onProgressStatusChange(resourceWrapper)
                }
                else -> {
                    // When not loading, dismiss any active dialog
                    dialogManager.dismissActiveDialog()
                }
            }
        }

        // Register an observer on the Post Likes LiveData to load the Adapter with the List of all Post Likes
        viewModel.postLikes.observeNonNull(this) { likes: List<Post.User> ->
            // Load the Adapter with new data
            postLikesAdapter.submitList(likes)
        }

        // Register an observer on the Post Creator's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.profileImage.observe(this) { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(this@PostDetailActivity) // Loading with Activity's context
                    .load(
                        GlideHelper.getProtectedUrl(
                            image.url,
                            image.headers
                        )
                    ) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_profile)) // Loading with PlaceHolder Image

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    image_post_detail_creator_profile.run {
                        val params = layoutParams as ViewGroup.LayoutParams
                        params.width = placeHolderWidth
                        params.height = placeHolderHeight
                        layoutParams = params
                    }

                    // Override the dimensions of the Image (downloaded) to placeholder dimensions
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeHolderWidth, placeHolderHeight))
                }

                // Start the download and load into the corresponding ImageView
                glideRequest.into(image_post_detail_creator_profile)
            } ?: run {
                // Set default photo when there is no profile picture

                // Configuring Glide with relevant customizations and then setting the default photo
                GlideApp
                    .with(this) // Loading with Activity's context
                    .load(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_placeholder_profile
                        )
                    ) // Loading the default Image
                    .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                    .into(image_post_detail_creator_profile) // Load into the corresponding ImageView
            }
        }

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        viewModel.postImage.observe(this) { image: Image ->
            image.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(this@PostDetailActivity) // Loading with Activity's context
                    .load(
                        GlideHelper.getProtectedUrl(
                            image.url,
                            image.headers
                        )
                    ) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)) // Loading with PlaceHolder Image

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    image_post_detail_photo.run {
                        val params = layoutParams as ViewGroup.LayoutParams
                        params.width = placeHolderWidth
                        params.height = placeHolderHeight
                        layoutParams = params
                    }

                    // Override the dimensions of the Image (downloaded) to placeholder dimensions
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeHolderWidth, placeHolderHeight))
                }

                // Start the download and load into the corresponding ImageView
                glideRequest.into(image_post_detail_photo)
            }
        }

        // Register an observer on the Name LiveData to set its value on the corresponding textView
        viewModel.postCreatorName.observe(this) { name ->
            text_post_detail_creator_name.text = name
        }

        // Register an observer on the Post Creation Time LiveData to set its value on the corresponding textView
        viewModel.postCreationTime.observe(this) { creationTime ->
            text_post_detail_time.text = creationTime
        }

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        viewModel.likesCount.observe(this) { likesCount ->
            text_post_detail_like_count.text = getString(
                R.string.label_home_item_post_like_count,
                likesCount
            )
        }

        // Register an observer on the Post Likes' Presence LiveData to set the visibility of views accordingly
        viewModel.postHasLikes.observe(this, Observer { hasLikes: Boolean ->
            if (hasLikes) {
                // When there are Likes, show the RecyclerView and hide the Empty view
                rv_post_detail_likes.visibility = View.VISIBLE
                include_post_detail_no_likes.visibility = View.GONE
            } else {
                // When there are NO Likes, show the Empty view and hide the RecyclerView
                rv_post_detail_likes.visibility = View.GONE
                include_post_detail_no_likes.visibility = View.VISIBLE
            }
        })

        // Register an observer on the LiveData that determines whether the Post was created by the logged-in User,
        // in order to enable/disable the "Delete" menu button accordingly
        viewModel.postByUser.observe(this) { isUserPost: Boolean ->
            // Lookup for the Delete Menu item
            toolbar_post_detail.menu.findItem(R.id.action_post_detail_delete)?.let { menuItem: MenuItem ->
                if (isUserPost) {
                    // If this is User's Post, then make the Menu Item visible and enabled
                    menuItem.isVisible = true
                    menuItem.isEnabled = true
                } else {
                    // If this is Not User's Post, then hide the Menu Item and make it disabled
                    menuItem.isVisible = false
                    menuItem.isEnabled = false
                }
            }
        })

        // Register an observer on the User Like Menu button LiveData to change the "Heart" Image accordingly
        viewModel.hasUserLiked.observe(this) { hasLiked: Boolean ->
            // Lookup for the Heart Menu item
            toolbar_post_detail.menu.findItem(R.id.action_post_detail_like)
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

        // Register an observer on Delete Post confirmation Request events
        viewModel.launchDeletePostConfirm.observeResourceEvent(this) { _, titleResId: Int ->
            // Show the Confirmation Dialog to the User for Delete Post request
            dialogManager.showDialog(
                ConfirmOptionDialogFragment::class.java,
                ConfirmOptionDialogFragment.Companion::newInstance
            )
            // Pass the Dialog Title to be shown via its Shared ViewModel
            sharedConfirmOptionDialogViewModel.onDialogTitleTextChange(Resource.Success(titleResId))
        }

        // Register an observer on Delete Post confirmation - Positive response events
        sharedConfirmOptionDialogViewModel.actionPositiveButton.observeEvent(this) {
            // Delegate to the Primary ViewModel to begin the delete process
            viewModel.onDeleteConfirm()
        }

        // Register an observer to return to the Calling Activity with the status of User's Like on the Post
        viewModel.navigateParentWithLikeStatus.observeEvent(this) { intentMap: Map<String, Serializable> ->
            // Set the Result
            setResult(
                RESULT_LIKE_POST,
                Intent().putExtrasFromMap(intentMap)
            )
            // Navigate back to parent
            NavUtils.navigateUpFromSameTask(this)
        }

        // Register an observer to return to the Calling Activity with the Post Delete Success result
        viewModel.navigateParentWithDeleteSuccess.observeEvent(this) { intentMap: Map<String, Serializable> ->
            // Set the Result
            setResult(
                RESULT_DELETE_POST_SUCCESS,
                Intent().putExtrasFromMap(intentMap)
            )
            // Navigate back to parent
            NavUtils.navigateUpFromSameTask(this)
        }

        // Register an observer for ImmersivePhotoActivity launch events
        viewModel.launchImmersivePhoto.observeEvent(this) { intentMap: Map<String, Serializable> ->
            startActivity(
                Intent(this, ImmersivePhotoActivity::class.java).putExtrasFromMap(intentMap)
            )
        }
    }

    /**
     * Takes care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        // Delegate to the ViewModel to handle the back key navigation
        viewModel.onNavigateUp()
    }

    companion object {
        // Intent Extra constant for the Id of the Post whose details are to be loaded
        @JvmField
        val EXTRA_POST_ID = PostDetailActivity::class.java.`package`?.toString() + "extra.POST_ID"

        // Request code used by the activity that calls this activity for result
        const val REQUEST_POST_DETAIL = 300 // 301, 302 are reserved for the result of this request

        // Custom Result code for Like operation
        const val RESULT_LIKE_POST = REQUEST_POST_DETAIL + Activity.RESULT_FIRST_USER

        // Custom Result code for Successful Delete operation
        const val RESULT_DELETE_POST_SUCCESS = RESULT_LIKE_POST + Activity.RESULT_FIRST_USER

        // Intent Extra constant for passing the Response message of the Successful Delete operation
        @JvmField
        val EXTRA_RESULT_DELETE_POST_SUCCESS =
            PostDetailActivity::class.java.`package`?.toString() + "extra.DELETE_SUCCESS"

        // Intent Extra constant for passing the Id of Deleted Post on Successful Delete operation
        @JvmField
        val EXTRA_RESULT_DELETED_POST_ID =
            PostDetailActivity::class.java.`package`?.toString() + "extra.DELETED_POST_ID"

        // Intent Extra constant for passing the Id of the Post Liked/Unliked
        @JvmField
        val EXTRA_RESULT_LIKE_POST_ID =
            PostDetailActivity::class.java.`package`?.toString() + "extra.LIKE_POST_ID"

        // Intent Extra constant for passing the Like status of the Post
        @JvmField
        val EXTRA_RESULT_LIKE_POST_STATE =
            PostDetailActivity::class.java.`package`?.toString() + "extra.LIKE_POST_STATE"
    }

}
