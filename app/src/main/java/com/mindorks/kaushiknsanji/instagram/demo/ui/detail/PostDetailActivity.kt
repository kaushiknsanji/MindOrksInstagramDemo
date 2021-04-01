package com.mindorks.kaushiknsanji.instagram.demo.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityPostDetailBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo.ImmersivePhotoActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showAndEnableWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalListItemSpacingDecoration
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

    // ProgressTextDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var progressTextDialogSharedViewModel: ProgressTextDialogSharedViewModel

    // ConfirmOptionDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var confirmOptionDialogSharedViewModel: ConfirmOptionDialogSharedViewModel

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityPostDetailBinding::inflate)

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
        binding.toolbarPostDetail.apply {
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
        binding.rvPostDetailLikes.apply {
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
        binding.imagePostDetailPhoto.setOnClickListener { viewModel.onLaunchPhoto() }

        // Register click listener on the "No Likes Heart Image" to enable user to click and "Be the first to Like"
        binding.includePostDetailNoLikes.imageNoLikes.setOnClickListener { viewModel.onLikeAction() }

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
            binding.progressPostDetail.showWhen(started)
        }

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
                    progressTextDialogSharedViewModel.onProgressStatusChange(resourceWrapper)
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
            ImageUtils.loadImage(
                this,
                binding.imagePostDetailCreatorProfile,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.circleCropTransform(),
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_profile)
                ),
                defaultImageRes = R.drawable.ic_placeholder_profile,
                defaultImageRequestOptions = listOf(
                    RequestOptions.circleCropTransform()
                )
            )
        }

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        viewModel.postImage.observe(this) { image: Image ->
            ImageUtils.loadImage(
                this,
                binding.imagePostDetailPhoto,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)
                )
            )
        }

        // Register an observer on the Name LiveData to set its value on the corresponding textView
        viewModel.postCreatorName.observe(this) { name ->
            binding.textPostDetailCreatorName.text = name
        }

        // Register an observer on the Post Creation Time LiveData to set its value on the corresponding textView
        viewModel.postCreationTime.observe(this) { creationTime ->
            binding.textPostDetailTime.text = creationTime
        }

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        viewModel.likesCount.observe(this) { likesCount ->
            binding.textPostDetailLikeCount.text = resources.getQuantityString(
                R.plurals.label_post_detail_like_count,
                likesCount,
                likesCount
            )
        }

        // Register an observer on the Post Likes' Presence LiveData to set the visibility of views accordingly
        viewModel.postHasLikes.observe(this) { hasLikes: Boolean ->
            with(binding) {
                // When there are Likes, show the RecyclerView
                rvPostDetailLikes.showWhen(hasLikes)
                // When there are NO Likes, show the Empty view
                includePostDetailNoLikes.root.showWhen(!hasLikes)
            }
        }

        // Register an observer on the LiveData that determines whether the Post was created by the logged-in User,
        // in order to enable/disable the "Delete" menu button accordingly
        viewModel.postByUser.observe(this) { isUserPost: Boolean ->
            // Lookup for the Delete Menu item
            binding.toolbarPostDetail.menu.findItem(R.id.action_post_detail_delete)
                ?.let { menuItem: MenuItem ->
                    menuItem.showAndEnableWhen(isUserPost)
                }
        }

        // Register an observer on the User Like Menu button LiveData to change the "Heart" Image accordingly
        viewModel.hasUserLiked.observe(this) { hasLiked: Boolean ->
            // Lookup for the Heart Menu item
            binding.toolbarPostDetail.menu.findItem(R.id.action_post_detail_like)
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
        viewModel.launchDeletePostConfirm.observeEvent(this) { metadata: ConfirmOptionDialogMetadata ->
            // Show the Confirmation Dialog to the User for Delete Post request
            dialogManager.showDialog(
                ConfirmOptionDialogFragment::class.java,
                ConfirmOptionDialogFragment.Companion::newInstance
            )
            // Pass the metadata of the Dialog to be shown via its Shared ViewModel
            confirmOptionDialogSharedViewModel.onDialogMetadataChange(metadata)
        }

        // Register an observer on Delete Post confirmation - Positive response events
        confirmOptionDialogSharedViewModel.actionPositiveButton.observeEvent(this) {
            // Check if the Dialog Confirmation response is for DeletePost Dialog type
            if (confirmOptionDialogSharedViewModel.isDialogType(PostDetailViewModel.CONFIRM_OPTION_DIALOG_TYPE_DELETE_POST)) {
                // Delegate to the Primary ViewModel to begin the delete process
                viewModel.onDeleteConfirm()
            }
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
