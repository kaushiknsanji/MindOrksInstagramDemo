package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideApp
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.GlideHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import kotlinx.android.synthetic.main.item_home_post.view.*

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [PostsAdapter].
 *
 * @param container [ViewGroup] that contains the ItemViews.
 *
 * @author Kaushik N Sanji
 */
class PostItemViewHolder(container: ViewGroup, listener: BaseAdapter.DefaultListener<Post>?) :
    BaseItemViewHolder<Post, PostItemViewModel>(R.layout.item_home_post, container, listener) {

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
        // Register a Click Listener on the "Heart" ImageButton, to enable like/unlike Post
        itemView.imgbtn_home_item_toggle_post_like.setOnClickListener { itemViewModel.onLikeClick() }

        // Create a Double Tap Gesture Detection on Post Photo, to enable like/unlike Post on Double Tap action
        val postPhotoDoubleTapGestureDetector = GestureDetectorCompat(
            itemView.context,
            object : GestureDetector.SimpleOnGestureListener() {

                /**
                 * Notified when a tap occurs with the down [MotionEvent]
                 * that triggered it. This will be triggered immediately for
                 * every down event. All other events should be preceded by this.
                 *
                 * @param event The down motion event.
                 */
                override fun onDown(event: MotionEvent?): Boolean {
                    // Report as Consumed to receive future event for Double-Tap
                    return true
                }

                /**
                 * Notified when a single-tap occurs.
                 *
                 * Unlike [android.view.GestureDetector.OnGestureListener.onSingleTapUp], this
                 * will only be called after the detector is confident that the user's
                 * first tap is not followed by a second tap leading to a double-tap
                 * gesture.
                 *
                 * @param event The down motion event of the single-tap.
                 * @return true if the event is consumed, else false
                 */
                override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
                    // Delegate the event to the system to handle
                    itemView.performClick()
                    // Report as Unconsumed
                    return false
                }

                /**
                 * Notified when a double-tap occurs.
                 *
                 * @param event The down motion event of the first tap of the double-tap.
                 * @return true if the event is consumed, else false
                 */
                override fun onDoubleTap(event: MotionEvent?): Boolean {
                    // Delegate to the ItemView's ViewModel to handle
                    itemViewModel.onLikeClick()
                    // Report as Consumed
                    return true
                }

            }
        )

        // Register a Double Tap Listener using the Gesture Detection on Post Photo
        itemView.image_home_item_post_photo.setOnTouchListener { _, event: MotionEvent ->
            // Pass all the events to the Double Tap Gesture Detector and return its result
            postPhotoDoubleTapGestureDetector.onTouchEvent(event)
        }

        // Register a Click Listener on the TextView that displays the number of Likes on the Post
        itemView.text_home_item_post_like_count.setOnClickListener { itemViewModel.onLikeCountClick() }

        // Register a Click Listener on the Post Item
        itemView.setOnClickListener { itemViewModel.onItemClick() }

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
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder_profile)) // Loading with PlaceHolder Image

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
                    .into(itemView.image_home_item_post_creator_profile) // Load into the corresponding ImageView
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

        // Register an observer for the User's Click action on Post Item
        itemViewModel.actionItemClick.observeEvent(this) { post: Post ->
            listener?.onItemClick(post)
        }

        // Register an observer for the User's Click action on Post Likes Count
        itemViewModel.actionLikesCountClick.observeEvent(this) { post: Post ->
            (listener as? PostsAdapter.Listener)?.onLikesCountClick(post)
        }

    }

}