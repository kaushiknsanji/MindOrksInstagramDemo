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

package com.mindorks.kaushiknsanji.instagram.demo.utils.rx

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.TestScheduler

/**
 * Class that implements [SchedulerProvider] to provide the Reactive [TestScheduler] instance needed for Testing the App.
 *
 * @author Kaushik N Sanji
 */
class TestSchedulerProvider(private val testScheduler: TestScheduler) : SchedulerProvider {

    /**
     * Returns a default, shared [Scheduler] instance intended for computational work.
     */
    override fun computation(): Scheduler = testScheduler

    /**
     * Returns a default, shared [Scheduler] instance intended for IO-bound work.
     */
    override fun io(): Scheduler = testScheduler

    /**
     * Returns a default [Scheduler] instance intended for work on Android Main Thread.
     */
    override fun ui(): Scheduler = testScheduler

}