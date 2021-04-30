package com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * RoomDatabase Entity class for Instagram Users.
 *
 * @constructor Creates an Instance of [UserEntity]
 *
 * @author Kaushik N Sanji
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "profile_pic_url")
    var profilePicUrl: String?,

    @ColumnInfo(name = "tagline")
    var tagline: String
)