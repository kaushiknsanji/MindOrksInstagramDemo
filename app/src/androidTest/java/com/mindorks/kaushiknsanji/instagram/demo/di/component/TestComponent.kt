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

import android.content.Context
import com.mindorks.kaushiknsanji.instagram.demo.di.ApplicationContext
import com.mindorks.kaushiknsanji.instagram.demo.di.OkHttpClientAccessAuth
import com.mindorks.kaushiknsanji.instagram.demo.di.OkHttpClientNoAuth
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationTestModule
import dagger.Component
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

/**
 * Dagger [ApplicationComponent] Test interface for exposing dependencies from the Module [ApplicationTestModule].
 *
 * @author Kaushik N Sanji
 */
@Singleton
@Component(modules = [ApplicationTestModule::class])
interface TestComponent : ApplicationComponent {

    /**
     * Exposes Application [Context] instance
     */
    @ApplicationContext
    override fun getContext(): Context

    /**
     * Exposes the temporary directory [File]
     */
    @TempDirectory
    override fun getTempDirectory(): File

    /**
     * Exposes [OkHttpClient] instance meant for all API Calls except for refreshing Tokens.
     */
    @OkHttpClientAccessAuth
    override fun getHttpClientWithAccessAuthenticator(): OkHttpClient

    /**
     * Exposes [OkHttpClient] instance meant for only refreshing Tokens.
     */
    @OkHttpClientNoAuth
    override fun getHttpClientWithoutAuthenticator(): OkHttpClient
}