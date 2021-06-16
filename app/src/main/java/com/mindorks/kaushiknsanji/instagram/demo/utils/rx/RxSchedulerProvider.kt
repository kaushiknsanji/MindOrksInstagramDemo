package com.mindorks.kaushiknsanji.instagram.demo.utils.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Class that implements [SchedulerProvider] to provide the Reactive [Scheduler] instances needed for the App.
 *
 * @author Kaushik N Sanji
 */
class RxSchedulerProvider : SchedulerProvider {

    /**
     * Returns a default, shared [Scheduler] instance intended for computational work.
     */
    override fun computation(): Scheduler = Schedulers.computation()

    /**
     * Returns a default, shared [Scheduler] instance intended for IO-bound work.
     */
    override fun io(): Scheduler = Schedulers.io()

    /**
     * Returns a default [Scheduler] instance intended for work on Android Main Thread.
     */
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

}