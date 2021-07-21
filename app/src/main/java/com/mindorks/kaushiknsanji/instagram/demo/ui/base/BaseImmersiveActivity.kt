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

package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import android.os.Bundle
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.observeEvent
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.addSystemUiVisibilityListener
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.goImmersiveWhen
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.hideSystemUi
import com.mindorks.kaushiknsanji.instagram.demo.utils.display.showBelowCutout

/**
 * An abstract [BaseActivity] for all Fullscreen Immersive Activities in the app, that facilitates
 * setup and provides abstraction to common tasks.
 *
 * NOTE: Subclasses should only register a listener on a View that needs to delegate such events
 * to toggle Fullscreen Immersive mode. Such events should to be delegated to
 * [BaseImmersiveViewModel.onToggleFullscreen] and should NOT be observed as it is already
 * taken care by an observer registered here in this base class.
 *
 * @param VM The type of [BaseImmersiveViewModel] which will be the Primary ViewModel of the Activity.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseImmersiveActivity<VM : BaseImmersiveViewModel> : BaseActivity<VM>() {

    /**
     * Called when the activity is starting.  This is where most initialization should be done.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [.onSaveInstanceState].
     *     <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register a System UI Visibility change listener to update the visibility state of
        // System bars to the ViewModel
        window.addSystemUiVisibilityListener { isVisible: Boolean ->
            viewModel.onUpdateSystemBarsVisibilityState(isVisible)
        }

        // Ensure the app content is always shown below the device display cutouts
        showBelowCutout()
    }

    /**
     * Called when the current [getWindow] of the activity gains or loses
     * focus. This is the best indicator of whether this activity is the entity
     * with which the user actively interacts. The default implementation
     * clears the key tracking state, so should always be called.
     *
     * Note that this provides information about global focus state, which
     * is managed independently of activity lifecycle.  As such, while focus
     * changes will generally have some relation to lifecycle changes (an
     * activity that is stopped will not generally get window focus), you
     * should not rely on any particular order between the callbacks here and
     * those in the other lifecycle methods such as [.onResume].
     *
     * As a general rule, however, a foreground activity will have window
     * focus...  unless it has displayed other dialogs or popups that take
     * input focus, in which case the activity itself will not have focus
     * when the other windows have it.  Likewise, the system may display
     * system-level windows (such as the status bar notification panel or
     * a system alert) which will temporarily take window input focus without
     * pausing the foreground activity.
     *
     * Starting with [android.os.Build.VERSION_CODES.Q] there can be
     * multiple resumed activities at the same time in multi-window mode, so
     * resumed state does not guarantee window focus even if there are no
     * overlays above.
     *
     * If the intent is to know when an activity is the topmost active, the
     * one the user interacted with last among all activities but not including
     * non-activity windows like dialogs and popups, then
     * [.onTopResumedActivityChanged] should be used. On platform
     * versions prior to [android.os.Build.VERSION_CODES.Q],
     * [.onResume] is the best indicator.
     *
     * @param hasFocus Whether the window of this activity has focus.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Start in Fullscreen on launch when window has focus
        if (hasFocus) {
            hideSystemUi()
        }
    }

    /**
     * Method that initializes the [androidx.lifecycle.LiveData] observers.
     * Can be overridden by subclasses to initialize other [androidx.lifecycle.LiveData] observers.
     */
    override fun setupObservers() {
        super.setupObservers()

        // Register an observer for Fullscreen Toggle events to toggle the System bars visibility
        // for Fullscreen Sticky Immersive mode
        viewModel.toggleFullscreen.observeEvent(this) { toggle: Boolean ->
            goImmersiveWhen(toggle)
        }
    }
}