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

/**
 * Interface for Network related tasks.
 *
 * @author Kaushik N Sanji
 */
interface NetworkHelper {

    /**
     * Checks for Network connectivity.
     *
     * @return `true` if the Network is established; `false` otherwise.
     */
    fun isNetworkConnected(): Boolean

    /**
     * Generates a [NetworkError] instance appropriately based on the [throwable]
     */
    fun castToNetworkError(throwable: Throwable): NetworkError

}