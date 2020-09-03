package com.lei.wanandroid.ui.adapter

import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.recyclerview.widget.DiffUtil
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.HotWord
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.ui.adapter.base.SelectItem
import com.lei.wanandroid.ui.adapter.base.SingleSelectedAdapter

class HotWordAdapter : SingleSelectedAdapter<HotWord, BaseViewHolder>(
    R.layout.item_checked_textview_match_parent_width,
    HotWordItemCallback()
) {
    override fun convert(holder: BaseViewHolder, item: SelectItem<HotWord>) {
        val itemView = holder.getView<AppCompatCheckedTextView>(R.id.ctvName)
        with(itemView) {
            this.text = item.data.name
            this.isChecked = item.isSelect
        }
    }
}

private class HotWordItemCallback : DiffUtil.ItemCallback<SelectItem<HotWord>>() {
    override fun areItemsTheSame(
        oldItem: SelectItem<HotWord>,
        newItem: SelectItem<HotWord>
    ): Boolean {
        return oldItem === newItem || oldItem.data.id == newItem.data.id
    }

    override fun areContentsTheSame(
        oldItem: SelectItem<HotWord>,
        newItem: SelectItem<HotWord>
    ): Boolean {
        return oldItem.data.name == newItem.data.name && oldItem.isSelect == newItem.isSelect
    }
}
