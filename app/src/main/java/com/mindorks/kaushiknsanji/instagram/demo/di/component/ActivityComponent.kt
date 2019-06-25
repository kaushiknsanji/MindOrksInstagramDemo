package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ActivityModule
import dagger.Component

/**
 * Dagger Component for exposing dependencies from the Module [ActivityModule]
 * and its component [ApplicationComponent] dependency.
 *
 * @author Kaushik N Sanji
 */
@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
}