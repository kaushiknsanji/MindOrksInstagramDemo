package com.mindorks.kaushiknsanji.instagram.demo.utils.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView [RecyclerView.ItemDecoration] class
 * to add spacing between the items and its parent in the List managed by
 * [androidx.recyclerview.widget.LinearLayoutManager]
 *
 * NOTE: Applicable for List based layout in [RecyclerView.VERTICAL] orientation only.
 *
 * @property verticalOffsetSize The top and bottom spacing in Pixels to be applied between the items in the List
 * @property horizontalOffsetSize The left and right spacing in Pixels to be applied between the items and its parent
 * in the List
 * @property bottomOffsetSize The Spacing in Pixels to be applied at the bottom of the List, i.e., after the last item.
 * Defaulted to 0. The spacing applied will be twice the value provided.
 *
 * @author Kaushik N Sanji
 */
class VerticalListItemSpacingDecoration(
    private val verticalOffsetSize: Int,
    private val horizontalOffsetSize: Int,
    private val bottomOffsetSize: Int = 0
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
        // Get the Child View position in the adapter
        val position = parent.getChildAdapterPosition(view)

        // Evaluates to first item when position is 0
        val isFirstItem = position == 0

        // Evaluates to last item when position equals the adapter size
        val isLastItem = position == parent.adapter?.itemCount?.minus(1) ?: false

        // Set full spacing to the top when the Item is the First Item in the list
        if (isFirstItem) {
            outRect.top = verticalOffsetSize
        }

        // Set full spacing to bottom
        outRect.bottom = verticalOffsetSize
        // Set full spacing to left
        outRect.left = horizontalOffsetSize
        // Set full spacing to right
        outRect.right = horizontalOffsetSize

        if (isLastItem && bottomOffsetSize > 0) {
            // When it is the last item and we have the bottom offset

            // Apply twice the additional offset to the bottom
            outRect.bottom += (2 * bottomOffsetSize)
        }
    }

}