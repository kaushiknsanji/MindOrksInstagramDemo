package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mindorks.kaushiknsanji.instagram.demo.BuildConfig
import com.mindorks.kaushiknsanji.instagram.demo.InstagramApplication
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.di.ApplicationContext
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.RxSchedulerProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/**
 * Dagger Module for creating and exposing dependencies, tied to the Application Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ApplicationModule(private val application: InstagramApplication) {

    @Singleton
    @Provides
    fun provideApplication(): Application = application

    @Singleton
    @ApplicationContext
    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)

    @Singleton
    @Provides
    fun provideNetworkService(): NetworkService =
        Networking.create(
            BuildConfig.API_KEY,
            BuildConfig.BASE_URL,
            application.cacheDir,
            10 * 1024 * 1024 // 10MB Cache Size
        )

    @Singleton
    @Provides
    fun provideSharedPreferences(): SharedPreferences =
        application.getSharedPreferences("bootcamp-instagram-project-prefs", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideDatabaseService(): DatabaseService =
        Room.databaseBuilder(
            application,
            DatabaseService::class.java,
            "bootcamp-instagram-project-db"
        ).build()

}