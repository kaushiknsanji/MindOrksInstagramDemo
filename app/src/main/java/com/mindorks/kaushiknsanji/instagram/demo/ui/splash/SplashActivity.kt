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

package com.mindorks.kaushiknsanji.instagram.demo.ui.splash

import android.content.Intent
import android.os.Bundle
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.di.component.ActivityComponent
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity
import com.mindorks.kaushiknsanji.instagram.demo.ui.main.MainActivity
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent

/**
 * [BaseActivity] subclass that inflates the layout 'R.layout.activity_splash' to show a splash screen.
 * [SplashViewModel] is the primary [androidx.lifecycle.ViewModel] of this Activity.
 *
 * @author Kaushik N Sanji
 */
class SplashActivity : BaseActivity<SplashViewModel>() {

    companion object {
        const val TAG = "SplashActivity"
    }

    /**
     * Injects dependencies exposed by [ActivityComponent] into Activity.
     */
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    /**
     * Provides the Resource Layout Id for the Activity.
     */
    override fun provideLayoutId(): Int = R.layout.activity_splash

    /**
     * Initializes the Layout of the Activity.
     */
    override fun setupView(savedInstanceState: Bundle?) {
        //No-op
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer for MainActivity launch events
        viewModel.launchMain.observeEvent(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Terminate this activity
        }

        // Register an observer for LoginActivity launch events
        viewModel.launchLogin.observeEvent(this) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Terminate this activity
        }
    }

}
