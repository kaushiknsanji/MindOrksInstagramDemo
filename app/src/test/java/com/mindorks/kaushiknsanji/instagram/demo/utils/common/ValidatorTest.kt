package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import com.google.common.truth.Truth
import com.mindorks.kaushiknsanji.instagram.demo.R
import org.junit.Test

/**
 * Local Unit Test on [Validator].
 *
 * @author Kaushik N Sanji
 */
class ValidatorTest {

    @Test
    fun givenValidEmailValidPwd_whenValidate_shouldReturnSuccess() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Login Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        Truth.assertThat(validations).hasSize(2)
        // Assert that Validations contains EMAIL and PASSWORD Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(Validation.Field.PASSWORD, Resource.Success())
        )
    }

    @Test
    fun givenInvalidEmailValidPwd_whenValidate_shouldReturnEmailError() {
        // Invalid Email
        val email = "test"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Login Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        Truth.assertThat(validations).hasSize(2)
        // Assert that Validations contains EMAIL Error and PASSWORD Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_invalid)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success())
        )
    }

    @Test
    fun givenValidEmailInvalidPwd_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"

        // Call Login Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        Truth.assertThat(validations).hasSize(2)
        // Assert that Validations contains EMAIL Success and PASSWORD Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_small_length)
            )
        )
    }

    @Test
    fun givenEmptyEmailValidPwd_whenValidate_shouldReturnEmailError() {
        // Empty Email
        val email = ""
        // Valid Password (greater than 6 min-character-length)
        val password = "password"

        // Call Login Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        Truth.assertThat(validations).hasSize(2)
        // Assert that Validations contains EMAIL empty Error and PASSWORD Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_empty)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success())
        )
    }

    @Test
    fun givenValidEmailEmptyPwd_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Empty Password
        val password = ""

        // Call Login Validator to get the Validation results
        val validations = Validator.validateLoginFields(email, password)

        // Test the Validation results
        // Assert that Validations has 2 results
        Truth.assertThat(validations).hasSize(2)
        // Assert that Validations contains EMAIL Success and PASSWORD Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_empty)
            )
        )
    }

    @Test
    fun givenValidEmailValidPwdValidName_whenValidate_shouldReturnSuccess() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Valid UserName
        val name = "Mike"

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL, PASSWORD and NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(Validation.Field.NAME, Resource.Success())
        )
    }

    @Test
    fun givenInvalidEmailValidPwdValidName_whenValidate_shouldReturnEmailError() {
        // Invalid Email
        val email = "test"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Valid UserName
        val name = "Mike"

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Error, PASSWORD and NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_invalid)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(Validation.Field.NAME, Resource.Success())
        )
    }

    @Test
    fun givenValidEmailInvalidPwdValidName_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"
        // Valid UserName
        val name = "Mike"

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Success, PASSWORD Error and NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_small_length)
            ),
            Validation(Validation.Field.NAME, Resource.Success())
        )
    }

    @Test
    fun givenEmptyEmailValidPwdValidName_whenValidate_shouldReturnEmailError() {
        // Empty Email
        val email = ""
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Valid UserName
        val name = "Mike"

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL empty Error, PASSWORD and NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_empty)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(Validation.Field.NAME, Resource.Success())
        )
    }

    @Test
    fun givenValidEmailEmptyPwdValidName_whenValidate_shouldReturnPwdError() {
        // Valid Email
        val email = "test@gmail.com"
        // Empty Password
        val password = ""
        // Valid UserName
        val name = "Mike"

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Success, PASSWORD Empty Error and NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_empty)
            ),
            Validation(Validation.Field.NAME, Resource.Success())
        )
    }

    @Test
    fun givenValidEmailValidPwdEmptyName_whenValidate_shouldReturnNameError() {
        // Valid Email
        val email = "test@gmail.com"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Empty UserName
        val name = ""

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Success, PASSWORD Success and NAME Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenInvalidEmailValidPwdEmptyName_whenValidate_shouldReturnEmailErrorAndNameError() {
        // Invalid Email
        val email = "test"
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Empty UserName
        val name = ""

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Error, PASSWORD Success and NAME Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_invalid)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenValidEmailInvalidPwdEmptyName_whenValidate_shouldReturnPwdErrorAndNameError() {
        // Valid Email
        val email = "test@gmail.com"
        // Invalid Password (less than 6 min-character-length)
        val password = "pwd"
        // Empty UserName
        val name = ""

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Success, PASSWORD Error and NAME Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_small_length)
            ),
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenEmptyEmailValidPwdEmptyName_whenValidate_shouldReturnEmailErrorAndNameError() {
        // Empty Email
        val email = ""
        // Valid Password (greater than 6 min-character-length)
        val password = "password"
        // Empty UserName
        val name = ""

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Empty Error, PASSWORD Success and NAME Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.EMAIL,
                Resource.Error(R.string.error_login_sign_up_email_field_empty)
            ),
            Validation(Validation.Field.PASSWORD, Resource.Success()),
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenValidEmailEmptyPwdEmptyName_whenValidate_shouldReturnPwdErrorAndNameError() {
        // Valid Email
        val email = "test@gmail.com"
        // Empty Password
        val password = ""
        // Empty UserName
        val name = ""

        // Call SignUp Validator to get the Validation results
        val validations = Validator.validateSignUpFields(email, password, name)

        // Test the Validation results
        // Assert that Validations has 3 results
        Truth.assertThat(validations).hasSize(3)
        // Assert that Validations contains EMAIL Success, PASSWORD Empty Error and NAME Empty Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(Validation.Field.EMAIL, Resource.Success()),
            Validation(
                Validation.Field.PASSWORD,
                Resource.Error(R.string.error_login_sign_up_password_field_empty)
            ),
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenValidName_whenValidate_shouldReturnNameSuccess() {
        // Valid UserName
        val name = "Mike"

        // Call Edit Profile Validator to get the Validation results
        val validations = Validator.validateEditProfileFields(name)

        // Test the Validation results
        // Assert that Validations has only 1 result
        Truth.assertThat(validations).hasSize(1)
        // Assert that Validations contains NAME Success Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.NAME,
                Resource.Success()
            )
        )
    }

    @Test
    fun givenEmptyName_whenValidate_shouldReturnNameError() {
        // Empty Name
        val name = ""

        // Call Edit Profile Validator to get the Validation results
        val validations = Validator.validateEditProfileFields(name)

        // Test the Validation results
        // Assert that Validations has only 1 result
        Truth.assertThat(validations).hasSize(1)
        // Assert that Validations contains NAME Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenBlankName_whenValidate_shouldReturnNameError() {
        // Blank Name
        val name = "  "

        // Call Edit Profile Validator to get the Validation results
        val validations = Validator.validateEditProfileFields(name)

        // Test the Validation results
        // Assert that Validations has only 1 result
        Truth.assertThat(validations).hasSize(1)
        // Assert that Validations contains NAME Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

    @Test
    fun givenNullName_whenValidate_shouldReturnNameError() {
        // Name set to 'null'
        val name = null

        // Call Edit Profile Validator to get the Validation results
        val validations = Validator.validateEditProfileFields(name)

        // Test the Validation results
        // Assert that Validations has only 1 result
        Truth.assertThat(validations).hasSize(1)
        // Assert that Validations contains NAME Error Validations
        Truth.assertThat(validations).containsExactly(
            Validation(
                Validation.Field.NAME,
                Resource.Error(R.string.error_sign_up_name_field_empty)
            )
        )
    }

}