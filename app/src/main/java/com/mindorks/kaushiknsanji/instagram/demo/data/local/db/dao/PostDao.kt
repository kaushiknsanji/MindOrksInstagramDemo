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

package com.mindorks.kaushiknsanji.instagram.demo.data.local.db.dao

import androidx.room.*
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity.PostEntity
import io.reactivex.Single

/**
 * Database access object interface for "posts" table.
 *
 * @author Kaushik N Sanji
 */
@Dao
interface PostDao {

    /**
     * Inserts [post] data into "posts" table. In case of any conflict, existing record if any will be replaced.
     *
     * @return Emits a [Single] of [Long] for the Id of the new post inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: PostEntity): Single<Long>

    /**
     * Updates [post] data of the identified record(s) in the "posts" table.
     *
     * @return Emits a [Single] of [Int] for the number of post(s) updated.
     */
    @Update
    fun update(post: PostEntity): Single<Int>

    /**
     * Deletes [post] data of the identified record(s) in the "posts" table.
     *
     * @return Emits a [Single] of [Int] for the number of post(s) deleted.
     */
    @Delete
    fun delete(post: PostEntity): Single<Int>

    /**
     * Pulls a list of all posts data from the "posts" table.
     *
     * @return Emits a [Single] of List of [PostEntity] of all the posts data.
     */
    @Query("SELECT * FROM posts")
    fun getAllPosts(): Single<List<PostEntity>>

    /**
     * Pulls a list of posts data from the "posts" table, for all the posts created by user identified by its [creatorId].
     *
     * @return Emits a [Single] of List of [PostEntity] of all the posts data created by user.
     */
    @Query("SELECT * FROM posts WHERE creator_id = :creatorId")
    fun getAllPostsByUserId(creatorId: Long): Single<List<PostEntity>>

    /**
     * Retrieves a total count of posts/records from the "posts" table.
     *
     * @return Emits a [Single] of [Int] for the number of records found.
     */
    @Query("SELECT count(*) FROM posts")
    fun countOfAllPosts(): Single<Int>

    /**
     * Retrieves a total count of posts/records from the "posts" table, for all the posts created by user
     * identified by its [creatorId].
     *
     * @return Emits a [Single] of [Int] for the number of records found.
     */
    @Query("SELECT count(*) FROM posts WHERE creator_id = :creatorId")
    fun countOfAllPostsByUserId(creatorId: Long): Single<Int>

}