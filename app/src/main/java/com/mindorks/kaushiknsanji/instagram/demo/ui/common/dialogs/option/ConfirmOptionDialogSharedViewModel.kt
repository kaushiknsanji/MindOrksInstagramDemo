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

package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseDialogViewModel] subclass for [ConfirmOptionDialogFragment].
 *
 * @author Kaushik N Sanji
 */
class ConfirmOptionDialogSharedViewModel(
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