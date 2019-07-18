package com.mindorks.kaushiknsanji.instagram.demo.utils.rx

import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

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