package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import dagger.Module
import dagger.Provides

/**
 * Dagger Module for creating and exposing dependencies, tied to the Activity Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity

}