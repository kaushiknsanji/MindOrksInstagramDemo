package com.mindorks.kaushiknsanji.instagram.demo.di.module

import androidx.lifecycle.LifecycleRegistry
import com.mindorks.kaushiknsanji.instagram.demo.di.ViewHolderScope
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseItemViewHolder
import dagger.Module
import dagger.Provides

/**
 * Dagger Module for creating and exposing dependencies, tied to the RecyclerView's ViewHolder Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ViewHolderModule(private val viewHolder: BaseItemViewHolder<*, *>) {

    @ViewHolderScope
    @Provides
    fun provideLifecycleRegistry(): LifecycleRegistry = LifecycleRegistry(viewHolder)

}