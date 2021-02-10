package com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo

import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityImmersivePhotoBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeNonNull
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_immersive_photo' to show the Post's Photo
 * in an Immersive mode allowing the user to zoom and interact with it.
 * [ImmersivePhotoViewModel] is the Primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class ImmersivePhotoActivity : BaseActivity<ImmersivePhotoViewModel>() {

    // ViewBinding instance for this Activity
    private val binding by viewBinding(ActivityImmersivePhotoBinding::inflate)

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
        // Ensure the activity is started with the required URL of the Post Photo to be shown,
        // and then load the Post Photo
        intent.getStringExtra(EXTRA_IMAGE_URL)!!.let { imageUrl: String ->
            viewModel.onLoadImage(imageUrl)
        }

        // Dispatch the current Immersive Mode state to ViewModel
        updateImmersiveModeState(window.decorView.systemUiVisibility)

        // Register Tap listener on the Post Photo
        binding.viewImmersivePhoto.setOnPhotoTapListener { _, _, _ -> viewModel.onToggleFullscreen() }

        // Register Click listener on the "Close" Image
        binding.imageImmersivePhotoClose.setOnClickListener { viewModel.onClose() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        viewModel.postImage.observeNonNull(this) { image: Image ->
            image.run {
                // Configuring Glide with Image URL and other relevant customizations
                GlideApp
                    .with(this@ImmersivePhotoActivity) // Loading with Activity's context
                    .load(
                        // Loading GlideUrl with Headers
                        GlideHelper.getProtectedUrl(
                            image.url,
                            image.headers
                        )
                    )
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)) // Loading with PlaceHolder Image
                    .into(view_immersive_photo) // Start the download and load into the corresponding ImageView
            }
        }

        // Register an observer for Fullscreen Toggle events to toggle the System UI Visibility bits
        // for Fullscreen Sticky Immersive mode
        viewModel.toggleFullscreen.observeEvent(this) { toggle: Boolean ->
            if (toggle) {
                // On [toggle], toggle the bits corresponding to Fullscreen Sticky Immersive in System UI Visibility

                // Set the new System UI Visibility
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.let { uiOptions: Int ->
                    // Toggle "Navigation bar", "Status bar" and "Sticky Immersive mode" bits
                    uiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_FULLSCREEN xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }.also {
                    // Also, update the Immersive Mode state to ViewModel
                    updateImmersiveModeState(it)
                }
            }
        }

        // Register an observer on Immersive Mode state change events to show/hide other views accordingly
        viewModel.immersiveModeState.observeEvent(this) { immersiveModeOn: Boolean ->
            // When Immersive Mode is On, hide the Photo Close ImageView
            binding.imageImmersivePhotoClose.showWhen(!immersiveModeOn)
        }

        // Register an observer for close action events, to close this Activity
        viewModel.actionClose.observeEvent(this) { close: Boolean ->
            if (close) {
                // Terminate the Activity on [close]
                finish()
            }
        }
    }

    /**
     * Updates the current Immersive Mode state to the Primary ViewModel to handle.
     *
     * @param uiOptions The Bitwise-or flags of System UI Visibility, to check for Immersive Mode state.
     * Value published will be `true` when the [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] bit is already set.
     */
    private fun updateImmersiveModeState(uiOptions: Int) {
        viewModel.onUpdateImmersiveModeState(
            uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        )
    }

    companion object {
        // Intent extra constant for the URL of the Image to be shown
        @JvmField
        val EXTRA_IMAGE_URL =
            ImmersivePhotoActivity::class.java.`package`?.toString() + "extra.IMAGE_URL"
    }

}