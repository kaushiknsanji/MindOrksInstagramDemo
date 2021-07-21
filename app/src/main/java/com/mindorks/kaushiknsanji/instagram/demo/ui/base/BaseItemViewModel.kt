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

package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [BaseViewModel] abstract subclass for providing abstraction to common tasks of RecyclerView's ItemViews.
 *
 * @param T The type of ItemView's data.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseItemViewModel<T : Any>(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for ItemView's data
    val itemData: MutableLiveData<T> = MutableLiveData()

    /**
     * Posts the new [data] to the LiveData [itemData]
     */
    fun updateItemData(data: T) {
        itemData.postValue(data)
    }

    /**
     * Method to initiate the cleanup manually when the ItemView's ViewModel is no longer used.
     */
    fun onManualCleared() = onCleared()

}