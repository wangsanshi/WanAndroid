package com.lei.wanandroid.ui.adapter.base

import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * BRVAH功能太多了，这里拿来了部分实现
 */
abstract class BaseQuickAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutId: Int,
    itemCallback: DiffUtil.ItemCallback<T>
) :
    RecyclerView.Adapter<VH>() {
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null
    var onItemChildClickListener: OnItemChildClickListener? = null
    var onItemChildLongClickListener: OnItemChildLongClickListener? = null

    private val mDiffer = AsyncListDiffer(this, itemCallback)

    /**
     * 用于保存需要设置点击事件的 item
     */
    private val childClickViewIds = LinkedHashSet<Int>()

    /**
     * 用于保存需要设置长按点击事件的 item
     */
    private val childLongClickViewIds = LinkedHashSet<Int>()

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

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
            onBindViewHolder(holder, position)
        } else {
            convert(holder, getItem(position), payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        convert(holder, getItem(position))
    }

    open fun getItem(@IntRange(from = 0) position: Int): T {
        return mDiffer.currentList.get(position)
    }

    fun isEmpty() = itemCount == 0

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

    protected open fun convert(holder: VH, item: T, payloads: List<Any>) {}

    protected abstract fun convert(holder: VH, item: T)

    open fun submitList(list: List<T>?) {
        this.submitList(list, null)
    }

    open fun submitList(list: List<T>?, comitCallback: Runnable?) {
        mDiffer.submitList(list, comitCallback)
    }
}