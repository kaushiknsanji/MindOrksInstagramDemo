package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseDialogViewModel] subclass for [ConfirmOptionDialogFragment].
 *
 * @author Kaushik N Sanji
 */
class SharedConfirmOptionDialogViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseDialogViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

}