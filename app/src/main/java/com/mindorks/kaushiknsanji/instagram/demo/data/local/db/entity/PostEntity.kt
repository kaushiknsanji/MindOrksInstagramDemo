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

package com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity

import androidx.room.*
import java.util.*

/**
 * RoomDatabase Entity class for Instagram Posts.
 *
 * @property creatorId [Long] value which is a Foreign Key to the "users" table
 * @property likedByUserIds [List] of [Long] values that represents a list of Foreign Key to the "users" table,
 * which identifies the list of users who liked the Post
 * @constructor Creates an Instance of [PostEntity]
 *
 * @author Kaushik N Sanji
 */
@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["creator_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["creator_id"])
    ]
)
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "image_url")
    var imageUrl: String,

    @ColumnInfo(name = "image_height")
    var imageHeight: Int = 0,

    @ColumnInfo(name = "image_width")
    var imageWidth: Int = 0,

    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @ColumnInfo(name = "creator_id")
    var creatorId: Long = 0,

    @ColumnInfo(name = "liked_by_user_ids")
    var likedByUserIds: List<Long>?
)