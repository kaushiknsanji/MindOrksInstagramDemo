package com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo

import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ActivityImmersivePhotoBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseImmersiveActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeNonNull
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.viewBinding
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showWhen

/**
 * [BaseImmersiveActivity] subclass that inflates the layout 'R.layout.activity_immersive_photo'
 * to show the Post's Photo in an Immersive mode allowing the user to zoom and interact with it.
 *
 * [ImmersivePhotoViewModel] is the Primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class ImmersivePhotoActivity : BaseImmersiveActivity<ImmersivePhotoViewModel>() {

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

        // Register Tap listener on the Post Photo, to trigger Fullscreen toggle events
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
            ImageUtils.loadImage(
                this,
                binding.viewImmersivePhoto,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)
                )
            )
        }

        // Register an observer on System bars visibility state change events
        // to show/hide other views accordingly
        viewModel.systemBarsVisibilityState.observeEvent(this) { isVisible: Boolean ->
            // When System bars are visible, hide the Photo Close ImageView
            binding.imageImmersivePhotoClose.showWhen(!isVisible)
        }

        // Register an observer for close action events, to close this Activity
        viewModel.actionClose.observeEvent(this) { close: Boolean ->
            if (close) {
                // Terminate the Activity on [close]
                finish()
            }
        }
    }

    companion object {
        // Intent extra constant for the URL of the Image to be shown
        @JvmField
        val EXTRA_IMAGE_URL =
            ImmersivePhotoActivity::class.java.`package`?.toString() + "extra.IMAGE_URL"
    }

}