package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.putExtrasFromMap
import com.mindorks.paracamera.Camera
import java.io.Serializable

/**
 * An abstract base activity call contracts that facilitates the setup of required
 * activity call contracts for the app.
 *
 * These activity call contracts can also be used by the Fragments in the app.
 *
 * @author Kaushik N Sanji
 */
object BaseActivityResultContracts {

    // Constant used for logs and Bundle Keys
    private const val TAG = "BaseActivityResultContracts"

    // Bundle Key constant for Activity Result Code
    private const val BUNDLE_KEY_RESULT_CODE = "$TAG.result.RESULT_CODE"

    /**
     * Returns the Result Code of an Activity Result if saved in a [Bundle].
     */
    fun getResultCode(bundle: Bundle) = bundle.getInt(BUNDLE_KEY_RESULT_CODE)

    /**
     * An abstract [ActivityResultContract] which specifies that an Activity can be called
     * with an input [Map] of Intent Extras and produce an output [Bundle].
     */
    abstract class InputExtrasMapOutputBundle :
        ActivityResultContract<Map<String, Serializable>, Bundle>() {

        /**
         * Create an intent that can be used for
         * [androidx.activity.result.ActivityResultCaller.registerForActivityResult].
         *
         * @param context [Context] to create an [Intent].
         * @param input The source [Map] containing the Intent Extras to load from.
         *
         * @return Returns the [Intent] with the Extras loaded from [input].
         */
        override fun createIntent(context: Context, input: Map<String, Serializable>): Intent =
            provideIntentWithoutInputExtras(context).putExtrasFromMap(input)


        /**
         * Parse and convert result obtained from
         * [androidx.activity.result.ActivityResultCallback.onActivityResult] to [Bundle].
         *
         * @param resultCode The [Int] result code returned by the child activity
         * through its setResult().
         * @param intent An [Intent], which can return result data to the caller
         * (various data can be attached to Intent "extras").
         *
         * @return [Bundle] containing the [resultCode] in the key [BUNDLE_KEY_RESULT_CODE] and
         * also the [intent] extras if any.
         */
        override fun parseResult(resultCode: Int, intent: Intent?): Bundle =
            (intent?.extras ?: Bundle()).apply {
                if (resultCode >= Activity.RESULT_OK) {
                    putInt(BUNDLE_KEY_RESULT_CODE, resultCode)
                }
            }

        /**
         * To be overridden by subclasses to provide an [Intent] that needs to be used to start the
         * required Activity, without the Intent Extras which will be passed as part of
         * [androidx.activity.result.ActivityResultLauncher.launch]
         *
         * @param context [Context] to create an [Intent].
         *
         * @return Returns the [Intent] to start the required Activity.
         */
        abstract fun provideIntentWithoutInputExtras(context: Context): Intent

    }

    /**
     * An [ActivityResultContract] to prompt the user to open a document and receive its
     * contents as a `content://` [android.net.Uri] which allows to use
     * [android.content.ContentResolver.openInputStream] to access its raw data.
     *
     * This extends [ActivityResultContracts.OpenDocument] to add the
     * category [Intent.CATEGORY_OPENABLE] so that only the content that can be streamed is returned.
     *
     * The `input` is an [Array] of MIME types to filter by, e.g., `image/ *`.
     *
     * Can be extended to override [createIntent] if you wish to pass additional extras to
     * the [Intent] created by `super.createIntent()`.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    open class OpenableDocument : ActivityResultContracts.OpenDocument() {

        /**
         * Create an intent that can be used for
         * [androidx.activity.result.ActivityResultCaller.registerForActivityResult].
         *
         * @param context [Context] to create an [Intent].
         * @param input An [Array] of MIME types to filter by.
         *
         * @return Returns an [Intent] that prompts the user to open an openable document.
         */
        override fun createIntent(context: Context, input: Array<out String>): Intent {
            return super.createIntent(context, input).apply {
                // With the Intent that can open any document, filter results that can be
                // streamed like files (this excludes stuff like timezones and contacts)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }

    }

    /**
     * An [ActivityResultContract] to take a picture and save it into the `content://` [android.net.Uri]
     * provided by the input [ParaCamera][Camera].
     *
     * The `input` is a [ParaCamera][Camera] instance required to setup the [Intent] for Image capture.
     *
     * Can be extended to override [createIntent] if you wish to pass additional extras to
     * the [Intent] created by `super.createIntent()`.
     */
    open class ParaCameraTakePicture : ActivityResultContract<Camera, Boolean>() {

        /**
         * Create an intent that can be used for
         * [androidx.activity.result.ActivityResultCaller.registerForActivityResult].
         *
         * @param context [Context] to create an [Intent].
         * @param input [Camera] instance to setup the [Intent] for Image capture.
         *
         * @return Returns an [Intent] with action [MediaStore.ACTION_IMAGE_CAPTURE] to launch a
         * Camera app for capturing an Image.
         */
        override fun createIntent(context: Context, input: Camera): Intent {
            // Access the private method of ParaCamera API which initializes the Intent for Image capture
            val setUpIntentMethod =
                input.javaClass.getDeclaredMethod("setUpIntent", Intent::class.java).apply {
                    isAccessible = true
                }

            // Prepare and return the Intent for Image Capture
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                // Setup the Intent further with the ParaCamera API
                setUpIntentMethod.invoke(input, this)
            }
        }

        /**
         * Parse and convert result obtained from
         * [androidx.activity.result.ActivityResultCallback.onActivityResult] to [Boolean].
         *
         * @param resultCode The [Int] result code returned by the child activity
         * through its setResult().
         * @param intent An [Intent], which can return result data to the caller
         * (various data can be attached to Intent "extras").
         *
         * @return Returns `true` if the Image capture was successful, i.e.,
         * if the [resultCode] is [Activity.RESULT_OK]; `false` otherwise.
         */
        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK

    }

}