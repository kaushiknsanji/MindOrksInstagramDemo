package com.mindorks.kaushiknsanji.instagram.demo.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ApplicationContext
import com.mindorks.kaushiknsanji.instagram.demo.di.module.ApplicationModule
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
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

}