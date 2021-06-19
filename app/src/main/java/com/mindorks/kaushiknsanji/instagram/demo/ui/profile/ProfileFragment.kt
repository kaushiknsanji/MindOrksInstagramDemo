package com.mindorks.kaushiknsanji.instagram.demo.ui.profile


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.FragmentProfileBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostsAdapter
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.TextAppearanceUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.VerticalGridItemSpacingDecoration
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

    // ConfirmOptionDialogSharedViewModel instance provided by Dagger
    @Inject
    lateinit var confirmOptionDialogSharedViewModel: ConfirmOptionDialogSharedViewModel

    // ViewBinding instance for this Fragment
    private val binding by viewBinding(FragmentProfileBinding::bind)

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
        binding.rvProfileMyPosts.apply {
            // Set the Layout Manager to GridLayoutManager
            layoutManager = gridLayoutManager.apply {
                // Set the Span Count
                spanCount = resources.getInteger(R.integer.profile_posts_grid_span)
            }
            // Set the Adapter for RecyclerView
            adapter = profilePostsAdapter
            // Add Grid Spacing Decoration
            resources.getDimensionPixelSize(R.dimen.profile_item_grid_spacing)
                .let { gridSpace: Int ->
                    addItemDecoration(VerticalGridItemSpacingDecoration(gridSpace, gridSpace))
            }
        }

        // Register Click Listener on "Edit Profile" button, to allow the logged-in user to edit their info
        binding.buttonProfileEdit.setOnClickListener { viewModel.onEditProfile() }

        // Register Click Listener on "Logout" button, to allow the logged-in user to logout
        binding.textButtonProfileLogout.setOnClickListener { viewModel.onLogoutRequest() }
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
            binding.progressHorizontalProfile.showWhen(started)
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
            binding.textProfileUserName.text = userName
        }

        // Register an observer on the Tagline LiveData to set its value on the corresponding TextView
        viewModel.userTagline.observe(this) { tagline: String ->
            binding.textProfileUserBio.apply {
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
            ImageUtils.loadImage(
                this@ProfileFragment.requireContext(),
                binding.imageProfileUser,
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

        // Register an observer on the User's Post Count LiveData to set its value on the corresponding TextView
        viewModel.userPostsCount.observe(this) { postsCount: Int ->
            TextAppearanceUtils.setHtmlText(
                binding.textProfilePostCount,
                resources.getQuantityString(
                    R.plurals.label_profile_post_count,
                    postsCount,
                    postsCount
                )
            )
        }

        // Register an observer on the User's Posts Presence LiveData to set the visibility of views accordingly
        viewModel.userPostsEmpty.observe(this) { empty: Boolean ->
            with(binding) {
                // When there are Posts, show the RecyclerView
                rvProfileMyPosts.showWhen(!empty)
                // When there are NO Posts, show the Empty View
                groupProfileNoPosts.showWhen(empty)
            }
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

        // Register an observer on Logout confirmation Request events
        viewModel.launchLogoutConfirm.observeEvent(this) { metadata: ConfirmOptionDialogMetadata ->
            // Show the Confirmation Dialog to the User for Logout Request
            dialogManager.showDialog(
                ConfirmOptionDialogFragment::class.java,
                ConfirmOptionDialogFragment.Companion::newInstance
            )
            // Pass the metadata of the Dialog to be shown via its Shared ViewModel
            confirmOptionDialogSharedViewModel.onDialogMetadataChange(metadata)
        }

        // Register an observer on Logout confirmation - Positive response events
        confirmOptionDialogSharedViewModel.actionPositiveButton.observeEvent(this) {
            // Check if the Dialog Confirmation response is for Logout Dialog type
            if (confirmOptionDialogSharedViewModel.isDialogType(ProfileViewModel.CONFIRM_OPTION_DIALOG_TYPE_LOGOUT)) {
                // Delegate to the Primary ViewModel to begin the Logout process
                viewModel.onLogoutConfirm()
            }
        }

        // Register an observer for LoginActivity launch events
        viewModel.launchLogin.observeEvent(this) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish() //Terminate this activity
        }

        // Register an observer for Post Deleted events
        mainSharedViewModel.postDeletedEventToProfile.observeEvent(this) {
            // Delegate to the ViewModel to remove the Post from the list
            viewModel.onPostDeleted()
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
