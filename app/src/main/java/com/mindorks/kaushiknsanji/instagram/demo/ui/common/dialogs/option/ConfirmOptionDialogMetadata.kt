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

package com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option

import com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogMetadata.Companion

/**
 * [BaseDialogMetadata] subclass used for Dialogs prepared via [ConfirmOptionDialogFragment].
 *
 * @param provideMetadataPairs Lambda function to provide an [Array] of Key-Value [Pair]s
 * which is later converted into [metadataMap]. Its `Receiver` is the [Companion], while the function
 * is also provided with [com.mindorks.kaushiknsanji.instagram.demo.ui.base.BaseDialogMetadata.BaseCompanion]
 * as its `base` parameter in order to help reference the available key constants in a simpler concise way.
 *
 * @constructor Creates an instance of [ConfirmOptionDialogMetadata] with
 * an [Array] of Key-Value [Pair]s provided by the function `provideMetadataPairs`.
 */
class ConfirmOptionDialogMetadata(
    provideMetadataPairs: Companion.(base: BaseCompanion) -> Array<Pair<String, Any?>>
) : BaseDialogMetadata(this.provideMetadataPairs(BaseCompanion)) {

    // Delegated property provided by [metadataMap]
    // which helps in identifying the type of [ConfirmOptionDialogFragment] shown
    val dialogType: String by metadataMap

    init {
        // Throw an exception when the Dialog type is not set
        require(dialogType.isNotBlank()) {
            "Property 'dialogType' needs to be set in order to be able to identify dialogs of type ${ConfirmOptionDialogFragment::class.qualifiedName}"
        }
    }

    /**
     * Returns a [Map] of required Keys with default values
     * that needs to be used when the provided Key is missing in [metadataMap].
     */
    override fun requiredMissingKeyDefaultValueMap(): Map<String, Any?> = mapOf(
        // Dialog Type is set to blank/empty when the corresponding key is missing in [metadataMap]
        KEY_DIALOG_TYPE to ""
    )

    companion object {
        // Constant for the Key representing the Dialog type that helps in
        // identifying the type of [ConfirmOptionDialogFragment] shown
        const val KEY_DIALOG_TYPE = "dialogType"
    }

}