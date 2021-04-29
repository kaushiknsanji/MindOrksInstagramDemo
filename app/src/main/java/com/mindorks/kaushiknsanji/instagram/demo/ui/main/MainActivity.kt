package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityMainBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import java.io.Serializable
import javax.inject.Inject

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_main' to show the Main screen
 * with Bottom Navigation View for [HomeFragment], [PhotoFragment] and [ProfileFragment].
 * [MainViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val TAG = "MainActivity"
    }

    // Instance of the ViewModel shared with the Bottom Navigation Fragments provided by Dagger
    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityMainBinding::inflate)

    // Saves the fragment instance being shown
    private var activeFragment: Fragment? = null

    // Activity Result observer to execute activity call contracts
    // and handle the results in a separate class
    private val activityResultObserver: MainActivityResultObserver by lazy {
        MainActivityResultObserver(activityResultRegistry, viewModel, mainSharedViewModel)
    }

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

        // Register a navigation listener on the Bottom Navigation Menu Items
        binding.bottomNavMain.run {
            // Clearing icon tinting to fix icon distortion
            itemIconTintList = null
            // Registering the listener
            setOnNavigationItemSelectedListener { menuItem ->
                // Defining action based on the MenuItem clicked
                when (menuItem.itemId) {

                    // For the Home Menu button
                    R.id.action_main_bottom_nav_home -> {
                        // Delegate to the ViewModel to handle
                        viewModel.onHomeSelected()
                        // Returning true as the click is handled
                        true
                    }

                    // For the "Add Photos" Menu button
                    R.id.action_main_bottom_nav_photo -> {
                        // Delegate to the ViewModel to handle
                        viewModel.onPhotoSelected()
                        // Returning true as the click is handled
                        true
                    }

                    // For the Profile Menu button
                    R.id.action_main_bottom_nav_profile -> {
                        // Delegate to the ViewModel to handle
                        viewModel.onProfileSelected()
                        // Returning true as the click is handled
                        true
                    }

                    // On all else, returning false
                    else -> false
                }
            }
        }

        // Restore the last active fragment instance if any, from the FragmentManager to prevent possible overlap
        if (activeFragment == null) {
            activeFragment = supportFragmentManager.fragments.takeIf { it.size > 0 }?.find { fragment: Fragment ->
                // Fragment to restore will be our container fragment that is added, not yet hidden but is not visible
                fragment.id == R.id.container_main_nav_fragment && fragment.isAdded && !fragment.isVisible && !fragment.isHidden
            }
        }

    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer for Activity results
        lifecycle.addObserver(activityResultObserver)

        // Register an observer for HomeFragment navigation events
        viewModel.navigateHome.observeEvent(this) {
            showHome()
        }

        // Register an observer for PhotoFragment navigation events
        viewModel.navigatePhoto.observeEvent(this) {
            showAddPhoto()
        }

        // Register an observer for ProfileFragment navigation events
        viewModel.navigateProfile.observeEvent(this) {
            showProfile()
        }

        // Register an observer for HomeFragment redirection events
        mainSharedViewModel.redirectHome.observeEvent(this) {
            // Select programmatically the Home button of the Bottom Navigation
            binding.bottomNavMain.selectedItemId = R.id.action_main_bottom_nav_home
        }

        // Register an observer for EditProfileActivity launch events
        mainSharedViewModel.launchEditProfile.observeEvent(this) {
            // Start EditProfileActivity for results
            activityResultObserver.launchEditProfile()
        }

        // Register an observer for PostDetailActivity launch events
        mainSharedViewModel.launchPostDetail.observeEvent(this) { intentMap: Map<String, Serializable> ->
            // Start PostDetailActivity for results
            activityResultObserver.launchPostDetail(intentMap)
        }

        // Register an observer for PostLikeActivity launch events
        mainSharedViewModel.launchPostLike.observeEvent(this) { intentMap: Map<String, Serializable> ->
            // Start PostLikeActivity for results
            activityResultObserver.launchPostLike(intentMap)
        }
    }

    /**
     * Called when the Home button of the Bottom Navigation Menu is clicked.
     * Displays the [HomeFragment] in the fragment container.
     */
    private fun showHome() {
        showFragment(HomeFragment.TAG, HomeFragment::class.java, HomeFragment.Companion::newInstance)
    }

    /**
     * Called when the "Add Photos" button of the Bottom Navigation Menu is clicked.
     * Displays the [PhotoFragment] in the fragment container.
     */
    private fun showAddPhoto() {
        showFragment(PhotoFragment.TAG, PhotoFragment::class.java, PhotoFragment.Companion::newInstance)
    }

    /**
     * Called when the Profile button of the Bottom Navigation Menu is clicked.
     * Displays the [ProfileFragment] in the fragment container.
     */
    private fun showProfile() {
        showFragment(ProfileFragment.TAG, ProfileFragment::class.java, ProfileFragment.Companion::newInstance)
    }

    /**
     * Method that displays the fragment pointed to by the [fragmentClass] in the fragment container.
     * 1. If the fragment required was not previously shown, then a new instance will be created
     * and shown in the container. Any fragment instance in the container previously, will be hidden, to load the
     * same instance the next time when it is needed.
     * 2. If the fragment required is currently shown, then it will continue to be shown.
     * 3. If the fragment was previously shown, then the same instance will be shown, hiding the fragment
     * previously shown in the container.
     *
     * @param T The Type of [BaseFragment].
     * @param fragmentTag [String] representing the Tag of the Fragment to be shown. Required for
     * looking up fragments loaded by the FragmentManager.
     * @param fragmentClass [Class] of the Fragment to be shown.
     * @param creator Lambda that creates and provides the instance of the Fragment to be shown.
     */
    private fun <T : BaseFragment<out BaseViewModel>> showFragment(
        fragmentTag: String, fragmentClass: Class<T>, creator: () -> T
    ) {

        // If the fragment required is currently shown, then head back
        if (fragmentClass.isInstance(activeFragment)) return

        // Get the FragmentTransaction
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // Find the Fragment by Tag
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (fragment == null) {
            // If the Fragment was not loaded previously, then create a new instance and add to the FragmentTransaction
            fragment = creator.invoke()
            fragmentTransaction.add(R.id.container_main_nav_fragment, fragment, fragmentTag)
        } else {
            // If the Fragment was previously loaded, then show that same Fragment instance
            fragmentTransaction.show(fragment)
        }

        // If there was another Fragment previously shown in the container, then hide this Fragment
        // to load the same, the next time when needed
        if (activeFragment != null) {
            fragmentTransaction.hide(activeFragment!!)
        }

        // Commit the Fragment Transactions
        fragmentTransaction.commit()

        // Save the Fragment shown
        activeFragment = fragment
    }

}