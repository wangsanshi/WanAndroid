package com.lei.wanandroid.ui.adapter

import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.recyclerview.widget.DiffUtil
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.ui.adapter.base.SelectItem
import com.lei.wanandroid.ui.adapter.base.SingleSelectedAdapter

class WechatPublicAccountAdapter() :
    SingleSelectedAdapter<PublicAccount, BaseViewHolder>(
        R.layout.item_checked_textview_wrap_content_width,
        WechatItemCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: SelectItem<PublicAccount>) {
        val itemView = holder.getView<AppCompatCheckedTextView>(R.id.ctvName)
        with(itemView) {
            this.text = item.data.name
            this.isChecked = item.isSelect
        }
    }
}

private class WechatItemCallback : DiffUtil.ItemCallback<SelectItem<PublicAccount>>() {
    override fun areItemsTheSame(
        oldItem: SelectItem<PublicAccount>,
        newItem: SelectItem<PublicAccount>
    ): Boolean {
        return oldItem === newItem || oldItem.data.id == newItem.data.id
    }

    override fun areContentsTheSame(
        oldItem: SelectItem<PublicAccount>,
        newItem: SelectItem<PublicAccount>
    ): Boolean {
        return oldItem.data.name == newItem.data.name && oldItem.isSelect == newItem.isSelect
    }

}