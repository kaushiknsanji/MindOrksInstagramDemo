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

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

/**
 * [BaseItemViewModel] subclass for [PostLikeItemViewHolder].
 *
 * @param userRepository [UserRepository] instance for [User] data.
 * @constructor Instance of [PostLikeItemViewModel] created and provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
class PostLikeItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository
) : BaseItemViewModel<Post.User>(schedulerProvider, compositeDisposable, networkHelper) {

    // Stores the logged-in [User] information
    private val user: User = userRepository.getCurrentUser()

    // Prepare the headers for the Image download requests
    private val headers: Map<String, String> = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    // Transforms [itemData] to get the Name of the User who liked the Post
    val userName: LiveData<String> = itemData.map { user -> user.name }

    // Transforms [userName] to create a dummy user handle based on the user name
    val userHandle: LiveData<String> = userName.map { name: String ->
        // Convert all to lowercase and remove whitespaces if any
        name.lowercase(Locale.getDefault()).replace("\\s".toRegex(), "")
    }

    // Transforms [itemData] to get the [Image] model of the Profile Picture, of the User who liked the Post
    val userImage: LiveData<Image?> = itemData.map { user ->
        user.profilePicUrl?.run { Image(url = this, headers = headers) }
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

}