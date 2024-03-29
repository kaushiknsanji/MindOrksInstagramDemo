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

package com.mindorks.kaushiknsanji.instagram.demo.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.TokenService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.auth.AccessTokenAuthenticator
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.auth.AccessTokenProvider
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.TokenRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ApplicationContext
import com.mindorks.kaushiknsanji.instagram.demo.di.OkHttpClientAccessAuth
import com.mindorks.kaushiknsanji.instagram.demo.di.OkHttpClientNoAuth
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.rxjava3.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import javax.inject.Singleton

/**
 * Dagger Component for exposing dependencies from the Module [ApplicationModule].
 *
 * @author Kaushik N Sanji
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    /**
     * Exposes [Application] instance
     */
    fun getApplication(): Application

    /**
     * Exposes Application [Context] instance
     */
    @ApplicationContext
    fun getContext(): Context

    /**
     * Exposes [CompositeDisposable] instance for Reactive streams
     */
    fun getCompositeDisposable(): CompositeDisposable

    /**
     * Exposes [SchedulerProvider] instance
     */
    fun getSchedulerProvider(): SchedulerProvider

    /**
     * Exposes [NetworkHelper] instance
     */
    fun getNetworkHelper(): NetworkHelper

    /**
     * Exposes [HttpLoggingInterceptor] instance
     */
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor

    /**
     * Exposes [OkHttpClient] instance meant for all API Calls except for refreshing Tokens.
     */
    @OkHttpClientAccessAuth
    fun getHttpClientWithAccessAuthenticator(): OkHttpClient

    /**
     * Exposes [OkHttpClient] instance meant for only refreshing Tokens.
     */
    @OkHttpClientNoAuth
    fun getHttpClientWithoutAuthenticator(): OkHttpClient

    /**
     * Exposes [NetworkService] instance
     */
    fun getNetworkService(): NetworkService

    /**
     * Exposes [SharedPreferences] instance
     */
    fun getSharedPreferences(): SharedPreferences

    /**
     * Exposes [DatabaseService] instance
     */
    fun getDatabaseService(): DatabaseService

    /**
     * Exposes [UserRepository] instance created using constructor injection
     */
    fun getUserRepository(): UserRepository

    /**
     * Exposes [PostRepository] instance created using constructor injection
     */
    fun getPostRepository(): PostRepository

    /**
     * Exposes [PhotoRepository] instance created using constructor injection
     */
    fun getPhotoRepository(): PhotoRepository

    /**
     * Exposes the temporary directory [File]
     */
    @TempDirectory
    fun getTempDirectory(): File

    /**
     * Exposes [TokenService] instance
     */
    fun getTokenService(): TokenService

    /**
     * Exposes [TokenRepository] instance created using constructor injection
     */
    fun getTokenRepository(): TokenRepository

    /**
     * Exposes [AccessTokenProvider] instance created using constructor injection
     */
    fun getAccessTokenProvider(): AccessTokenProvider

    /**
     * Exposes [AccessTokenAuthenticator] instance created using constructor injection
     */
    fun getAccessTokenAuthenticator(): AccessTokenAuthenticator

}