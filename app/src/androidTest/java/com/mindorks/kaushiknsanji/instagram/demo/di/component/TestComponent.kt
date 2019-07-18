package com.mindorks.kaushiknsanji.instagram.demo.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PhotoRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.PostRepository
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ApplicationContext
import com.mindorks.kaushiknsanji.instagram.demo.di.TempDirectory
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationTestModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
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
     * Exposes [Application] instance
     */
    override fun getApplication(): Application

    /**
     * Exposes Application [Context] instance
     */
    @ApplicationContext
    override fun getContext(): Context

    /**
     * Exposes [CompositeDisposable] instance for Reactive streams
     */
    override fun getCompositeDisposable(): CompositeDisposable

    /**
     * Exposes [SchedulerProvider] instance
     */
    override fun getSchedulerProvider(): SchedulerProvider

    /**
     * Exposes [NetworkHelper] instance
     */
    override fun getNetworkHelper(): NetworkHelper

    /**
     * Exposes [NetworkService] instance
     */
    override fun getNetworkService(): NetworkService

    /**
     * Exposes [SharedPreferences] instance
     */
    override fun getSharedPreferences(): SharedPreferences

    /**
     * Exposes [DatabaseService] instance
     */
    override fun getDatabaseService(): DatabaseService

    /**
     * Exposes [UserRepository] instance created using constructor injection
     */
    override fun getUserRepository(): UserRepository

    /**
     * Exposes [PostRepository] instance created using constructor injection
     */
    override fun getPostRepository(): PostRepository

    /**
     * Exposes [PhotoRepository] instance created using constructor injection
     */
    override fun getPhotoRepository(): PhotoRepository

    /**
     * Exposes the temporary directory [File]
     */
    @TempDirectory
    override fun getTempDirectory(): File

}