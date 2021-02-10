package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ItemProfilePostBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import kotlinx.android.synthetic.main.item_profile_post.view.*

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [ProfilePostsAdapter].
 *
 * @property container [ViewGroup] that contains the ItemViews.
 * @param listener Instance of [ProfilePostsAdapter.Listener] to receive Navigation events. Defaulted to `null`.
 *
 * @author Kaushik N Sanji
 */
class ProfilePostItemViewHolder(
    private val container: ViewGroup, listener: ProfilePostsAdapter.Listener? = null
) : BaseItemViewHolder<Post, ProfilePostsAdapter.Listener, ProfilePostItemViewModel, ItemProfilePostBinding>(
    R.layout.item_profile_post,
    container,
    listener,
    viewBindingFactory = ItemProfilePostBinding::inflate
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
        //Register a Click Listener on the Post Item
        itemView.setOnClickListener { itemViewModel.onItemClick() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an Observer on User's Post Photo LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.postImage.observe(this) { image: Image ->
            image.run {
                // Configuring Glide with Image URL and other relevant customizations
                val glideRequest = GlideApp
                    .with(itemView.context) // Loading with ItemView's context
                    .load(
                        GlideHelper.getProtectedUrl(
                            image.url,
                            image.headers
                        )
                    ) // Loading the GlideUrl with Headers
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)) // Loading with PlaceHolder Image

                // Get the Span Count of GridLayoutManager from [container]
                val gridSpanCount = getContainerSpanCount()

                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Dividing the Placeholder dimensions by the Span Count
                    val itemWidth = placeHolderWidth / gridSpanCount
                    val itemHeight = placeHolderHeight / gridSpanCount

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    itemView.image_profile_item_post_photo.run {
                        val params = layoutParams as ViewGroup.LayoutParams
                        params.width = itemWidth
                        params.height = itemHeight
                        layoutParams = params
                    }

                    // Override the dimensions of the Image (downloaded) to placeholder dimensions
                    glideRequest
                        .apply(RequestOptions.overrideOf(itemWidth, itemHeight))
                }

                // Start the download and load into the corresponding ImageView
                glideRequest.into(itemView.image_profile_item_post_photo)
            }
        }

        // Register an observer for the User's Click action on Post Item
        itemViewModel.actionItemClick.observeEvent(this) { post: Post ->
            listener?.onItemClick(post)
        }
    }

    /**
     * Retrieves the Span Count if applied on the RecyclerView [container] GridLayoutManager. If not applied, then it
     * returns `1` as the default value.
     */
    private fun getContainerSpanCount(): Int =
        (container as? RecyclerView)?.let { recyclerView ->
            // Retrieving the SpanCount value if the layout manager is GridLayoutManager
            (recyclerView.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        } ?: 1
}