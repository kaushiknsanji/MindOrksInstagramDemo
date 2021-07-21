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