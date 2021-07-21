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

package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.DialogFragmentScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.DialogFragmentModule
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.option.ConfirmOptionDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.picture.PhotoOptionsDialogFragment
import com.mindorks.kaushiknsanji.instagram.demo.ui.common.dialogs.progress.ProgressTextDialogFragment
import dagger.Component

/**
 * Dagger Component for exposing dependencies from the Module [DialogFragmentModule]
 * and its component [ApplicationComponent] dependency.
 *
 * @author Kaushik N Sanji
 */
@DialogFragmentScope
@Component(dependencies = [ApplicationComponent::class], modules = [DialogFragmentModule::class])
interface DialogFragmentComponent {

    fun inject(dialogFragment: PhotoOptionsDialogFragment)

    fun inject(dialogFragment: ProgressTextDialogFragment)

    fun inject(dialogFragment: ConfirmOptionDialogFragment)

}