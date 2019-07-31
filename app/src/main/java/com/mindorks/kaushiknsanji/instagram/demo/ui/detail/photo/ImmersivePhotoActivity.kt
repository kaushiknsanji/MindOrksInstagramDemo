package com.mindorks.kaushiknsanji.instagram.demo.ui.detail.photo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.setVisibility
import kotlinx.android.synthetic.main.activity_immersive_photo.*

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_immersive_photo' to show the Post's Photo
 * in an Immersive mode allowing the user to zoom and interact with it.
 * [ImmersivePhotoViewModel] is the Primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class ImmersivePhotoActivity : BaseActivity<ImmersivePhotoViewModel>() {

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_immersive_photo

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {
        // Ensure the activity is started with the required information on the Post Photo to be shown,
        // and then load the Post Photo
        intent.extras!!.run {
            viewModel.onLoadImage(
                getString(EXTRA_IMAGE_URL)!!,
                getInt(EXTRA_IMAGE_PLACEHOLDER_WIDTH),
                getInt(EXTRA_IMAGE_PLACEHOLDER_HEIGHT)
            )
        }

        // Register Tap listener on the Post Photo
        view_immersive_photo.setOnPhotoTapListener { _, _, _ -> viewModel.onToggleFullscreen() }

        // Register Click listener on the "Close" Image
        image_immersive_photo_close.setOnClickListener { viewModel.onClose() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on Post Photo loading progress to show/hide the Progress Circle
        viewModel.loadingProgress.observe(this, Observer { started: Boolean ->
            // Show the Progress Circle when [started], else leave it hidden
            progress_immersive_photo.setVisibility(started)
        })

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        viewModel.postImage.observe(this, Observer { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(this@ImmersivePhotoActivity) // Loading with Activity's context
                    .load(GlideHelper.getProtectedUrl(image.url, image.headers)) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)) // Loading with PlaceHolder Image

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    view_immersive_photo.run {
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
                glideRequest.into(view_immersive_photo)
            }
        })

        // Register an observer for Fullscreen Toggle events to toggle the UI Options for Fullscreen Immersive mode
        viewModel.toggleFullscreen.observeEvent(this) { toggle: Boolean ->
            if (toggle) {
                // On [toggle], toggle the bits corresponding to Fullscreen Immersive in UI Options

                // Set the new UI Options
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.also { uiOptions: Int ->
                    // Toggle "Navigation bar", "Status bar" and "Immersive mode" bits
                    uiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_FULLSCREEN xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
            }
        }

        // Register an observer on Immersive Mode LiveData to show/hide other views accordingly
        viewModel.isImmersiveMode.observe(this, Observer { isImmersive: Boolean ->
            if (isImmersive) {
                // On [isImmersive], hide the required views
                image_immersive_photo_close.visibility = View.GONE
            } else {
                // Otherwise, show the required views
                image_immersive_photo_close.visibility = View.VISIBLE
            }
        })

        // Register an observer for close action events, to close this Activity
        viewModel.closeAction.observeEvent(this) { close: Boolean ->
            if (close) {
                // Terminate the Activity on [close]
                finish()
            }
        }
    }

    companion object {
        // Intent extra constant for the URL of the Image to shown
        @JvmField
        val EXTRA_IMAGE_URL = ImmersivePhotoActivity::class.java.`package`.toString() + "extra.IMAGE_URL"

        // Intent extra constant for the computed Placeholder Width of the Image to be shown
        @JvmField
        val EXTRA_IMAGE_PLACEHOLDER_WIDTH =
            ImmersivePhotoActivity::class.java.`package`.toString() + "extra.IMAGE_PLACEHOLDER_WIDTH"

        // Intent extra constant for the computed Placeholder Height of the Image to be shown
        @JvmField
        val EXTRA_IMAGE_PLACEHOLDER_HEIGHT =
            ImmersivePhotoActivity::class.java.`package`.toString() + "extra.IMAGE_PLACEHOLDER_HEIGHT"
    }

}
