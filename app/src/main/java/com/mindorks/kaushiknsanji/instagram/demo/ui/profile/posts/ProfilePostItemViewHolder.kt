/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ItemProfilePostBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.toRecyclerView
import com.mindorks.kaushiknsanji.instagram.demo.utils.widget.getSpanCount

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
            // Get the Span Count of GridLayoutManager from [container]
            val spanCount = container.toRecyclerView().getSpanCount(1)
            // Load the Image
            ImageUtils.loadImage(
                itemView.context,
                itemViewBinding.imageProfileItemPostPhoto,
                imageData = image.copy(
                    // Make a copy with the Placeholder dimensions normalized by the Span Count
                    // in order to fit the Grid accordingly
                    placeHolderWidth = image.placeHolderWidth / spanCount,
                    placeHolderHeight = image.placeHolderHeight / spanCount
                ),
                requestOptions = listOf(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder_photo)
                )
            )
        }

        // Register an observer for the User's Click action on Post Item
        itemViewModel.actionItemClick.observeEvent(this) { post: Post ->
            listener?.onItemClick(post)
        }
    }

}