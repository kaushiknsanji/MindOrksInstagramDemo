package com.mindorks.kaushiknsanji.instagram.demo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.dao.PostDao
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.dao.UserDao
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity.PostEntity
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity.UserEntity

/**
 * An abstract [RoomDatabase] class that exposes the Database access objects and manages the versioning.
 * Instance of this is provided by Dagger.
 *
 * @author Kaushik N Sanji
 */
@Database(
    entities = [
        UserEntity::class,
        PostEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class DatabaseService : RoomDatabase() {

    /**
     * Database access object for "users" table
     */
    abstract fun userDao(): UserDao

    /**
     * Database access object for "posts" table
     */
    abstract fun postDao(): PostDao

}