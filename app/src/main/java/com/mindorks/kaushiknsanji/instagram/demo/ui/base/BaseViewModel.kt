package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.net.ssl.HttpsURLConnection

/**
 * An abstract base [ViewModel] for all the ViewModels in the app, that provides abstraction to common tasks.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseViewModel(
    protected val schedulerProvider: SchedulerProvider,
    protected val compositeDisposable: CompositeDisposable,
    protected val networkHelper: NetworkHelper
) : ViewModel() {

    // LiveData for the Messages that needs to be shown in Toast/Snackbar
    val messageString: MutableLiveData<Resource<String>> = MutableLiveData()
    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()

    /**
     * Method that checks the Internet connectivity.
     *
     * @return Returns `true` if connected; `false` otherwise.
     */
    protected fun checkInternetConnection(): Boolean = networkHelper.isNetworkConnected()

    /**
     * Method that checks the Internet connectivity. If not connected, then an error message
     * will be posted to [messageStringId] LiveData.
     *
     * @return Returns `true` if connected; `false` otherwise.
     */
    protected fun checkInternetConnectionWithMessage(): Boolean =
        if (checkInternetConnection()) {
            true
        } else {
            // Post an error message when not connected
            messageStringId.postValue(Resource.error(R.string.error_network_connection_issue))
            false
        }

    /**
     * Method that checks for the [err] and posts an appropriate error message to [messageStringId] LiveData.
     */
    protected fun handleNetworkError(err: Throwable?) =
        err?.let {
            // When we have an error, convert it to NetworkError and post error messages based on its status
            networkHelper.castToNetworkError(err).run {
                when (status) {
                    // For default error
                    -1 -> messageStringId.postValue(Resource.error(R.string.error_network_default_issue))
                    // For Connect exceptions
                    0 -> messageStringId.postValue(Resource.error(R.string.error_network_server_connection_issue))
                    // For HTTP 401 error
                    HttpsURLConnection.HTTP_UNAUTHORIZED -> messageStringId.postValue(
                        Resource.error(
                            R.string.error_network_login_unauthorized_issue
                        )
                    )
                    // For HTTP 500 error
                    HttpsURLConnection.HTTP_INTERNAL_ERROR ->
                        messageStringId.postValue(Resource.error(R.string.error_network_internal_issue))
                    // For HTTP 503 error
                    HttpsURLConnection.HTTP_UNAVAILABLE ->
                        messageStringId.postValue(Resource.error(R.string.error_network_server_not_available_issue))
                    // For other errors
                    else -> messageString.postValue(Resource.error(message))
                }
            }
        }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    override fun onCleared() {
        // Clear all disposable resources of Rx streams in the end
        compositeDisposable.dispose()
        super.onCleared()
    }

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    abstract fun onCreate()

}