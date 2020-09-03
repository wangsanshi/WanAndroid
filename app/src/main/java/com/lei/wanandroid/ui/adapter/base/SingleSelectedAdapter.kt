package com.lei.wanandroid.ui.adapter.base

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayout

typealias OnSelectedChangeListener<T> = (T) -> Unit

abstract class SingleSelectedAdapter<T, VH : BaseViewHolder>(
    @LayoutRes layoutId: Int,
    itemCallback: DiffUtil.ItemCallback<SelectItem<T>>
) :
    BaseQuickAdapter<SelectItem<T>, VH>(layoutId, itemCallback) {
    private var selectedPosition = -1

    var onSelectChangeListener: OnSelectedChangeListener<T>? = null

    init {
        onItemClickListener = { _, _, position ->
            if (selectedPosition != position) {
                if (selectedPosition != -1) {
                    getItem(selectedPosition).isSelect = false
                    notifyItemChanged(selectedPosition)
                }
                getItem(position).isSelect = true
                notifyItemChanged(position)
                selectedPosition = position
                onSelectChangeListener?.invoke(getItem(selectedPosition).data)
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is FlexboxLayout.LayoutParams) {
            layoutParams.flexGrow = 1.0f
            layoutParams.alignSelf = AlignSelf.FLEX_END
        }

        super.onBindViewHolder(holder, position)
    }

    fun clearSelectedState() {
        val lastSelectedPosition = selectedPosition
        selectedPosition = -1
        if (lastSelectedPosition != -1) {
            getItem(lastSelectedPosition).isSelect = false
            notifyItemChanged(lastSelectedPosition)
        }
    }

    fun submitListWithNoSelected(list: List<T>?) {
        submitListWithDedaultSelected(list, -1)
    }

    fun submitListWithDedaultSelected(list: List<T>?, defaultSelectPosition: Int = 0) {
        val destList = list?.map { SelectItem(false, it) }
        super.submitList(destList, Runnable {
            if (defaultSelectPosition in 0 until itemCount) {
                getItem(defaultSelectPosition).isSelect = true
                notifyItemChanged(defaultSelectPosition)
                onSelectChangeListener?.invoke(getItem(defaultSelectPosition).data)
                selectedPosition = defaultSelectPosition
            } else {
                Log.w(
                    "SingleSelectedAdapter",
                    "set default selected error , please check defaultSelectPosition"
                )
            }
        })
    }
}

data class SelectItem<T>(var isSelect: Boolean, val data: T)
