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

package com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.databinding.ItemPostLikeBinding
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ImageUtils

/**
 * [BaseItemViewHolder] subclass used as [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * in [PostLikesAdapter].
 *
 * @param container [ViewGroup] that contains the ItemViews.
 * @param listener Instance of [PostLikesAdapter.Listener] to receive Navigation events. Defaulted to `null`.
 *
 * @author Kaushik N Sanji
 */
class PostLikeItemViewHolder(
    container: ViewGroup,
    listener: PostLikesAdapter.Listener? = null
) : BaseItemViewHolder<Post.User, PostLikesAdapter.Listener, PostLikeItemViewModel, ItemPostLikeBinding>(
    R.layout.item_post_like,
    container,
    listener,
    viewBindingFactory = ItemPostLikeBinding::inflate
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
        //No-op
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer on the User Name LiveData to set its value on the corresponding textView
        itemViewModel.userName.observe(this) { name ->
            itemViewBinding.textPostLikeItemUserProfileName.text = name
        }

        // Register an observer on the User Handle LiveData to set its value on the corresponding textView
        itemViewModel.userHandle.observe(this) { handle ->
            itemViewBinding.textPostLikeItemUserProfileHandle.text = handle
        }

        // Register an observer on the User's Profile Picture LiveData to load the Image
        // into the corresponding ImageView
        itemViewModel.userImage.observe(this) { image: Image? ->
            ImageUtils.loadImage(
                itemView.context,
                itemViewBinding.imagePostLikeItemUserProfile,
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

    }
}