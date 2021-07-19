package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import androidx.recyclerview.widget.*

/**
 * Kotlin file for extension functions on `RecyclerView`.
 *
 * @author Kaushik N Sanji
 */

// Default constant used in Span Count determination
private const val ZERO_SPAN_COUNT = 0

// Default constant used in Orientation determination
private const val INVALID_ORIENTATION = -1

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [LinearLayoutManager] and returns the same.
 *
 * @throws [ClassCastException] if [RecyclerView.LayoutManager] is NOT [LinearLayoutManager].
 */
@Throws(ClassCastException::class)
fun RecyclerView.getLinearLayoutManager(): LinearLayoutManager =
    this.layoutManager as LinearLayoutManager

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [GridLayoutManager] and returns the same.
 *
 * @throws [ClassCastException] if [RecyclerView.LayoutManager] is NOT [GridLayoutManager].
 */
@Throws(ClassCastException::class)
fun RecyclerView.getGridLayoutManager(): GridLayoutManager =
    this.layoutManager as GridLayoutManager

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [StaggeredGridLayoutManager] and returns the same.
 *
 * @throws [ClassCastException] if [RecyclerView.LayoutManager] is NOT [StaggeredGridLayoutManager].
 */
@Throws(ClassCastException::class)
fun RecyclerView.getStaggeredGridLayoutManager(): StaggeredGridLayoutManager =
    this.layoutManager as StaggeredGridLayoutManager

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [LinearLayoutManager] and returns the same.
 * If not [LinearLayoutManager], then it returns `null`.
 */
fun RecyclerView.getLinearLayoutManagerOrNull(): LinearLayoutManager? =
    this.layoutManager as? LinearLayoutManager

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [GridLayoutManager] and returns the same.
 * If not [GridLayoutManager], then it returns `null`.
 */
fun RecyclerView.getGridLayoutManagerOrNull(): GridLayoutManager? =
    this.layoutManager as? GridLayoutManager

/**
 * Casts the receiver's [RecyclerView.LayoutManager] to [StaggeredGridLayoutManager] and returns the same.
 * If not [StaggeredGridLayoutManager], then it returns `null`.
 */
fun RecyclerView.getStaggeredGridLayoutManagerOrNull(): StaggeredGridLayoutManager? =
    this.layoutManager as? StaggeredGridLayoutManager

/**
 * Retrieves and returns the `SpanCount` from the receiver's [RecyclerView.LayoutManager].
 * It cycles through both Grid layout managers, i.e., [GridLayoutManager] and
 * [StaggeredGridLayoutManager] in the order, to read the `SpanCount`. If it fails in both the cases,
 * then the value passed as [default] will be returned.
 *
 * @param default The default [Int] value of the `SpanCount` to be returned in case of failure.
 */
fun RecyclerView.getSpanCount(default: Int = ZERO_SPAN_COUNT): Int =
    getGridLayoutManagerOrNull()?.spanCount
        ?: getStaggeredGridLayoutManagerOrNull()?.spanCount
        ?: default

/**
 * Retrieves and returns the `Orientation` from the receiver's [RecyclerView.LayoutManager].
 * It cycles through all the layout managers, i.e., [LinearLayoutManager], [GridLayoutManager]
 * and [StaggeredGridLayoutManager] in the order, to read the `Orientation`. If it fails in all cases,
 * then the value passed as [default] will be returned.
 *
 * @param default The default [Int] value of the `Orientation` to be returned in case of failure.
 */
fun RecyclerView.getOrientation(default: Int = INVALID_ORIENTATION): Int =
    getLinearLayoutManagerOrNull()?.orientation
        ?: getGridLayoutManagerOrNull()?.orientation
        ?: getStaggeredGridLayoutManagerOrNull()?.orientation
        ?: default

/**
 * Returns `true` if the `Orientation` from the [receiver][RecyclerView]
 * is [RecyclerView.VERTICAL]; `false` otherwise.
 */
fun RecyclerView.isOrientationVertical(): Boolean =
    getOrientation() == RecyclerView.VERTICAL

/**
 * Returns `true` if the `Orientation` from the [receiver][RecyclerView]
 * is [RecyclerView.HORIZONTAL]; `false` otherwise.
 */
fun RecyclerView.isOrientationHorizontal(): Boolean =
    getOrientation() == RecyclerView.HORIZONTAL

/**
 * Extension function on [RecyclerView] for smoothly scrolling to the given [targetPosition] in a
 * vertically oriented [RecyclerView] such that the corresponding item gets properly displayed
 * and aligned with the top edge of the parent [RecyclerView].
 */
fun RecyclerView.smoothVScrollToPositionWithViewTop(targetPosition: Int) {
    // Check if the orientation is vertical before proceeding
    if (isOrientationVertical()) {
        // Configure custom LinearSmoothScroller in order to align
        // the top of the child with the parent RecyclerView top always
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
            /**
             * When scrolling towards a child view, this method defines whether we should align the top
             * or the bottom edge of the child with the parent RecyclerView.
             *
             * @return SNAP_TO_START, SNAP_TO_END or SNAP_TO_ANY; depending on the current target vector
             */
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        // Set the target position to scroll to
        linearSmoothScroller.targetPosition = targetPosition
        // Begin Smooth scroll with the scroller built
        layoutManager?.startSmoothScroll(linearSmoothScroller)
    }
}

/**
 * Extension function on [RecyclerView] built with [LinearLayoutManager]/[GridLayoutManager]
 * to retrieve the Item position of the first completely visible or partially visible
 * Item on the screen.
 *
 * While determining the position, position of first completely visible Item is prioritized over
 * the partially visible one.
 *
 * @return [Int] value of the position of the first visible Item on the screen.
 * Can be [RecyclerView.NO_POSITION] if it could not be determined or if the [RecyclerView]
 * is NOT setup with [LinearLayoutManager]/[GridLayoutManager].
 */
private fun RecyclerView.getFirstVisibleItemPositionFromLinearLayoutManager(): Int =
    getLinearLayoutManagerOrNull()?.let { layoutManager ->
        layoutManager
            .findFirstCompletelyVisibleItemPosition()
            .takeIf { it > RecyclerView.NO_POSITION }
            ?: layoutManager.findFirstVisibleItemPosition()
    } ?: RecyclerView.NO_POSITION

/**
 * Extension function on [RecyclerView] built with [StaggeredGridLayoutManager]
 * to retrieve the Item position of the first completely visible or partially visible
 * Item on the screen.
 *
 * While determining the position, position of first completely visible Item is prioritized over
 * the partially visible one, and since this is built with [StaggeredGridLayoutManager], only
 * the position of the first view of the Grid span is considered.
 *
 * @return [Int] value of the position of the first visible Item on the screen.
 * Can be [RecyclerView.NO_POSITION] if it could not be determined or if the [RecyclerView]
 * is NOT setup with [StaggeredGridLayoutManager].
 */
private fun RecyclerView.getFirstVisibleItemPositionFromStaggeredGridLayoutManager(): Int =
    getStaggeredGridLayoutManagerOrNull()?.let { layoutManager ->
        layoutManager
            .findFirstCompletelyVisibleItemPositions(null)[0]
            .takeIf { it > RecyclerView.NO_POSITION }
            ?: layoutManager.findFirstVisibleItemPositions(null)[0]
    } ?: RecyclerView.NO_POSITION

/**
 * Extension function on [RecyclerView] to retrieve the Item position of the
 * first completely visible or partially visible Item on the screen.
 *
 * While determining the position, position of first completely visible Item is prioritized over
 * the partially visible one.
 *
 * This method first tries to retrieve the Item Position from the
 * [LinearLayoutManager]/[GridLayoutManager] of the [RecyclerView]. If this is
 * found to be [RecyclerView.NO_POSITION], then it will try to retrieve
 * from [StaggeredGridLayoutManager] of the [RecyclerView].
 *
 * @return [Int] value of the position of the first visible Item on the screen.
 * Can be [RecyclerView.NO_POSITION] if it could not be determined or if the [RecyclerView]
 * is NOT setup with [LinearLayoutManager]/[GridLayoutManager]/[StaggeredGridLayoutManager], i.e.,
 * if setup with a [RecyclerView.LayoutManager] that does not provide this information.
 */
fun RecyclerView.getFirstVisibleItemPosition(): Int =
    getFirstVisibleItemPositionFromLinearLayoutManager().takeIf { it > RecyclerView.NO_POSITION }
        ?: getFirstVisibleItemPositionFromStaggeredGridLayoutManager()