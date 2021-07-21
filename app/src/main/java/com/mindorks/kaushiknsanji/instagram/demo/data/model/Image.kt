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

package com.mindorks.kaushiknsanji.instagram.demo.data.model

/**
 * Application-wide Data Model for an Image to be displayed.
 *
 * @property url [String] representing the URL source of the Image.
 * @property headers [Map] of Retrofit Headers required for downloading the Image from the [url].
 * @property placeHolderWidth [Int] value of the width of the PlaceHolder where the Image will be displayed after download.
 * @property placeHolderHeight [Int] value of the height of the PlaceHolder where the Image will be displayed after download.
 * @constructor Creates and returns an Instance of [Image]
 *
 * @author Kaushik N Sanji
 */
data class Image(
    val url: String,
    val headers: Map<String, String>,
    val placeHolderWidth: Int = -1,
    val placeHolderHeight: Int = -1
)