package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata
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

    // LiveData for Dialog Type
    private val dialogTypeStr: MutableLiveData<String> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when the Dialog type identifier needs to be set/changed to [type]
     */
    private fun onDialogTypeChange(type: String) {
        dialogTypeStr.postValue(type)
    }

    /**
     * Checks whether the Dialog type of this [ConfirmOptionDialogFragment] is [dialogType].
     *
     * @return `true` if the Dialog is of the given [dialogType]; `false` otherwise.
     */
    fun isDialogType(dialogType: String): Boolean = dialogTypeStr.value == dialogType

    /**
     * Called when [ConfirmOptionDialogFragment]'s custom [metadata] needs to be set/changed.
     */
    override fun onDialogMetadataChange(metadata: BaseDialogMetadata) {
        super.onDialogMetadataChange(metadata)

        // Applying changes for this custom dialog using its metadata
        (metadata as ConfirmOptionDialogMetadata).run {
            onDialogTypeChange(dialogType)
        }
    }
}