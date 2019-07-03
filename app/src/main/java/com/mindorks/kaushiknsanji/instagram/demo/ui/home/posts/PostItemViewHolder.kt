package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import kotlinx.android.synthetic.main.item_home_post.view.*

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [PostsAdapter].
 *
 * @param container [ViewGroup] that contains the ItemViews.
 *
 * @author Kaushik N Sanji
 */
class PostItemViewHolder(container: ViewGroup) :
    BaseItemViewHolder<Post, PostItemViewModel>(R.layout.item_home_post, container) {

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
        // Register a click listener on the "Heart" ImageButton, to enable like/unlike Post
        itemView.imgbtn_home_item_toggle_post_like.setOnClickListener { itemViewModel.onLikeClick() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the Name LiveData to set its value on the corresponding textView
        itemViewModel.postCreatorName.observe(this, Observer { name ->
            itemView.text_home_item_post_creator_name.text = name
        })

        // Register an observer on the Post Creation Time LiveData to set its value on the corresponding textView
        itemViewModel.postCreationTime.observe(this, Observer { creationTime ->
            itemView.text_home_item_post_time.text = creationTime
        })

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        itemViewModel.likesCount.observe(this, Observer { likesCount ->
            itemView.text_home_item_post_like_count.text = itemView.context.getString(
                R.string.label_home_item_post_like_count,
                likesCount
            )
        })

        // Register an observer on the user like button action LiveData to change the "Heart" image accordingly
        itemViewModel.hasUserLiked.observe(this, Observer { hasLiked: Boolean ->
            itemView.imgbtn_home_item_toggle_post_like.run {
                isActivated = hasLiked
            }
        })

        // Register an observer on the Post Creator's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.profileImage.observe(this, Observer { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(itemView.context) // Loading with ItemView's context
                    .load(GlideHelper.getProtectedUrl(image.url, image.headers)) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.circleCropTransform()) // Cropping the Image with a Circular Mask
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected)) // Loading with PlaceHolder Image

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    itemView.image_home_item_post_creator_profile.run {
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
                glideRequest.into(itemView.image_home_item_post_creator_profile)
            }
        })

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.postImage.observe(this, Observer { image: Image? ->
            image?.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(itemView.context) // Loading with ItemView's context
                    .load(GlideHelper.getProtectedUrl(image.url, image.headers)) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)) // Loading with PlaceHolder Image

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    itemView.image_home_item_post_photo.run {
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
                glideRequest.into(itemView.image_home_item_post_photo)
            }
        })

    }
}