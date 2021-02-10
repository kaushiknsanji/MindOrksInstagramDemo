package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import android.content.Context
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Image
import com.mindorks.paracamera.Camera
import com.mindorks.paracamera.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Utility object that deals with Image related operations.
 *
 * @author Kaushik N Sanji
 */
object ImageUtils {

    // Constant used for Logs
    private const val TAG: String = "ImageUtils"

    /**
     * Loads an Image into the corresponding [ImageView][destinationImageView].
     *
     * @param context [Context] for use with Glide and for loading [defaultImageRes] when required.
     * @param destinationImageView [ImageView] into which the downloaded image needs to be set to.
     * @param imageData [Image] instance to be provided when the Image is to be downloaded
     * from the Remote. Defaulted to `null`.
     * @param imageFile [File] instance pointing to an Image in the local storage, to be provided
     * if the Image needs to be loaded from local. Defaulted to `null`.
     * @param requestOptions [List] of [RequestOptions] customizations to be applied on the
     * Image to be shown. Defaulted to an empty list.
     * @param defaultImageRes Resource identifier of the default Drawable
     * to be shown when [imageData] and [imageFile] is `null`. Defaulted to invalid identifier `0`.
     * @param defaultImageRequestOptions [List] of [RequestOptions] customizations to be applied
     * on the [default Image][defaultImageRes]. Defaulted to an empty list.
     */
    fun loadImage(
        context: Context,
        destinationImageView: ImageView,
        imageData: Image? = null,
        imageFile: File? = null,
        requestOptions: List<RequestOptions> = emptyList(),
        @DrawableRes defaultImageRes: Int = 0,
        defaultImageRequestOptions: List<RequestOptions> = emptyList()
    ) {
        if (imageData != null || imageFile != null) {
            // When we have the Image information, configure Glide with Image URL/File
            // and other relevant customizations
            val glideRequest = GlideHelper.getAppropriateGlideRequests(context)
                .load(
                    imageData?.let {
                        // Load GlideUrl with Headers when we have [imageData]
                        GlideHelper.getProtectedUrl(
                            imageData.url,
                            imageData.headers
                        )
                    } ?:
                    // else, Load the File
                    imageFile
                )

            // Apply other customizations if any
            requestOptions.forEach { requestOption ->
                glideRequest.apply(requestOption)
            }

            imageData?.run {
                // Applies when we have [imageData]
                if (placeHolderWidth > 0 && placeHolderHeight > 0) {
                    // If we have the placeholder dimensions from the [image], then scale the ImageView
                    // to match these dimensions

                    // Scaling the ImageView dimensions to fit the placeholder dimensions
                    destinationImageView.run {
                        val params = layoutParams as ViewGroup.LayoutParams
                        params.width = placeHolderWidth
                        params.height = placeHolderHeight
                        layoutParams = params
                    }

                    // Override the dimensions of the Image (downloaded) to placeholder dimensions
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeHolderWidth, placeHolderHeight))
                }
            }

            // Start and load the image into the corresponding ImageView
            glideRequest.into(destinationImageView)

        } else {
            // Set default image if available when there is no image

            if (defaultImageRes != 0) {
                // Configure Glide with relevant customizations and then set the default photo
                val glideRequest = GlideHelper.getAppropriateGlideRequests(context)
                    .load(
                        // Load the default Image
                        ContextCompat.getDrawable(
                            context,
                            defaultImageRes
                        )
                    )

                // Apply other customizations if any
                defaultImageRequestOptions.forEach { requestOption ->
                    glideRequest.apply(requestOption)
                }

                // Start and load the image into the corresponding ImageView
                glideRequest.into(destinationImageView)
            }
        }
    }

    /**
     * Calculates the width ratio of the target screen/window to the source image,
     * and then scales the source Image height to fit the width of the target screen/window,
     * while respecting the aspect ratio of the source Image.
     *
     * @param sourceImageWidth [Int] value of the source Image Width. May be `null`.
     * @param sourceImageHeight [Int] value of the source Image Height. May be `null`.
     * @param targetWidth [Int] value of the target screen/window width
     * @param defaultImageHeight The lambda action to provide a default height to be used
     * when [sourceImageHeight] of the source image is unavailable.
     *
     * @return [Int] value of the scaled height when [sourceImageHeight] of the source image is available.
     * Otherwise, value provided by the function [defaultImageHeight] is returned.
     */
    fun scaleImageHeightToTargetWidth(
        sourceImageWidth: Int?,
        sourceImageHeight: Int?,
        targetWidth: Int,
        defaultImageHeight: () -> Int
    ): Int =
        sourceImageHeight?.let { imageHeight ->
            (sourceImageWidth?.let { imageWidth -> targetWidth.toFloat() / imageWidth.toFloat() }
                ?: 1f) * imageHeight.toFloat()
        }?.toInt() ?: defaultImageHeight()

    /**
     * Saves the Image pointed to by the [inputImageStream] in the [directory]
     * with the given [name][imageName] which is scaled/downsized to the [requiredHeight]
     * while respecting its aspect ratio.
     *
     * @return [File] of the resulting final image, saved in the [directory]
     * with the given [name][imageName].
     */
    fun saveImageToFile(
        inputImageStream: InputStream,
        directory: File,
        imageName: String,
        requiredHeight: Int
    ): File? {
        // Create a Temporary file to copy the Image pointed by [inputImageStream]
        val tempFile = File(directory.path + File.separator + "TMP_IMG_PROCESSING")
        // File to store the final image
        val resultFile: File?
        try {
            // Create a File that holds the resulting final image
            resultFile = File(directory.path + File.separator + imageName + ".${Camera.IMAGE_JPG}")
            // Copy the [inputImageStream] image to the [tempFile]
            inputImageStream.use { srcStream ->
                FileOutputStream(tempFile).use { destStream ->
                    srcStream.copyTo(destStream)
                }
            }
            // Scale/downsize the Image in [tempFile] proportionally to the [requiredHeight]
            // and save the compressed version to [resultFile]
            Utils.saveBitmap(
                Utils.decodeFile(tempFile, requiredHeight),
                resultFile.path,
                Camera.IMAGE_JPG,
                Constants.JPEG_COMPRESSION_QUALITY
            )
        } finally {
            // Finally, delete the Temporary Image File
            tempFile.delete()
        }
        // Return the File containing the final Image
        return resultFile
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