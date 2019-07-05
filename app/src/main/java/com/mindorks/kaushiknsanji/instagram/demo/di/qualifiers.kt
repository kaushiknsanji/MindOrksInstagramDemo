package com.mindorks.kaushiknsanji.instagram.demo.di

import javax.inject.Qualifier

/**
 * Kotlin file for all the "Qualifier" annotations used in the app.
 *
 * @author Kaushik N Sanji
 */

/**
 * [Qualifier] annotation used for distinguishing the [android.content.Context]
 * provided by [com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule]
 */
@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class ApplicationContext

/**
 * [Qualifier] annotation used for distinguishing the [android.content.Context]
 * provided by [com.mindorks.kaushiknsanji.instagram.demo.di.module.ActivityModule]
 */
@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityContext

/**
 * [Qualifier] annotation used for distinguishing the directory [java.io.File]
 * provided by [com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule]
 */
@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class TempDirectory