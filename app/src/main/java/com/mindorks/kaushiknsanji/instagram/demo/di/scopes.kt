package com.mindorks.kaushiknsanji.instagram.demo.di

import javax.inject.Scope

/**
 * Kotlin file for all the "Scope" annotations used in the app.
 *
 * @author Kaushik N Sanji
 */

/**
 * [Scope] annotation for scoping the dependencies
 * exposed by [com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent]
 * and for distinguishing with the scoped dependencies exposed by its component
 * [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] dependency.
 */
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityScope

/**
 * [Scope] annotation for scoping the dependencies
 * exposed by [com.mindorks.kaushiknsanji.instagram.demo.di.component.FragmentComponent]
 * and for distinguishing with the scoped dependencies exposed by its component
 * [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] dependency.
 */
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentScope