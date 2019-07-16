package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter

/**
 * [BaseAdapter] subclass for the [androidx.recyclerview.widget.RecyclerView]
 * in [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment].
 *
 * @param parentLifecycle [Lifecycle] of [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment]
 * @constructor Instance of [ProfilePostsAdapter] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class ProfilePostsAdapter(
    parentLifecycle: Lifecycle
) : BaseAdapter<Post, ProfilePostItemViewHolder>(parentLifecycle, mutableListOf()) {

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
        ProfilePostItemViewHolder(parent)

}