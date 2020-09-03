package com.lei.wanandroid.ui.adapter

import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Log.e("aaa", "onChanged")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                Log.e(
                    "aaa",
                    "onItemRangeChanged , positionStart=$positionStart , itemCount=$itemCount"
                )
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                Log.e(
                    "aaa",
                    "onItemRangeChanged, positionStart=$positionStart , itemCount=$itemCount"
                )
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                Log.e(
                    "aaa",
                    "onItemRangeMoved, fromPosition=$fromPosition , toPosition=$toPosition, itemCount=$itemCount"
                )
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.e(
                    "aaa",
                    "onItemRangeInserted , positionStart=$positionStart , itemCount=$itemCount"
                )
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                Log.e(
                    "aaa",
                    "onItemRangeRemoved , positionStart=$positionStart , itemCount=$itemCount"
                )
            }
        })
    }

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