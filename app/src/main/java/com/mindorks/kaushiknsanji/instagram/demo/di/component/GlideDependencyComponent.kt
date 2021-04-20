package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.GlideDependencyScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.GlideDependencyModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.ApplicationGlideModule
import dagger.Component

/**
 * Dagger Component for exposing dependencies from the Module [GlideDependencyModule]
 * and its component [ApplicationComponent] dependency.
 *
 * @author Kaushik N Sanji
 */
@GlideDependencyScope
@Component(dependencies = [ApplicationComponent::class], modules = [GlideDependencyModule::class])
interface GlideDependencyComponent {

    fun inject(glideModule: ApplicationGlideModule)

}