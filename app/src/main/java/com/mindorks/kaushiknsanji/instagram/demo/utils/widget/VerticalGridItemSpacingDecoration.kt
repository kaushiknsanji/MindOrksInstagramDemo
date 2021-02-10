package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView [RecyclerView.ItemDecoration] class
 * to add spacing between the items and its parent in the Grid managed by
 * [androidx.recyclerview.widget.GridLayoutManager] or
 * [androidx.recyclerview.widget.StaggeredGridLayoutManager].
 *
 * NOTE: Applicable for Grid based layout in [RecyclerView.VERTICAL] orientation only.
 *
 * @property verticalOffsetSize The top and bottom spacing in Pixels to be applied between the items in the Grid
 * @property horizontalOffsetSize The left and right spacing in Pixels to be applied between the items in the Grid
 *
 * @author Kaushik N Sanji
 */
class VerticalGridItemSpacingDecoration(
    private val verticalOffsetSize: Int,
    private val horizontalOffsetSize: Int
) : RecyclerView.ItemDecoration() {

    /**
     * Retrieve any offsets for the given item. Each field of `outRect` specifies
     * the number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     *
     *
     * If this ItemDecoration does not affect the positioning of item views, it should set
     * all four fields of `outRect` (left, top, right, bottom) to zero
     * before returning.
     *
     *
     * If you need to access Adapter for additional data, you can call
     * [RecyclerView.getChildAdapterPosition] to get the adapter position of the
     * View.
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.isOrientationVertical()) {
            // Proceed only when RecyclerView Orientation is Vertical

            // Get the Grid Span count from the layout manager of the RecyclerView
            val spanCount: Int = parent.getSpanCount()

            // Ensure spanCount is at least 1 before proceeding
            if (spanCount < 1) {
                throw IllegalArgumentException("Span count should be at least 1. Determined is $spanCount")
            }

            // Get the Child View position in the adapter
            val position = parent.getChildAdapterPosition(view)
            // Calculate the column index of the item position
            val columnIndex = position % spanCount

            // Evaluates to first column when the column index is 0
            val isFirstColumn = columnIndex == 0
            // Evaluates to last column when the column index is (spanCount - 1)
            val isLastColumn = columnIndex + 1 == spanCount
            // Evaluates to first row when the item position is less than the spanCount
            val isFirstRow = position < spanCount

            if (isFirstColumn) {
                // Set full spacing to left when it is the first column item
                outRect.left = horizontalOffsetSize
            } else {
                // Set half spacing to left when it is other than the first column item
                outRect.left = horizontalOffsetSize / 2
            }

            if (isLastColumn) {
                // Set full spacing to right when it is the last column item
                outRect.right = horizontalOffsetSize
            } else {
                // Set half spacing to right when it is other than the last column item
                outRect.right = horizontalOffsetSize / 2
            }

            if (isFirstRow) {
                // Set full spacing to top when it is the item of the first row
                outRect.top = verticalOffsetSize
            } else {
                // Set 0 spacing to top when it is the item of row other than the first row,
                // since spacing will be taken care by defining the bottom
                outRect.top = 0
            }

            // Set full spacing to bottom
            outRect.bottom = verticalOffsetSize
        } else {
            // Call to super when RecyclerView Orientation is NOT Vertical
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}