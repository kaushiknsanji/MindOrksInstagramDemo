package com.mindorks.kaushiknsanji.instagram.demo.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.di.ActivityContext
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainSharedViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpViewModel
import com.mindorks.kaushiknsanji.instagram.demo.ui.splash.SplashViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.ViewModelProviderFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Dagger Module for creating and exposing dependencies, tied to the Activity Lifecycle.
 *
 * @author Kaushik N Sanji
 */
@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity

    /**
     * Provides instance of [SplashViewModel]
     */
    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SplashViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(SplashViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(SplashViewModel::class.java)


    /**
     * Provides instance of [LoginViewModel]
     */
    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): LoginViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(LoginViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        LoginViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(LoginViewModel::class.java)

    /**
     * Provides instance of [SignUpViewModel]
     */
    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SignUpViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(SignUpViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        SignUpViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
    }).get(SignUpViewModel::class.java)

    /**
     * Provides instance of [MainViewModel]
     */
    @Provides
    fun provideMainViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(MainViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        MainViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(MainViewModel::class.java)

    /**
     * Provides instance of [MainSharedViewModel]
     */
    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel = ViewModelProviders.of(activity, ViewModelProviderFactory(MainSharedViewModel::class) {
        // [creator] lambda that creates and returns the ViewModel instance
        MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(MainSharedViewModel::class.java)
}