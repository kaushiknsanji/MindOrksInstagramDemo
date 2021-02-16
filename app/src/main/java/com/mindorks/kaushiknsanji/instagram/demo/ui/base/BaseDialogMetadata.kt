package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata.BaseCompanion.requiredKeys
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource

/**
 * An abstract base Dialog Metadata class for the Dialog's elements that are initialized dynamically,
 * whose Dialog is constructed using [androidx.appcompat.app.AlertDialog.Builder]
 * via the [BaseDialogFragment] class.
 *
 * @property metadataMap A [MutableMap] of Dialog's elements with its values.
 * Keys can be anything that your Dialog needs. [Keys][requiredKeys] provided by [BaseDialogMetadata]
 * class contains all the Dialog's elements that can be dynamically set.
 *
 * @constructor A Private constructor that creates an instance of [BaseDialogMetadata] with [metadataMap].
 */
abstract class BaseDialogMetadata private constructor(val metadataMap: MutableMap<String, Any?>) {

    /**
     * Public secondary constructor that creates an instance of [BaseDialogMetadata] with
     * an [Array] of Key-Value [Pair]s.
     *
     * @param metadataPairs An [Array] of Key-Value [Pair]s that is converted into [metadataMap].
     */
    constructor(metadataPairs: Array<Pair<String, Any?>>) : this(hashMapOf(*metadataPairs))

    // Delegated properties provided by [metadataMap] for the Dialog's elements
    val titleDialogId: Resource<Int> by metadataMap
    val messageDialogId: Resource<Int> by metadataMap
    val positiveButtonTextId: Resource<Int> by metadataMap
    val negativeButtonTextId: Resource<Int> by metadataMap
    val neutralButtonTextId: Resource<Int> by metadataMap

    init {
        // Apply default values for missing keys in [metadataMap]
        applyMissingKeyDefaults()
    }

    /**
     * Can be overridden by subclasses to provide a [Map] of required Keys with default values
     * that needs to be used when the provided Key is missing in [metadataMap].
     */
    protected open fun requiredMissingKeyDefaultValueMap(): Map<String, Any?> = emptyMap()

    /**
     * Applies default values for missing Keys in [metadataMap].
     */
    private fun applyMissingKeyDefaults() {
        // For the required Keys provided by [BaseCompanion], apply a value of `Resource null`
        // when the corresponding Key is missing
        requiredKeys.forEach { missingKey: String ->
            if (missingKey !in metadataMap) {
                // Applying `Resource null` causes the corresponding Dialog element to be hidden
                metadataMap[missingKey] = Resource.Success(null)
            }
        }

        // For the required Keys provided by subclass, apply the provided default value
        // when the corresponding Key is missing
        requiredMissingKeyDefaultValueMap().forEach { (missingKey: String, value: Any?) ->
            if (missingKey !in metadataMap) {
                metadataMap[missingKey] = value
            }
        }
    }

    companion object BaseCompanion {
        // Constants for the Keys representing the Dialog's elements that can be initialized dynamically
        const val KEY_TITLE = "titleDialogId"
        const val KEY_MESSAGE = "messageDialogId"
        const val KEY_BUTTON_POSITIVE = "positiveButtonTextId"
        const val KEY_BUTTON_NEGATIVE = "negativeButtonTextId"
        const val KEY_BUTTON_NEUTRAL = "neutralButtonTextId"

        // Getter for the array of Keys representing the Dialog's elements
        private val requiredKeys: Array<String>
            get() = arrayOf(
                KEY_TITLE,
                KEY_MESSAGE,
                KEY_BUTTON_POSITIVE,
                KEY_BUTTON_NEGATIVE,
                KEY_BUTTON_NEUTRAL
            )
    }

}