package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostsAdapter.Listener

/**
 * [BaseAdapter] subclass for the [androidx.recyclerview.widget.RecyclerView]
 * in [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment].
 *
 * @param parentLifecycle [Lifecycle] of [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment]
 * @param hostListener The Host of this Adapter that wishes to auto register/unregister as [Listener]
 * for Navigation events. The Host should implement the [Listener] for this to work.
 * @constructor Instance of [ProfilePostsAdapter] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class ProfilePostsAdapter(
    parentLifecycle: Lifecycle,
    hostListener: Listener?
) : BaseAdapter<Post, Listener, ProfilePostItemViewHolder>(parentLifecycle, hostListener) {

    /**
     * Called when RecyclerView needs a new [ProfilePostItemViewHolder] of the given type to represent
     * an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new [ProfilePostItemViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostItemViewHolder =
        ProfilePostItemViewHolder(parent, object : Listener {
            /**
             * Callback Method of [BaseAdapter.DefaultListener] invoked when the user clicks on the Adapter Item.
             *
             * @param itemData Data of the Adapter Item which is an instance of [Post].
             */
            override fun onItemClick(itemData: Post) {
                // Delegate to all registered listeners
                listenerObservable.getListeners().forEach { it.onItemClick(itemData) }
            }
        })

    /**
     * Interface to be implemented by the Host [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment]
     * to receive callback events.
     */
    interface Listener : DefaultListener<Post>
}