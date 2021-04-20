package com.mindorks.kaushiknsanji.instagram.demo.di.module

import com.bumptech.glide.module.AppGlideModule
import dagger.Module

/**
 * Dagger Module for creating and exposing dependencies, tied to the base [AppGlideModule] of Glide library.
 *
 * @author Kaushik N Sanji
 */
@Module
class GlideDependencyModule(private val glideModule: AppGlideModule) {

}