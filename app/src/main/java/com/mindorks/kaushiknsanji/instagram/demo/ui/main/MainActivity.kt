package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import kotlin.reflect.KFunction0

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

    // Saves the fragment instance being shown
    private var activeFragment: Fragment? = null

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_main

    /**
     * Initializes the Layout of the Activity.a
     */
    override fun setupView(savedInstanceState: Bundle?) {

        // Register a navigation listener on the Bottom Navigation Menu Items
        bottom_nav_main.run {
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
                // Fragment to restore will be our container fragment that is added, not yet hidden and not yet visible
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
            bottom_nav_main.selectedItemId = R.id.action_main_bottom_nav_home
        }

        // Register an observer for EditProfileActivity launch events
        mainSharedViewModel.launchEditProfile.observeEvent(this) {
            // Start EditProfileActivity with the request code for results
            startActivityForResult(
                Intent(this, EditProfileActivity::class.java),
                EditProfileActivity.REQUEST_EDIT_PROFILE
            )
        }

    }

    /**
     * Called when the Home button of the Bottom Navigation Menu is clicked.
     * Displays the [HomeFragment] in the fragment container.
     */
    private fun showHome() {
        showFragment(HomeFragment.TAG, HomeFragment.Companion::newInstance, HomeFragment::class.java)
    }

    /**
     * Called when the "Add Photos" button of the Bottom Navigation Menu is clicked.
     * Displays the [PhotoFragment] in the fragment container.
     */
    private fun showAddPhoto() {
        showFragment(PhotoFragment.TAG, PhotoFragment.Companion::newInstance, PhotoFragment::class.java)
    }

    /**
     * Called when the Profile button of the Bottom Navigation Menu is clicked.
     * Displays the [ProfileFragment] in the fragment container.
     */
    private fun showProfile() {
        showFragment(ProfileFragment.TAG, ProfileFragment.Companion::newInstance, ProfileFragment::class.java)
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
     * @param kInstanceFunction Kotlin Method reference that creates and provides the instance of the Fragment to be shown.
     * @param fragmentClass [Class] of the Fragment to be shown.
     */
    private fun <T : BaseFragment<out BaseViewModel>> showFragment(
        fragmentTag: String, kInstanceFunction: KFunction0<T>, fragmentClass: Class<T>
    ) {

        // If the fragment required is currently shown, then head back
        if (fragmentClass.isInstance(activeFragment)) return

        // Get the FragmentTransaction
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // Find the Fragment by Tag
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (fragment == null) {
            // If the Fragment was not loaded previously, then create a new instance and add to the FragmentTransaction
            fragment = kInstanceFunction.invoke()
            fragmentTransaction.add(R.id.container_main_nav_fragment, fragment, fragmentTag)
        } else {
            // If the Fragment was previously loaded, then show that same Fragment instance
            fragmentTransaction.show(fragment)
        }

        // If there was another Fragment previously shown in the container, then hide this Fragment
        // to load the same, the next time when needed
        if (activeFragment != null) {
            fragmentTransaction.hide(activeFragment as Fragment)
        }

        // Commit the Fragment Transactions
        fragmentTransaction.commit()

        // Save the Fragment shown
        activeFragment = fragment
    }

    /**
     * Receive the result from a previous call to [startActivityForResult].
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode The integer result code returned by the child activity
     * through its setResult().
     * @param intent An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode >= RESULT_OK) {
            // When we have a success result from any Activity started

            if (resultCode >= RESULT_FIRST_USER) {
                // When we have the custom results for the requests made

                // Taking action based on Request code
                when (requestCode) {
                    // For EditProfileActivity request
                    EditProfileActivity.REQUEST_EDIT_PROFILE -> {
                        // Taking action based on the Result codes
                        when (resultCode) {
                            // For the Successful edit
                            EditProfileActivity.RESULT_EDIT_PROFILE_SUCCESS -> {
                                // Delegate to the MainSharedViewModel, to trigger profile updates in HomeFragment
                                // and ProfileFragment
                                mainSharedViewModel.onEditProfileSuccess()
                                // Display the success message if available
                                intent?.getStringExtra(EditProfileActivity.EXTRA_RESULT_EDIT_SUCCESS)
                                    ?.takeUnless { it.isBlank() }?.let {
                                        showMessage(it)
                                    }
                            }
                            // For the Update that did not require any action as there was no change
                            EditProfileActivity.RESULT_EDIT_PROFILE_NO_ACTION -> {
                                // Display a message to the user saying no changes were done
                                showMessage(R.string.message_edit_profile_no_change)
                            }
                        }
                    }
                }

            }

        }

    }
}