package com.mindorks.kaushiknsanji.instagram.demo.ui.profile


import android.os.Bundle
import android.view.View
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment

/**
 * [BaseFragment] subclass that inflates the layout 'R.layout.fragment_profile' to show a list of Posts
 * created by the logged-in User. Also, provides an option to the logged-in user to edit profile.
 * [ProfileViewModel] is the primary [androidx.lifecycle.ViewModel] of this Fragment.
 *
 * @author Kaushik N Sanji
 */
class ProfileFragment : BaseFragment<ProfileViewModel>() {

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

    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()
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
