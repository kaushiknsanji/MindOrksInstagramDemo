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

import android.content.Context
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Utility object that deals with [File] management.
 *
 * @author Kaushik N Sanji
 */
object FileUtils {

    // Constant used for Logs
    private const val TAG: String = "FileUtils"

    // Lock for creating directories
    private val filesDirLock: ReentrantLock = ReentrantLock()

    /**
     * Creates a directory by the name [dirName] under the application's files directory.
     *
     * @return A [File] pointing to the directory by the name [dirName]; can be `null` when the directories for the path
     * could not be created.
     */
    fun getDirectory(context: Context, dirName: String): File? {
        // Construct a File for the directory [dirName] under the application's files directory
        val file = File(context.filesDir.path + File.separator + dirName)
        // Create the directories to the File if not present and return the same when the directories exists
        return createFilesDir(file)
    }

    /**
     * Creates directories for the path pointed to by the [file].
     *
     * @return Returns the same [file] if its directories were already present or created successfully; else `null`
     */
    private fun createFilesDir(file: File): File? {
        // Ensuring synchronization with Lock while creating the Directory
        filesDirLock.withLock {
            if (!file.exists()) {
                // Create directories when they do not exist
                if (!file.mkdirs()) {
                    if (file.exists()) {
                        // spurious failure; probably racing with another process for this app

                        // Returning the [file] when the directories were successfully created
                        return file
                    }

                    // Log an error if the directories could not be created and return null
                    Logger.e(TAG, "Unable to create files sub-dir " + file.path)
                    return null
                }
            }
            // Returning the [file] when the directories were already present or created successfully
            return file
        }
    }

}