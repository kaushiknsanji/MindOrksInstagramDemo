package com.mindorks.kaushiknsanji.instagram.demo.ui.main

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
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import kotlinx.android.synthetic.main.activity_main.*
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

}
