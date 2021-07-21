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

package com.mindorks.kaushiknsanji.instagram.demo.utils.common

/**
 * Utility object for the Constants used in the App.
 *
 * @author Kaushik N Sanji
 */
object Constants {

    // For the Quality index of JPEG Image compression. 100 is Highest Quality and 0 is Highest Compression
    const val JPEG_COMPRESSION_QUALITY = 80

    // Name of the Directory for the Temporary files created by the App in the Application's directory
    const val DIRECTORY_TEMP = "temp"

    // Max Height to which the Images are to be scaled/downsized while respecting its aspect ratio
    const val IMAGE_MAX_HEIGHT_SCALE = 500

    // Content Type String for Images
    const val TYPE_IMAGE = "image/*"

}