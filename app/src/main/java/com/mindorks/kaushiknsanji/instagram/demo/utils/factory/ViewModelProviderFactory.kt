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

package com.mindorks.kaushiknsanji.instagram.demo.utils.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

/**
 * Class that extends [ViewModelProvider.NewInstanceFactory] to provide the instance of ViewModels
 * bootstrapped with dependencies.
 *
 * @param VM The type of ViewModel.
 * @property kClass The Kotlin [KClass] holder of ViewModel that needs to be provided.
 * @property creator The ViewModel instance creator lambda that prepares and returns the ViewModel instance needed.
 * @constructor Creates an Instance of [ViewModelProviderFactory] for the ViewModel instance needed.
 *
 * @author Kaushik N Sanji
 */
class ViewModelProviderFactory<VM : ViewModel>(
    private val kClass: KClass<VM>,
    private val creator: () -> VM
) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Factory that returns the ViewModel instance of the ViewModel class holder [modelClass]
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class, RuntimeException::class)
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(kClass.java)) {
            try {
                // When both the [modelClass] and the [kClass] are same,
                // try to cast to the ViewModel instance and return it
                creator() as T
            } catch (e: ClassCastException) {
                // Throw a RuntimeException when the cast fails
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else {
            // Throw an IllegalArgumentException when the [modelClass] or the [kClass] is invalid
            throw IllegalArgumentException("Unknown class name")
        }

}