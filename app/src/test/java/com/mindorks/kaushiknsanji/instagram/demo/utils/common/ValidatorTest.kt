package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import com.mindorks.kaushiknsanji.instagram.demo.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

/**
 * Local Unit Test on [Validator].
 *
 * @author Kaushik N Sanji
 */
class ValidatorTest {

    @Test
    fun givenValidEmailAndPwd_whenValidate_shouldReturnSuccess() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        assertThat(validations, Matchers.hasSize(2))
        // Assert that Validations contains EMAIL and PASSWORD Success Validations
        assertThat(
            validations,
            Matchers.contains(
                Validation(Validation.Field.EMAIL, Resource.Success()),
                Validation(Validation.Field.PASSWORD, Resource.Success())
            )
        )
    }

    @Test
    fun givenInvalidEmailAndValidPwd_whenValidate_shouldReturnEmailError() {
        // Invalid Email
        val email = "test"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        assertThat(validations, Matchers.hasSize(2))
        // Assert that Validations contains EMAIL Error and PASSWORD Success Validations
        assertThat(
            validations,
            Matchers.contains(
                Validation(
                    Validation.Field.EMAIL,
                    Resource.Error(R.string.error_login_sign_up_email_field_invalid)
                ),
                Validation(Validation.Field.PASSWORD, Resource.Success())
            )
        )
    }

    @Test
    fun givenValidEmailAndInvalidPwd_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"

        // Call Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        assertThat(validations, Matchers.hasSize(2))
        // Assert that Validations contains EMAIL Success and PASSWORD Error Validations
        assertThat(
            validations,
            Matchers.contains(
                Validation(Validation.Field.EMAIL, Resource.Success()),
                Validation(
                    Validation.Field.PASSWORD,
                    Resource.Error(R.string.error_login_sign_up_password_field_small_length)
                )
            )
        )
    }

    @Test
    fun givenEmptyEmailAndValidPwd_whenValidate_shouldReturnEmailError() {
        // Empty Email
        val email = ""
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        assertThat(validations, Matchers.hasSize(2))
        // Assert that Validations contains EMAIL empty Error and PASSWORD Success Validations
        assertThat(
            validations,
            Matchers.contains(
                Validation(
                    Validation.Field.EMAIL,
                    Resource.Error(R.string.error_login_sign_up_email_field_empty)
                ),
                Validation(Validation.Field.PASSWORD, Resource.Success())
            )
        )
    }

    @Test
    fun givenValidEmailAndEmptyPwd_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Empty Password
        val password = ""

        // Call Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        assertThat(validations, Matchers.hasSize(2))
        // Assert that Validations contains EMAIL Success and PASSWORD empty Error Validations
        assertThat(
            validations,
            Matchers.contains(
                Validation(Validation.Field.EMAIL, Resource.Success()),
                Validation(
                    Validation.Field.PASSWORD,
                    Resource.Error(R.string.error_login_sign_up_password_field_empty)
                )
            )
        )
    }

}