package com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["liked_by_user_ids"]
        ) //TODO: Will this ForeignKey Work?
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
) {

    /**
     * Secondary Default constructor to hack around the persisting bug of Room in Kotlin.
     * This constructor will not be used by the Room.
     */
    constructor() : this(0, "", 0, 0, Date(), 0, null)

}