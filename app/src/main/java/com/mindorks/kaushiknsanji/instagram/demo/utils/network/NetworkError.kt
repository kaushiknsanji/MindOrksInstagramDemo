package com.mindorks.kaushiknsanji.instagram.demo.utils.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Class for capturing the Network Error details from the Remote API response.
 *
 * @property status [Int] value of the HTTP Status codes. Defaulted to -1.
 * @property statusCode [String] value of the HTTP Status code. Defaulted to "-1".
 * @property message [String] value containing the error message. Defaulted to "Something went wrong"
 * @constructor Creates an Instance of [NetworkError]
 *
 * @author Kaushik N Sanji
 */
data class NetworkError(
    val status: Int = -1,

    @Expose
    @SerializedName("statusCode")
    val statusCode: String = "-1",

    @Expose
    @SerializedName("message")
    val message: String = "Something went wrong"
)