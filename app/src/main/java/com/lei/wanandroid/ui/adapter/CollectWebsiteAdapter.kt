package com.lei.wanandroid.ui.adapter

import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import coil.api.load
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.WebSite
import com.lei.wanandroid.ui.adapter.base.BaseQuickAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class CollectWebsiteAdapter :
    BaseQuickAdapter<WebSite, BaseViewHolder>(
        R.layout.item_collect_website,
        WebSiteItemCallback()
    ) {

    override fun convert(holder: BaseViewHolder, item: WebSite) {
        holder.setText(R.id.tvName, item.name)
        holder.setText(R.id.tvLink, item.link)
        holder.getView<ImageView>(R.id.ivCollect).load(R.drawable.inset_collect)
    }

}

private class WebSiteItemCallback : DiffUtil.ItemCallback<WebSite>() {
    override fun areItemsTheSame(oldItem: WebSite, newItem: WebSite): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WebSite, newItem: WebSite): Boolean {
        return oldItem.name == newItem.name && oldItem.link == newItem.link
    }
}