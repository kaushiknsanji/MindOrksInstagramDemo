package com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts.PostsAdapter.Listener

/**
 * [BaseAdapter] subclass for the [androidx.recyclerview.widget.RecyclerView]
 * in [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment].
 *
 * @param parentLifecycle [Lifecycle] of [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment]
 * @param hostListener The Host of this Adapter that wishes to auto register/unregister as [Listener]
 * for Navigation events. The Host should implement the [Listener] for this to work.
 * @constructor Instance of [PostsAdapter] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class PostsAdapter(
    parentLifecycle: Lifecycle,
    hostListener: Listener?
) : BaseAdapter<Post, Listener, PostItemViewHolder>(parentLifecycle, hostListener) {

    /**
     * Called when RecyclerView needs a new [PostItemViewHolder] of the given type to represent
     * an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new [PostItemViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder =
        PostItemViewHolder(parent, object : Listener {

            /**
             * Callback Method of [PostsAdapter.Listener] invoked when the user clicks on the TextView that
             * displays the number of Likes on the Post.
             *
             * @param itemData Data of the Adapter Item which is an instance of [Post].
             */
            override fun onLikesCountClick(itemData: Post) {
                // Delegate to all registered listeners
                delegateEvent { it.onLikesCountClick(itemData) }
            }

            /**
             * Callback Method of [BaseAdapter.DefaultListener] invoked when the user clicks on the Adapter Item.
             *
             * @param itemData Data of the Adapter Item which is an instance of [Post].
             */
            override fun onItemClick(itemData: Post) {
                // Delegate to all registered listeners
                delegateEvent { it.onItemClick(itemData) }
            }

            /**
             * Callback Method of [PostsAdapter.Listener] invoked when the user likes/unlikes the Post.
             *
             * @param itemData The updated data of Adapter Item which is an instance of [Post].
             */
            override fun onLikeUnlikeSync(itemData: Post) {
                // Delegate to all registered listeners
                delegateEvent { it.onLikeUnlikeSync(itemData) }
            }

            /**
             * Delegates the [event] to listeners registered in [listenerObservable].
             */
            inline fun delegateEvent(event: (Listener) -> Unit) {
                listenerObservable.getListeners().forEach(event)
            }

        })

    /**
     * Provides the [DiffUtil.ItemCallback] instance for
     * calculating the difference between two non-null items in a List.
     */
    override fun provideItemCallback(): DiffUtil.ItemCallback<Post> = object : DiffUtil.ItemCallback<Post>() {
        /**
         * Called to check whether two objects represent the same item.
         *
         * For example, if your items have unique ids, this method should check their id equality.
         *
         * Note: `null` items in the list are assumed to be the same as another `null`
         * item and are assumed to not be the same as a non-`null` item. This callback will
         * not be invoked for either of those cases.
         *
         * @param oldPost The [Post] item in the old list.
         * @param newPost The [Post] item in the new list.
         * @return `true` if the two items represent the same [Post] or `false` if they are different.
         */
        override fun areItemsTheSame(oldPost: Post, newPost: Post): Boolean = (oldPost.id == newPost.id)

        /**
         * Called to check whether two items have the same data.
         *
         * This information is used to detect if the contents of an item have changed.
         *
         * This method to check equality instead of [Object.equals] so that you can
         * change its behavior depending on your UI.
         *
         * For example, if you are using DiffUtil with a
         * [androidx.recyclerview.widget.RecyclerView.Adapter], you should
         * return whether the items' visual representations are the same.
         *
         * This method is called only if [areItemsTheSame] returns `true` for
         * these items.
         *
         * Note: Two `null` items are assumed to represent the same contents. This callback
         * will not be invoked for this case.
         *
         * @param oldPost The [Post] item in the old list.
         * @param newPost The [Post] item in the new list.
         * @return `true` if the contents of the items are the same or `false` if they are different.
         */
        override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean = (oldPost == newPost)
    }

    /**
     * Interface to be implemented by the Host [com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment]
     * to receive callback events.
     */
    interface Listener : DefaultListener<Post> {
        /**
         * Callback Method of [PostsAdapter.Listener] invoked when the user clicks on the TextView that
         * displays the number of Likes on the Post.
         *
         * @param itemData Data of the Adapter Item which is an instance of [Post].
         */
        fun onLikesCountClick(itemData: Post)

        /**
         * Callback Method of [PostsAdapter.Listener] invoked when the user likes/unlikes the Post.
         *
         * @param itemData The updated data of Adapter Item which is an instance of [Post].
         */
        fun onLikeUnlikeSync(itemData: Post)
    }

}