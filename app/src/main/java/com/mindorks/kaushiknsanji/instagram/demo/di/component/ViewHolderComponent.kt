package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.ViewHolderScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ViewHolderModule
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
}