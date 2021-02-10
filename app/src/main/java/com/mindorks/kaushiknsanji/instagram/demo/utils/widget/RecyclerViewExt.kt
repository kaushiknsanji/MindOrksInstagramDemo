package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

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