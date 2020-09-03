package com.lei.wanandroid.ui.adapter.base

import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import java.util.*

abstract class BasePagedListAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutId: Int,
    itemCallback: DiffUtil.ItemCallback<T>
) :
    PagedListAdapter<T, VH>(itemCallback) {

    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null
    var onItemChildClickListener: OnItemChildClickListener? = null
    var onItemChildLongClickListener: OnItemChildLongClickListener? = null

    /**
     * 用于保存需要设置点击事件的 item
     */
    private val childClickViewIds = LinkedHashSet<Int>()

    /**
     * 用于保存需要设置长按点击事件的 item
     */
    private val childLongClickViewIds = LinkedHashSet<Int>()

    override fun getItemViewType(position: Int): Int {
        return layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = createViewHolder(parent.getItemView(layoutId))
        setListeners(
            viewHolder,
            childClickViewIds,
            childLongClickViewIds,
            onItemClickListener,
            onItemLongClickListener,
            onItemChildClickListener,
            onItemChildLongClickListener
        )
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position)
        } else {
            convert(holder, getItem(position), payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        convert(holder, getItem(position), position)
    }

    override public fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    /**
     * 设置需要点击事件的子view
     * @param viewIds IntArray
     */
    fun addChildClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childClickViewIds.add(viewId)
        }
    }

    /**
     * 设置需要长按点击事件的子view
     * @param viewIds IntArray
     */
    fun addChildLongClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childLongClickViewIds.add(viewId)
        }
    }

    fun isEmpty() = itemCount == 0

    protected open fun convert(holder: VH, item: T?, payloads: List<Any>) {}

    protected abstract fun convert(holder: VH, item: T?, position: Int)
}