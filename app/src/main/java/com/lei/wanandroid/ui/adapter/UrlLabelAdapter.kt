package com.lei.wanandroid.ui.adapter

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.lei.wanandroid.R
import com.lei.wanandroid.ui.adapter.base.BaseQuickAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class UrlLabelAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_url_label, UrlLabelCallback()) {
    override fun convert(holder: BaseViewHolder, item: String) {
        (holder.itemView as TextView).text = item
    }
}

class UrlLabelCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}