package com.mindorks.kaushiknsanji.instagram.demo.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.repository.UserRepository
import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseViewModel
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.network.NetworkHelper
import com.mindorks.kaushiknsanji.instagram.demo.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * [BaseViewModel] subclass for [LoginActivity]
 *
 * @property userRepository [UserRepository] instance for [User] data.
 *
 * @author Kaushik N Sanji
 */
class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Login progress indication
    val loginProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the Field values in the Login Screen
    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()

    // LiveData for the Validations of the Field values
    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    // LiveData for Email validation results
    val emailValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.EMAIL)

    // LiveData for Password validation results
    val passwordValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.PASSWORD)

    // LiveData for launching MainActivity
    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    // LiveData for launching SignUpActivity
    val launchSignUp: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    /**
     * Callback method to be implemented, which will be called when this ViewModel's Activity/Fragment is created.
     */
    override fun onCreate() {
        //No-op
    }

    /**
     * Called when there is a change in the Email field.
     * This method posts the change to [emailField] LiveData
     */
    fun onEmailChange(email: String) = emailField.postValue(email)

    /**
     * Called when there is a change in the Password field.
     * This method posts the change to [passwordField] LiveData
     */
    fun onPasswordChange(password: String) = passwordField.postValue(password)

    /**
     * Called when the user clicks on the Login Button to login. Performs the login operation with the Remote API
     * after passing the necessary validations.
     */
    fun onLogin() {
        // Get the values of the fields
        val emailValue: String? = emailField.value
        val passwordValue: String? = passwordField.value

        // Do the validation
        val validations = Validator.validateLoginFields(emailValue, passwordValue)
        // Update the validations LiveData to trigger Email and Password validation results LiveData
        validationsList.postValue(validations)

        if (emailValue != null && passwordValue != null
            && validations.isNotEmpty()
            && validations.count { validation -> validation.resource.status == Status.SUCCESS } == validations.size
            && checkInternetConnectionWithMessage()
        ) {
            // Perform the Login operation when we have the email, password, right number of success validations
            // and internet connectivity

            // Start the Login Progress indication
            loginProgress.postValue(true)

            compositeDisposable.add(
                // Make the Login API Call and save the resulting disposable
                userRepository.doUserLogin(emailValue, passwordValue)
                    .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { user: User ->
                            // Save the successfully logged-in user information
                            userRepository.saveCurrentUser(user)
                            // Stop the Login Progress indication
                            loginProgress.postValue(false)
                            // Launch MainActivity
                            launchMain.postValue(Event(emptyMap()))
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Stop the Login Progress indication
                            loginProgress.postValue(false)
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )

        }

    }

    /**
     * Called when the user chooses to "Sign Up with Email" by clicking on the corresponding button.
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity]
     */
    fun onSignUpWithEmail() {
        launchSignUp.postValue(Event(emptyMap()))
    }

}