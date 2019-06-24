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
    val profilePicUrl: String? = null
)