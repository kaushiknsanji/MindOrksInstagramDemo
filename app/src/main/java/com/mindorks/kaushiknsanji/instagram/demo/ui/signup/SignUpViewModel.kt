package com.mindorks.kaushiknsanji.instagram.demo.ui.signup

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
 * [BaseViewModel] subclass for [SignUpActivity]
 *
 * @property userRepository [UserRepository] instance for [User] data.
 *
 * @author Kaushik N Sanji
 */
class SignUpViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    // LiveData for the Sign-Up Progress indication
    val signUpProgress: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData for the Field values in the Sign-Up Screen
    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()
    val nameField: MutableLiveData<String> = MutableLiveData()

    // LiveData for the Validations of the Field values
    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    // LiveData for Email validation results
    val emailValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.EMAIL)

    // LiveData for Password validation results
    val passwordValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.PASSWORD)

    // LiveData for Name validation results
    val nameValidation: LiveData<Resource<Int>> =
        validationsList.filterValidation(Validation.Field.NAME)

    // LiveData for launching MainActivity
    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    // LiveData for launching LoginActivity
    val launchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

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
     * Called when there is a change in the Name field.
     * This method posts the change to [nameField] LiveData
     */
    fun onNameChange(name: String) = nameField.postValue(name)

    /**
     * Called when the user clicks on the "Sign Up" Button to register. Performs the Sign-up operation with the Remote API
     * after passing the necessary validations.
     */
    fun onSignUp() {
        // Get the values of the fields
        val emailValue: String? = emailField.value
        val passwordValue: String? = passwordField.value
        val nameValue: String? = nameField.value

        // Do the validation
        val validations = Validator.validateSignUpFields(emailValue, passwordValue, nameValue)
        // Update the validations LiveData to trigger Email, Password and Name validation results LiveData
        validationsList.postValue(validations)

        if (emailValue != null && passwordValue != null && nameValue != null
            && validations.isNotEmpty()
            && validations.count { validation -> validation.resource.status == Status.SUCCESS } == validations.size
            && checkInternetConnectionWithMessage()
        ) {
            // Perform the Sign-Up operation when we have the email, password, name, right number of success validations
            // and internet connectivity

            // Start the Sign-Up Progress indication
            signUpProgress.postValue(true)

            compositeDisposable.addAll(
                // Make the SignUp API Call and save the resulting disposable
                userRepository.doUserSignUp(emailValue, passwordValue, nameValue)
                    .subscribeOn(schedulerProvider.io()) // Operate on IO Thread
                    .subscribe(
                        // OnSuccess
                        { user: User ->
                            // Save the successfully registered user information
                            userRepository.saveCurrentUser(user)
                            // Stop the Sign-Up Progress indication
                            signUpProgress.postValue(false)
                            // Launch MainActivity
                            launchMain.postValue(Event(emptyMap()))
                        },
                        // OnError
                        { throwable: Throwable? ->
                            // Stop the Sign-Up Progress indication
                            signUpProgress.postValue(false)
                            // Handle and display the appropriate error
                            handleNetworkError(throwable)
                        }
                    )
            )

        }
    }

    /**
     * Called when the user chooses to "Login with Email" by clicking on the corresponding button.
     * Triggers an event to launch the [com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity]
     */
    fun onLoginWithEmail() {
        launchLogin.postValue(Event(emptyMap()))
    }

}