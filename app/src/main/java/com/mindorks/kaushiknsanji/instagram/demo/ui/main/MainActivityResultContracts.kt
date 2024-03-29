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

package com.mindorks.kaushiknsanji.instagram.demo.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivityResultContracts.InputExtrasMapOutputBundle
import com.mindorks.kaushiknsanji.instagram.demo.ui.detail.PostDetailActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.like.PostLikeActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity

/**
 * Singleton class for all the activity call contracts required by [MainActivity].
 *
 * @author Kaushik N Sanji
 */
object MainActivityResultContracts {

    /**
     * [ActivityResultContract] for [EditProfileActivity] which can be called
     * with an input [Map] of Intent Extras and produce an output [Bundle].
     *
     * @constructor Creates [ActivityResultContract] for [EditProfileActivity].
     */
    class EditProfile : InputExtrasMapOutputBundle() {

        /**
         * Provides an [Intent] that needs to be used to start the
         * required [EditProfileActivity], without the Intent Extras which will be passed as part of
         * [androidx.activity.result.ActivityResultLauncher.launch]
         *
         * @param context [Context] to create an [Intent].
         *
         * @return Returns the [Intent] to start the required [EditProfileActivity].
         */
        override fun provideIntentWithoutInputExtras(context: Context): Intent =
            Intent(context, EditProfileActivity::class.java)

    }

    /**
     * [ActivityResultContract] for [PostDetailActivity] which can be called
     * with an input [Map] of Intent Extras and produce an output [Bundle].
     *
     * @constructor Create [ActivityResultContract] for [PostDetailActivity].
     */
    class PostDetail : InputExtrasMapOutputBundle() {

        /**
         * Provides an [Intent] that needs to be used to start the
         * required [PostDetailActivity], without the Intent Extras which will be passed as part of
         * [androidx.activity.result.ActivityResultLauncher.launch]
         *
         * @param context [Context] to create an [Intent].
         *
         * @return Returns the [Intent] to start the required [PostDetailActivity].
         */
        override fun provideIntentWithoutInputExtras(context: Context): Intent =
            Intent(context, PostDetailActivity::class.java)

    }

    /**
     * [ActivityResultContract] for [PostLikeActivity] which can be called
     * with an input [Map] of Intent Extras and produce an output [Bundle].
     *
     * @constructor Create [ActivityResultContract] for [PostLikeActivity].
     */
    class PostLike : InputExtrasMapOutputBundle() {

        /**
         * Provides an [Intent] that needs to be used to start the
         * required [PostLikeActivity], without the Intent Extras which will be passed as part of
         * [androidx.activity.result.ActivityResultLauncher.launch]
         *
         * @param context [Context] to create an [Intent].
         *
         * @return Returns the [Intent] to start the required [PostLikeActivity].
         */
        override fun provideIntentWithoutInputExtras(context: Context): Intent =
            Intent(context, PostLikeActivity::class.java)

    }

}