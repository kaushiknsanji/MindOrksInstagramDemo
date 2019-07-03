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