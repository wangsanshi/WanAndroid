package com.lei.wanandroid.ui.adapter

import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.google.android.flexbox.FlexboxLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.Navigation
import com.lei.wanandroid.ui.adapter.base.BaseQuickAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class NavigationAdapter :
    BaseQuickAdapter<Navigation, BaseViewHolder>(
        R.layout.item_navigation,
        NavigationItemCallback()
    ) {

    override fun convert(holder: BaseViewHolder, item: Navigation) {
        holder.setText(R.id.tvName, item.name)
        bindArticles(holder.getView(R.id.flexLayout), item.articles)
    }

    private fun bindArticles(layout: FlexboxLayout, articles: List<Article>) {
        for (article in articles) {
            (LayoutInflater.from(layout.context)
                .inflate(
                    R.layout.item_navigation_child,
                    layout,
                    false
                ) as AppCompatTextView).apply {
                text = article.title
                layout.addView(this)
            }
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        holder.getView<FlexboxLayout>(R.id.flexLayout).removeAllViews()
    }
}

private class NavigationItemCallback : DiffUtil.ItemCallback<Navigation>() {
    override fun areItemsTheSame(oldItem: Navigation, newItem: Navigation): Boolean {
        return oldItem.cid == newItem.cid
    }

    override fun areContentsTheSame(oldItem: Navigation, newItem: Navigation): Boolean {
        return oldItem.name == newItem.name
    }
}