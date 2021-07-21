/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * [Scope] annotation for scoping the dependencies
 * exposed by [com.mindorks.kaushiknsanji.instagram.demo.di.component.ViewHolderComponent]
 * and for distinguishing with the scoped dependencies exposed by its component
 * [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] dependency.
 */
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ViewHolderScope

/**
 * [Scope] annotation for scoping the dependencies
 * exposed by [com.mindorks.kaushiknsanji.instagram.demo.di.component.DialogFragmentComponent]
 * and for distinguishing with the scoped dependencies exposed by its component
 * [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] dependency.
 */
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class DialogFragmentScope

/**
 * [Scope] annotation for scoping the dependencies
 * exposed by [com.mindorks.kaushiknsanji.instagram.demo.di.component.GlideDependencyComponent]
 * and for distinguishing with the scoped dependencies exposed by its component
 * [com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent] dependency.
 */
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class GlideDependencyScope