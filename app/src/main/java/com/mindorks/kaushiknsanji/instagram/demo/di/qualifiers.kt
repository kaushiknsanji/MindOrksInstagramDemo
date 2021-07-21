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

/**
 * [Qualifier] annotation used for distinguishing the [okhttp3.OkHttpClient]
 * provided by [com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule]
 * which is meant for all API Calls except for refreshing Tokens.
 */
@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class OkHttpClientAccessAuth

/**
 * [Qualifier] annotation used for distinguishing the [okhttp3.OkHttpClient]
 * provided by [com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule]
 * which is meant for only refreshing Tokens.
 */
@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class OkHttpClientNoAuth