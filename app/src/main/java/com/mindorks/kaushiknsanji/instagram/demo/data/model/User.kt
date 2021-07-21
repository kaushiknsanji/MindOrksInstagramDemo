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
 * Application-wide Data Model for User data.
 *
 * @constructor Creates an Instance of [User]
 *
 * @author Kaushik N Sanji
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val profilePicUrl: String? = null,
    val tagline: String? = null
)