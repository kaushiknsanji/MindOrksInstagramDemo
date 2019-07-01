package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.FragmentScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.FragmentModule
import com.mindorks.kaushiknsanji.instagram.demo.ui.home.HomeFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.photo.PhotoFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.profile.ProfileFragment
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

    fun inject(homeFragment: HomeFragment)

    fun inject(photoFragment: PhotoFragment)

    fun inject(profileFragment: ProfileFragment)

}