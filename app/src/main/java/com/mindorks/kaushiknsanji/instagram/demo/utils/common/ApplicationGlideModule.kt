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

package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.di.OkHttpClientAccessAuth
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ApplicationComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.DaggerGlideDependencyComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.component.GlideDependencyComponent
import com.mindorks.kaushiknsanji.instagram.demo.di.module.GlideDependencyModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.inject.Inject

/**
 * Application-wide Configurator Utility for [com.bumptech.glide.Glide]
 *
 * @author Kaushik N Sanji
 */
@Excludes(value = [OkHttpLibraryGlideModule::class])
@GlideModule
class ApplicationGlideModule : AppGlideModule() {

    // Instance of OkHttpClient provided by Dagger
    @OkHttpClientAccessAuth
    @Inject
    lateinit var okHttpClient: OkHttpClient

    /**
     * Returns `false` as there are no legacy [GlideModule]s being registered via AndroidManifest.
     */
    override fun isManifestParsingEnabled(): Boolean = false

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        Logger.i(TAG, "Registering Glide components...")

        // Inject the required dependencies
        injectDependencies(buildGlideDependencyComponent(context))

        // Use the App's custom OkHttpClient with Access Authenticator, to be able to
        // re-authenticate requests when the access token passed in the Headers expire.
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }

    /**
     * Injects dependencies exposed by [glideDependencyComponent] into this module.
     */
    private fun injectDependencies(glideDependencyComponent: GlideDependencyComponent) {
        glideDependencyComponent.inject(this)
    }

    /**
     * Builds and provides the [Dagger component][GlideDependencyComponent] for Glide Module.
     *
     * @param context [Context] of the application to retrieve [ApplicationComponent].
     */
    @Suppress("DEPRECATION")
    private fun buildGlideDependencyComponent(context: Context): GlideDependencyComponent =
        DaggerGlideDependencyComponent.builder()
            .applicationComponent((context as InstagramApplication).applicationComponent)
            .glideDependencyModule(GlideDependencyModule(this))
            .build()

    companion object {
        // Constant used for logs
        const val TAG = "ApplicationGlideModule"
    }
}