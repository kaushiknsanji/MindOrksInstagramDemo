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

/**
 * Class to track changes to a `State` using bit logic. Useful for merging state changes/updates
 * to a Boolean value for use with [androidx.lifecycle.MediatorLiveData].
 *
 * Usage:
 * ```
 * companion object {
 *      // Constant used as keys for tracking buttons initialized
 *      private const val KEY_BUTTON_A = "ButtonA"
 *      private const val KEY_BUTTON_B = "ButtonB"
 *      private const val KEY_BUTTON_C = "ButtonC"
 *
 *      // Getter for the array of all the Buttons to be tracked for initialized state
 *      private val buttonsTrackedForInitState get() = arrayOf(
 *          KEY_BUTTON_A,
 *          KEY_BUTTON_B,
 *          KEY_BUTTON_C
 *      )
 * }
 * ...
 * private val hasButtonA: LiveData<Boolean> = ..
 * private val hasButtonB: LiveData<Boolean> = ..
 * private val hasButtonC: LiveData<Boolean> = ..
 *
 * // BitStateTracker to help with merging any button's initialized state to button panel's visibility
 * private val buttonInitStateMerger = BitStateTracker (
 *      buttonsTrackedForInitState, // Buttons to track
 *      Int::or, // Bitwise operation for True state
 *      { bitState: Int, bitKey: Int ->
 *          // Bitwise operation for False state
 *          bitState and bitKey.inv()
 *      },
 *      { bitState: Int ->
 *          // BitState transformation to visibility boolean
 *          bitState != 0
 *      }
 * )
 *
 * // MediatorLiveData to see if any Buttons have been initialized for updating
 * // the Button Panel visibility
 * val hasAnyButtons: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
 *      // Add sources for the tracked Buttons
 *      addSource(hasButtonA){ newValue: Boolean ->
 *          postValue(buttonInitStateMerger.updateState(KEY_BUTTON_A, newValue))
 *      }
 *      addSource(hasButtonB){ newValue: Boolean ->
 *          postValue(buttonInitStateMerger.updateState(KEY_BUTTON_B, newValue))
 *      }
 *      addSource(hasButtonC){ newValue: Boolean ->
 *          postValue(buttonInitStateMerger.updateState(KEY_BUTTON_C newValue))
 *      }
 * }
 * ```
 *
 * @param bitKeys [Array] of [String] Keys that needs to be monitored and updated for state changes.
 * Max number of keys supported is 32.
 * @property bitStateTrueOperation Lambda bitwise operation that needs to be executed on `true` state condition for any bitKey.
 * @property bitStateFalseOperation Lambda bitwise operation that needs to be executed on `false` state condition for any bitKey.
 * @property stateCheckCondition Lambda condition that needs to be executed on [bitState] to return
 * the desired [Boolean] state. In other words, result will be a [Boolean] representation of current [bitState].
 * @constructor Creates an instance of [BitStateTracker] with the configuration set.
 * @throws [IllegalArgumentException] when the number of `bitKeys` is more than the
 * supported number of 32 keys.
 *
 * @author Kaushik N Sanji
 */
class BitStateTracker(
    bitKeys: Array<String>,
    private val bitStateTrueOperation: (bitState: Int, bitKey: Int) -> Int,
    private val bitStateFalseOperation: (bitState: Int, bitKey: Int) -> Int,
    private val stateCheckCondition: (bitState: Int) -> Boolean
) {

    // A Map of Keys and its bit representation
    private val bitsMap: HashMap<String, Int> = HashMap(bitKeys.size)

    // Current computed state of bitwise operations
    private var bitState: Int = 0

    init {
        // Throw an exception when the number of keys provided is more than
        // the supported number of 32 keys
        require(bitKeys.size <= 32) {
            "Number of Keys provided in the 'bitKeys' parameter exceeds the max supported size of 32"
        }

        // For each key, compute its bit representation and store it in bitsMap with its key
        bitKeys.forEachIndexed { index, key ->
            // Bit representation is obtained by shifting 1 to the left by its position index
            // in the bitKeys array
            bitsMap[key] = 1 shl index
        }
    }

    /**
     * Updates the given [state] of the [key] into current computed [bitState].
     *
     * @param key [String] representing the tracked [key] in [bitsMap] that needs to be updated.
     * @param state [Boolean] representing the new state of the tracked [key].
     * @return Based on the configured [stateCheckCondition] which is applied on
     * the computed [bitState], this will be a [Boolean] representation of the [bitState].
     */
    fun updateState(key: String, state: Boolean): Boolean {
        // Update the current [bitState] based on the new [state] of the key
        bitState = if (state) {
            bitStateTrueOperation(bitState, bitsMap[key]!!)
        } else {
            bitStateFalseOperation(bitState, bitsMap[key]!!)
        }

        // Return the computed [bitState] in boolean representation
        return readState()
    }

    /**
     * Returns the current computed [bitState].
     */
    fun readBitState(): Int = bitState

    /**
     * Returns the current computed [bitState] in [Boolean] representation
     * obtained via the configured [stateCheckCondition] function.
     */
    fun readState(): Boolean = stateCheckCondition(readBitState())
}