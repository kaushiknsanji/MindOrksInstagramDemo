package com.mindorks.kaushiknsanji.instagram.demo.utils.rx

import io.reactivex.rxjava3.core.Scheduler


/**
 * Interface for providing the Reactive [Scheduler]s.
 *
 * @author Kaushik N Sanji
 */
interface SchedulerProvider {

    /**
     * Returns a default, shared [Scheduler] instance intended for computational work.
     */
    fun computation(): Scheduler

    /**
     * Returns a default, shared [Scheduler] instance intended for IO-bound work.
     */
    fun io(): Scheduler

    /**
     * Returns a default [Scheduler] instance intended for work on Android Main Thread.
     */
    fun ui(): Scheduler

}