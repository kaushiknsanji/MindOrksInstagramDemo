package com.mindorks.kaushiknsanji.instagram.demo.ui.profile


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeResource
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.TextAppearanceUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalGridItemSpacingDecoration
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

/**
 * [BaseFragment] subclass that inflates the layout 'R.layout.fragment_profile' to show a list of Posts
 * created by the logged-in User. Also, provides an option to the logged-in user to edit profile.
 * [ProfileViewModel] is the primary [androidx.lifecycle.ViewModel] of this Fragment.
 *
 * @author Kaushik N Sanji
 */
class ProfileFragment : BaseFragment<ProfileViewModel>(), ProfilePostsAdapter.Listener {

    // GridLayoutManager instance provided by Dagger
    @Inject
    lateinit var gridLayoutManager: GridLayoutManager

    // ProfilePostsAdapter instance provided by Dagger
    @Inject
    lateinit var profilePostsAdapter: ProfilePostsAdapter

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
    override fun provideLayoutId(): Int = R.layout.fragment_profile

    /**
     * Initializes the Layout of the Fragment.
     */
    override fun setupView(view: View, savedInstanceState: Bundle?) {

        // Setting up RecyclerView
        rv_profile_my_posts.apply {
            // Set the Layout Manager to GridLayoutManager
            layoutManager = gridLayoutManager.apply {
                // Set the Span Count
                spanCount = resources.getInteger(R.integer.profile_posts_grid_span)
            }
            // Set the Adapter for RecyclerView
            adapter = profilePostsAdapter
            // Add Grid Spacing Decoration
            resources.getDimensionPixelSize(R.dimen.profile_item_grid_spacing).let { gridSpace: Int ->
                addItemDecoration(VerticalGridItemSpacingDecoration(gridSpace, gridSpace))
            }
        }

        // Register Click Listener on "Edit Profile" button, to allow the logged-in user to edit their info
        button_profile_edit.setOnClickListener { viewModel.onEditProfile() }

        // Register Click Listener on "Logout" button, to allow the logged-in user to logout
        text_button_profile_logout.setOnClickListener { viewModel.onLogout() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Posts download and logout progress LiveData to show/hide the Progress horizontal
        viewModel.liveProgress.observe(this) { started: Boolean ->
            // Show the Progress horizontal when [started], else leave it hidden
            progress_horizontal_profile.showWhen(started)
        }

        // Register an observer on User's Posts LiveData to reload the adapter with the new List of Posts
        viewModel.userPosts.observeResource(this) { _, posts: List<Post>? ->
            posts?.run {
                // Load the Adapter with new data
                profilePostsAdapter.submitList(this)
            }
        }

        // Register an observer on the Name LiveData to set its value on the corresponding TextView
        viewModel.userName.observe(this) { userName: String ->
            text_profile_user_name.text = userName
        }

        // Register an observer on the Tagline LiveData to set its value on the corresponding TextView
        viewModel.userTagline.observe(this) { tagline: String ->
            text_profile_user_bio.apply {
                // When we have some value, show the View
                showWhen(tagline.isNotBlank().also { isTaglineNotBlank ->
                    if (isTaglineNotBlank) {
                        // Also, when we have some value, set its Text
                        text = tagline
                    }
                })
            }
        }

        // Register an observer on the User's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        viewModel.userImage.observe(this) { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(this@ProfileFragment) // Loading with Fragment reference
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
                    image_profile_user.run {
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
                glideRequest.into(image_profile_user)
            } ?: run {
                // Set default photo when there is no profile picture

                // Configuring Glide with relevant customizations and then setting the default photo
                GlideApp
                    .with(this@ProfileFragment) // Loading with Fragment reference
                    .load(
                        ContextCompat.getDrawable(
                            this@ProfileFragment.requireContext(),
                            R.drawable.ic_placeholder_profile
                        )
                    ) // Loading the default Image
                    .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                    .into(image_profile_user) // Load into the corresponding ImageView
            }
        }

        // Register an observer on the User's Post Count LiveData to set its value on the corresponding TextView
        viewModel.userPostsCount.observe(this) { postsCount: Int ->
            TextAppearanceUtils.setHtmlText(
                text_profile_post_count,
                resources.getString(
                    R.string.label_profile_post_count, postsCount
                )
            )
        }

        // Register an observer on the User's Posts Presence LiveData to set the visibility of views accordingly
        viewModel.userPostsEmpty.observe(this) { empty: Boolean ->
            // When there are Posts, show the RecyclerView
            rv_profile_my_posts.showWhen(!empty)
            // When there are NO Posts, show the Empty View
            group_profile_no_posts.showWhen(empty)
        }

        // Register an observer for EditProfileActivity launch events
        viewModel.launchEditProfile.observeEvent(this) {
            // Delegate to the MainActivity via the Shared ViewModel
            mainSharedViewModel.onEditProfileRequest()
        }

        // Register an observer on the Post creation LiveData to add the New Post to the top of the List
        mainSharedViewModel.postPublishUpdateToProfile.observeEvent(this) { newPost: Post ->
            // Delegate to the ViewModel to handle
            viewModel.onNewPost(newPost)
        }

        // Register an observer for Profile information update events
        mainSharedViewModel.userProfileInfoUpdateToProfile.observeEvent(this) { reload: Boolean ->
            if (reload) {
                // On reload, delegate to the ViewModel to refresh User information
                viewModel.onRefreshUserInfo()
            }
        }

        // Register an observer for LoginActivity launch events
        viewModel.launchLogin.observeEvent(this) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish() //Terminate this activity
        }

        // Register an observer for Post Deleted events
        mainSharedViewModel.postDeletedEventToProfile.observeEvent(this) { postId: String ->
            // Delegate to the ViewModel to remove the Post from the list
            viewModel.onPostDeleted(postId)
        }

    }

    /**
     * Callback Method of [com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter.DefaultListener] invoked
     * when the user clicks on the Adapter Item.
     *
     * @param itemData Data of the Adapter Item which is an instance of [Post].
     */
    override fun onItemClick(itemData: Post) {
        // Delegate to the MainActivity via the Shared ViewModel
        mainSharedViewModel.onPostItemClick(itemData)
    }

    companion object {

        // Constant used as Fragment Tag and also for logs
        const val TAG = "ProfileFragment"

        /**
         * Factory method to create a new instance of this fragment.
         *
         * @return A new instance of fragment [ProfileFragment].
         */
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle()
            }
    }
}
