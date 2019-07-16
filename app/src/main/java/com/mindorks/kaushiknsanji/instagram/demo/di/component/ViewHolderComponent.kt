package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.ViewHolderScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ViewHolderModule
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

}