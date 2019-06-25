package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.FragmentScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.FragmentModule
import dagger.Component

/**
 * Dagger Component for exposing dependencies from the Module [FragmentModule]
 * and its component [ApplicationComponent] dependency.
 *
 * @author Kaushik N Sanji
 */
@FragmentScope
@Component(dependencies = [ApplicationComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {
}