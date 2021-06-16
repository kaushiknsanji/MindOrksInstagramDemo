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