package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import dagger.Module
import dagger.Provides

/**
 * Dagger Module for creating and exposing dependencies, tied to the Activity Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity

}