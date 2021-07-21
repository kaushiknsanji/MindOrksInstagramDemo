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
import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity.UserEntity
import io.reactivex.Single

/**
 * Database access object interface for "users" table.
 *
 * @author Kaushik N Sanji
 */
@Dao
interface UserDao {

    /**
     * Inserts [user] data into "users" table. In case of any conflict, existing record if any will be replaced.
     *
     * @return Emits a [Single] of [Long] for the Id of the new user inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity): Single<Long>

    /**
     * Updates [user] data of the identified record(s) in the "users" table.
     *
     * @return Emits a [Single] of [Int] for the number of user(s) updated.
     */
    @Update
    fun update(user: UserEntity): Single<Int>

    /**
     * Deletes [user] data of the identified record(s) in the "users" table.
     *
     * @return Emits a [Single] of [Int] for the number of user(s) deleted.
     */
    @Delete
    fun delete(user: UserEntity): Single<Int>

    /**
     * Inserts a batch of [users] data into "users" table. In case of any conflict, existing records if any
     * will be replaced.
     *
     * @return Emits a [Single] of List of [Long] for the Ids of the new user(s) inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(vararg users: UserEntity): Single<List<Long>>

    /**
     * Pulls a list of all users data from the "users" table.
     *
     * @return Emits a [Single] of List of [UserEntity] of all the users data.
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Single<List<UserEntity>>

    /**
     * Retrieves user data identified by its [id] from the "users" table.
     *
     * @return Emits a [Single] of [UserEntity] of the identified user.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Single<UserEntity>

    /**
     * Retrieves a total count of users/records from the "users" table.
     *
     * @return Emits a [Single] of [Int] for the number of records found.
     */
    @Query("SELECT count(*) FROM users")
    fun count(): Single<Int>

}