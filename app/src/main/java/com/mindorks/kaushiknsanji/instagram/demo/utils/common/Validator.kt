package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mindorks.kaushiknsanji.instagram.demo.R
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Validation.Field

/**
 * Utility object for validation of fields in [com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity]
 * and [com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity]
 *
 * @author Kaushik N Sanji
 */
object Validator {

    // Constant for the minimum password length
    private const val MIN_PASSWORD_LENGTH = 6

    /**
     * Method that validates [email] and [password], and returns a resulting list of [Validation]s.
     * Required for [com.mindorks.kaushiknsanji.instagram.demo.ui.login.LoginActivity]
     */
    fun validateLoginFields(email: String?, password: String?): List<Validation> =
        mutableListOf<Validation>().apply {
            // Building the Validation results

            // For [email]
            validateEmail(email)

            // For [password]
            validatePassword(password)
        }

    /**
     * Method that validates [email], [password] and [name], and returns a resulting list of [Validation]s.
     * Required for [com.mindorks.kaushiknsanji.instagram.demo.ui.signup.SignUpActivity]
     */
    fun validateSignUpFields(email: String?, password: String?, name: String?): List<Validation> =
        mutableListOf<Validation>().apply {
            // Building the Validation results

            // For [email]
            validateEmail(email)

            // For [password]
            validatePassword(password)

            // For [name]
            validateUserName(name)
        }

    /**
     * Method that validates [name] and returns a resulting list of [Validation]s.
     * Required for [com.mindorks.kaushiknsanji.instagram.demo.ui.profile.edit.EditProfileActivity]
     */
    fun validateEditProfileFields(name: String?): List<Validation> =
        mutableListOf<Validation>().apply {
            // Building the Validation results

            // For [name]
            validateUserName(name)
        }

    /**
     * Validates [email] and appends the results of [Validation]
     */
    private fun MutableList<Validation>.validateEmail(email: String?) {
        when {
            // When email is empty or blank, build a Validation with an appropriate error message
            email.isNullOrBlank() ->
                add(Validation(Field.EMAIL, Resource.error(R.string.error_login_sign_up_email_field_empty)))
            // When the email is invalid, build a Validation with an appropriate error message
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
                add(
                    Validation(
                        Field.EMAIL,
                        Resource.error(R.string.error_login_sign_up_email_field_invalid)
                    )
                )
            // When the email is valid, build a Validation with success resource
            else ->
                add(Validation(Field.EMAIL, Resource.success()))
        }
    }

    /**
     * Validates [password] and appends the results of [Validation]
     */
    private fun MutableList<Validation>.validatePassword(password: String?) {
        when {
            // When password is empty or blank, build a Validation with an appropriate error message
            password.isNullOrBlank() ->
                add(
                    Validation(
                        Field.PASSWORD,
                        Resource.error(R.string.error_login_sign_up_password_field_empty)
                    )
                )
            // When password length is below the required criteria, build a Validation
            // with an appropriate error message
            password.length < MIN_PASSWORD_LENGTH ->
                add(
                    Validation(
                        Field.PASSWORD,
                        Resource.error(R.string.error_login_sign_up_password_field_small_length)
                    )
                )
            // When password is valid, build a Validation with success resource
            else ->
                add(Validation(Field.PASSWORD, Resource.success()))
        }
    }

    /**
     * Validates [name] and appends the results of [Validation]
     */
    private fun MutableList<Validation>.validateUserName(name: String?) {
        when {
            // When name is empty or blank, build a Validation with an appropriate error message
            name.isNullOrBlank() ->
                add(Validation(Field.NAME, Resource.error(R.string.error_sign_up_name_field_empty)))
            // When name is valid, build a Validation with success resource
            else ->
                add(Validation(Field.NAME, Resource.success()))
        }
    }

}

/**
 * Class with [Field] metadata on the message [resource] wrapped.
 *
 * @property field [Field] enum for [Field.EMAIL], [Field.PASSWORD] and [Field.NAME] types
 * @property resource A [Resource] wrapper for an android string resource id.
 * @constructor Creates an Instance of [Validation]
 *
 * @author Kaushik N Sanji
 */
data class Validation(val field: Field, val resource: Resource<Int>) {

    /**
     * [Field] enums for Email, Password and Name fields
     */
    enum class Field {
        EMAIL,
        PASSWORD,
        NAME
    }

}

/**
 * Extension function on [LiveData] of [List] of [Validation]s that transforms
 * the [List] of [Validation]s to a [LiveData] of [Resource] for the
 * required [field] being validated.
 */
fun LiveData<List<Validation>>.filterValidation(field: Field): LiveData<Resource<Int>> =
    Transformations.map(this) { validations ->
        validations.find { validation -> validation.field == field }
            ?.resource
            ?: Resource.unknown()
    }