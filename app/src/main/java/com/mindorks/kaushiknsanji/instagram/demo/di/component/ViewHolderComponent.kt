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

package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.ViewHolderScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ViewHolderModule
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.likes.PostLikeItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.posts.PostItemViewHolder
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.posts.ProfilePostItemViewHolder
import dagger.Component

/**
 * Dagger Component for exposing dependencies from the Module [ViewHolderModule]
 * and its component [ApplicationComponent] dependency.
 *
 * @author Kaushik N Sanji
 */
@ViewHolderScope
@Component(dependencies = [ApplicationComponent::class], modules = [ViewHolderModule::class])
interface ViewHolderComponent {

    fun inject(postItemViewHolder: PostItemViewHolder)

    fun inject(profilePostItemViewHolder: ProfilePostItemViewHolder)

    fun inject(postLikeItemViewHolder: PostLikeItemViewHolder)

}