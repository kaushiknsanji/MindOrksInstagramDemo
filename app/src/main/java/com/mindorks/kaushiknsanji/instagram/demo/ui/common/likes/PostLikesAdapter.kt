package com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseAdapter

/**
 * [BaseAdapter] subclass for the [androidx.recyclerview.widget.RecyclerView]
 * in [com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity] and
 * [com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity].
 *
 * @param parentLifecycle [Lifecycle] of the Activity using this Adapter.
 * @constructor Instance of [PostLikesAdapter] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class PostLikesAdapter(parentLifecycle: Lifecycle) :
    BaseAdapter<Post.User, PostLikeItemViewHolder>(parentLifecycle) {

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
        PostLikeItemViewHolder(parent)

}