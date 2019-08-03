package com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikesAdapter.Listener

/**
 * [BaseAdapter] subclass for the [androidx.recyclerview.widget.RecyclerView]
 * in [com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity] and
 * [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity].
 *
 * @param parentLifecycle [Lifecycle] of the Activity using this Adapter.
 * @param hostListener The Host of this Adapter that wishes to auto register/unregister as [Listener]
 * for Navigation events. The Host should implement the [Listener] for this to work.
 * @constructor Instance of [PostLikesAdapter] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class PostLikesAdapter(
    parentLifecycle: Lifecycle,
    hostListener: Listener?
) : BaseAdapter<Post.User, Listener, PostLikeItemViewHolder>(parentLifecycle, hostListener) {

    /**
     * Called when RecyclerView needs a new [PostLikeItemViewHolder] of the given type to represent
     * an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new [PostLikeItemViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostLikeItemViewHolder =
        PostLikeItemViewHolder(parent, object : Listener {
            /**
             * Callback Method of [BaseAdapter.DefaultListener] invoked when the user clicks on the Adapter Item.
             *
             * @param itemData Data of the Adapter Item which is an instance of [Post.User].
             */
            override fun onItemClick(itemData: Post.User) {
                // Delegate to all registered listeners
                listenerObservable.getListeners().forEach { it.onItemClick(itemData) }
            }
        })

    /**
     * Interface to be implemented by the Host of this Adapter to receive callback events.
     */
    interface Listener : DefaultListener<Post.User>

}