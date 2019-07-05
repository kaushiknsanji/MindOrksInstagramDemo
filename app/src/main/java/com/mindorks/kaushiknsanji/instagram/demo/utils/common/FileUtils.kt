package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.content.Context
import android.graphics.BitmapFactory
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Constants.JPEG_COMPRESSION_QUALITY
import com.mindorks.kaushiknsanji.instagram.demo.utils.log.Logger
import com.mindorks.paracamera.Camera
import com.mindorks.paracamera.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Utility object that deals with [File] management.
 *
 * @author Kaushik N Sanji
 */
object FileUtils {

    // Constant used for Logs
    const val TAG: String = "FileUtils"

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
        ReentrantLock().withLock {
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

    /**
     * Saves the Image pointed to by the [input] in the [directory] with the given name [imageName] which is
     * scaled/downsized to the [requiredHeight] while respecting its aspect ratio.
     */
    fun saveInputStreamToFile(input: InputStream, directory: File, imageName: String, requiredHeight: Int): File? {
        // Create a Temporary file to copy the Image pointed by [input]
        val tempFile = File(directory.path + File.separator + "TMP_IMG_PROCESSING")
        try {
            // Create a File that holds the resulting final Image
            val resultFile = File(directory.path + File.separator + imageName + ".${Camera.IMAGE_JPG}")
            // Copy the [input] image to the [tempFile]
            input.use { srcStream ->
                FileOutputStream(tempFile).use { destStream ->
                    srcStream.copyTo(destStream)
                }
            }
            // Scale/downsize the Image in [tempFile] proportionally to the [requiredHeight] and save the compressed version to [resultFile]
            Utils.saveBitmap(
                Utils.decodeFile(tempFile, requiredHeight),
                resultFile.path,
                Camera.IMAGE_JPG,
                JPEG_COMPRESSION_QUALITY
            )
            // Return the File containing the final Image
            return resultFile
        } finally {
            // Finally, delete the Temporary Image File
            tempFile.delete()
        }
    }

    /**
     * Decodes and returns the original dimensions of the Image pointed to by the [imageFile],
     * in a [Pair] of Image's width to height
     */
    fun getImageSize(imageFile: File): Pair<Int, Int>? {
        // Create an Instance of BitmapFactory Options to decode the dimensions of the original
        // Bitmap from the File
        val bitmapOptions = BitmapFactory.Options().apply {
            // Decoding for Bounds only
            inJustDecodeBounds = true
        }
        // Decode the dimensions of the original bitmap from the File
        BitmapFactory.decodeFile(imageFile.canonicalPath, bitmapOptions)

        return if (bitmapOptions.outWidth > -1 && bitmapOptions.outHeight > -1) {
            // Returning the Pair of bitmap's original dimensions when decoded successfully
            bitmapOptions.outWidth to bitmapOptions.outHeight
        } else {
            // Returning Null when failed to decode
            null
        }
    }

}