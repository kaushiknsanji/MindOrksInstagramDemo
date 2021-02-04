package com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import kotlinx.android.synthetic.main.item_post_like.view.*

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [PostLikesAdapter].
 *
 * @param container [ViewGroup] that contains the ItemViews.
 * @param listener Instance of [PostLikesAdapter.Listener] to receive Navigation events. Defaulted to `null`.
 *
 * @author Kaushik N Sanji
 */
class PostLikeItemViewHolder(
    container: ViewGroup,
    listener: PostLikesAdapter.Listener? = null
) : BaseItemViewHolder<Post.User, PostLikesAdapter.Listener, PostLikeItemViewModel>(
    R.layout.item_post_like,
    container,
    listener
) {

    /**
     * Injects dependencies exposed by [ViewHolderComponent] into [androidx.recyclerview.widget.RecyclerView.ViewHolder].
     */
    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) {
        viewHolderComponent.inject(this)
    }

    /**
     * Initializes the Layout of the [itemView].
     */
    override fun setupView(itemView: View) {
        //No-op
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the User Name LiveData to set its value on the corresponding textView
        itemViewModel.userName.observe(this) { name ->
            itemView.text_post_like_item_user_profile_name.text = name
        }

        // Register an observer on the User Handle LiveData to set its value on the corresponding textView
        itemViewModel.userHandle.observe(this) { handle ->
            itemView.text_post_like_item_user_profile_handle.text = handle
        }

        // Register an observer on the User's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.userImage.observe(this) { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(itemView.context) // Loading with ItemView's context
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
                    itemView.image_post_like_item_user_profile.run {
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
                glideRequest.into(itemView.image_post_like_item_user_profile)
            } ?: run {
                // Set default photo when there is no profile picture

                // Configuring Glide with relevant customizations and then setting the default photo
                GlideApp
                    .with(itemView.context) // Loading with ItemView's context
                    .load(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_placeholder_profile
                        )
                    ) // Loading the default Image
                    .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                    .into(itemView.image_post_like_item_user_profile) // Load into the corresponding ImageView
            }
        }

    }
}