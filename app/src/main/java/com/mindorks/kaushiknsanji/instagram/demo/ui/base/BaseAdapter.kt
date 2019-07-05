package com.mindorks.kaushiknsanji.instagram.demo.ui.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * An abstract base class for [RecyclerView.Adapter] that registers as a [LifecycleObserver]
 * on the [Lifecycle] of a LifecycleOwner to be Lifecycle aware. Provides abstraction for common tasks and setup.
 *
 * @param T The type of ItemView's data.
 * @param VH The type of ItemView's ViewHolder that extends [BaseItemViewHolder]
 * @param parentLifecycle The [Lifecycle] of a LifecycleOwner to observe on.
 * @property dataList [MutableList] of type [T] which is the data list of the Adapter.
 *
 * @author Kaushik N Sanji
 */
abstract class BaseAdapter<T : Any, VH : BaseItemViewHolder<T, out BaseItemViewModel<T>>>(
    parentLifecycle: Lifecycle, private val dataList: MutableList<T>
) : RecyclerView.Adapter<VH>() {

    // For the RecyclerView instance
    private var recyclerView: RecyclerView? = null

    init {
        // Register an Observer on the parent's Lifecycle to keep the Adapter in-sync
        parentLifecycle.addObserver(object : LifecycleObserver {

            /**
             * Called when the parent's Lifecycle is Started. This should update the Lifecycle
             * of only the visible ViewHolders to Started state.
             */
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onParentStart() {
                recyclerView?.run {
                    val layoutManager = layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        // Finding the start-end Adapter positions of the Visible set from the LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        // Updating the visible ViewHolders in the range to Started state
                        (firstVisibleItemPosition..lastVisibleItemPosition).forEach { adapterPosition ->
                            findViewHolderForAdapterPosition(adapterPosition)?.let { viewHolder ->
                                (viewHolder as BaseItemViewHolder<*, *>).onStart()
                            }
                        }
                    }
                }
            }

            /**
             * Called when the parent's Lifecycle is Stopped. This should update the Lifecycle
             * of all the ViewHolders to Stopped state.
             */
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onParentStop() {
                recyclerView?.run {
                    (0 until childCount).forEach { childIndex ->
                        getChildAt(childIndex)?.let { childView ->
                            (getChildViewHolder(childView) as BaseItemViewHolder<*, *>).onStop()
                        }
                    }
                }
            }

            /**
             * Called when the parent's Lifecycle is Destroyed. This should update the Lifecycle
             * of all the ViewHolders to Destroyed state.
             */
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onParentDestroy() {
                recyclerView?.run {
                    (0 until childCount).forEach { childIndex ->
                        getChildAt(childIndex)?.let { childView ->
                            (getChildViewHolder(childView) as BaseItemViewHolder<*, *>).onDestroy()
                        }
                    }
                }
            }
        })
    }

    /**
     * Called by RecyclerView when it starts observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // When the RecyclerView is attached to this adapter, save the reference
        this.recyclerView = recyclerView
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // When the RecyclerView is detached, the Lifecycle of all ViewHolders should be Destroyed.
        this.recyclerView?.run {
            (0 until childCount).forEach { childIndex ->
                getChildAt(childIndex)?.let { childView ->
                    (getChildViewHolder(childView) as BaseItemViewHolder<*, *>).onDestroy()
                }
            }
        }

        // As the RecyclerView is detached from the Adapter, clear the reference.
        this.recyclerView = null

    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     *
     * This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * [onViewDetachedFromWindow] those resources should be restored here.
     *
     * @param holder Holder of the view being attached
     */
    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        // When the ItemView is attached from Window, it becomes visible.
        // Hence the ViewHolder Lifecycle should be started
        holder.onStart()
    }

    /**
     * Called when a view created by this adapter has been detached from its window.
     *
     * Becoming detached from the window is not necessarily a permanent condition;
     * the consumer of an Adapter's views may choose to cache views offscreen while they
     * are not visible, attaching and detaching them as appropriate.
     *
     * @param holder Holder of the view being detached
     */
    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        // When the ItemView is detached from Window, it goes into the background (not visible).
        // Hence the ViewHolder Lifecycle should be stopped
        holder.onStop()
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [androidx.recyclerview.widget.RecyclerView.ViewHolder.itemView] to reflect
     * the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Delegate to the holder to bind the data to the ItemView at the position
        holder.bind(dataList[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = dataList.size

    /**
     * Method to load additional data [newDataList]. Takes care of signaling complete change
     * or range change to the Adapter appropriately.
     */
    fun appendMore(newDataList: List<T>) {
        // Get the current data list size
        val oldDataListSize = dataList.size
        // Append the new data list
        dataList.addAll(newDataList)
        // Get the updated data list size
        val updatedDataListSize = dataList.size

        if (oldDataListSize == 0 && updatedDataListSize > 0) {
            // Signal complete change when only new list has data
            notifyDataSetChanged()
        } else if (oldDataListSize in 1 until updatedDataListSize) {
            // Signal range change when old list also had data
            notifyItemRangeChanged(oldDataListSize - 1, updatedDataListSize - oldDataListSize)
        }
    }

    /**
     * Method to reset the Adapter [dataList] with the [newList] of data. Takes care of signaling complete
     * change when reloaded.
     */
    fun resetData(newList: List<T>) {
        // Clear all data on the Adapter
        dataList.clear()
        // Append the new data list
        dataList.addAll(newList)
        // Signal complete change after reload
        notifyDataSetChanged()
    }

}