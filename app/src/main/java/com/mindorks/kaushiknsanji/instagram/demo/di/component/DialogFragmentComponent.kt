package com.mindorks.kaushiknsanji.instagram.demo.di.component

import com.mindorks.kaushiknsanji.instagram.demo.di.DialogFragmentScope
import com.mindorks.kaushiknsanji.instagram.demo.di.module.DialogFragmentModule
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

}