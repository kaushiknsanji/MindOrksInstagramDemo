package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ItemHomePostBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [PostsAdapter].
 *
 * @param container [ViewGroup] that contains the ItemViews.
 * @param listener Instance of [PostsAdapter.Listener] to receive Navigation events. Defaulted to `null`.
 *
 * @author Kaushik N Sanji
 */
class PostItemViewHolder(container: ViewGroup, listener: PostsAdapter.Listener? = null) :
    BaseItemViewHolder<Post, PostsAdapter.Listener, PostItemViewModel, ItemHomePostBinding>(
        R.layout.item_home_post,
        container,
        listener,
        viewBindingFactory = ItemHomePostBinding::inflate
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
    @SuppressLint("ClickableViewAccessibility")
    override fun setupView(itemView: View) {
        // Register a Click Listener on the "Heart" ImageButton, to enable like/unlike Post
        itemViewBinding.imgbtnHomeItemTogglePostLike.setOnClickListener { itemViewModel.onLikeClick() }

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
        itemViewBinding.imageHomeItemPostPhoto.setOnTouchListener { _, event: MotionEvent ->
            // Pass all the events to the Double Tap Gesture Detector and return its result
            postPhotoDoubleTapGestureDetector.onTouchEvent(event)
        }

        // Register a Click Listener on the TextView that displays the number of Likes on the Post
        itemViewBinding.textHomeItemPostLikeCount.setOnClickListener { itemViewModel.onLikeCountClick() }

        // Register a Click Listener on the Post Item
        itemViewBinding.root.setOnClickListener { itemViewModel.onItemClick() }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the Name LiveData to set its value on the corresponding textView
        itemViewModel.postCreatorName.observe(this) { name ->
            itemViewBinding.textHomeItemPostCreatorName.text = name
        }

        // Register an observer on the Post Creation Time LiveData to set its value on the corresponding textView
        itemViewModel.postCreationTime.observe(this) { creationTime ->
            itemViewBinding.textHomeItemPostTime.text = creationTime
        }

        // Register an observer on the "Likes count of the Post" - LiveData to set its value
        // on the corresponding textView
        itemViewModel.likesCount.observe(this) { likesCount ->
            itemViewBinding.textHomeItemPostLikeCount.text = itemView.context.getString(
                R.string.label_home_item_post_like_count,
                likesCount
            )
        }

        // Register an observer on the user like button action LiveData to change the "Heart" image accordingly
        itemViewModel.hasUserLiked.observe(this) { hasLiked: Boolean ->
            itemViewBinding.imgbtnHomeItemTogglePostLike.isActivated = hasLiked
        }

        // Register an observer on the Post Creator's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.profileImage.observe(this) { image: Image? ->
            ImageUtils.loadImage(
                itemView.context,
                itemViewBinding.imageHomeItemPostCreatorProfile,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.circleCropTransform(),
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_profile)
                ),
                defaultImageRes = R.drawable.ic_placeholder_profile,
                defaultImageRequestOptions = listOf(
                    RequestOptions.circleCropTransform()
                )
            )
        }

        // Register an observer on the Post Creator's Photo LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.postImage.observe(this) { image: Image ->
            ImageUtils.loadImage(
                itemView.context,
                itemViewBinding.imageHomeItemPostPhoto,
                imageData = image,
                requestOptions = listOf(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)
                )
            )
        }

        // Register an observer for the User's Click action on Post Item
        itemViewModel.actionItemClick.observeEvent(this) { post: Post ->
            listener?.onItemClick(post)
        }

        // Register an observer for the User's Click action on Post Likes Count
        itemViewModel.actionLikesCountClick.observeEvent(this) { post: Post ->
            listener?.onLikesCountClick(post)
        }

        // Register an observer for the User's Like/Unlike action on the Post
        itemViewModel.actionLikeUnlike.observeEvent(this) { post: Post ->
            listener?.onLikeUnlikeSync(post)
        }
    }

}