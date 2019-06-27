package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseFragment
import dagger.Module
import dagger.Provides

/**
 * Dagger Module for creating and exposing dependencies, tied to the Fragment Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.requireContext()

}