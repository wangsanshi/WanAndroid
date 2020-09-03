package com.lei.wanandroid.ui.adapter

import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import coil.api.load
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.CollectArticle
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class CollectArticlePagedListAdapter :
    BasePagedListAdapter<CollectArticle, BaseViewHolder>(
        R.layout.item_article,
        CollectArticleCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: CollectArticle?, position: Int) {
        item?.let { bindCollectArticle(holder, it) }
    }

    private fun bindCollectArticle(holder: BaseViewHolder, article: CollectArticle) {
        if (article.author.isBlank()) holder.setText(
            R.id.tvName,
            "(未知)"
        ) else holder.setText(R.id.tvName, article.author)
        holder.setGone(R.id.tvType, true)
        holder.setText(R.id.tvChapterName, article.chapterName)
        holder.setText(R.id.tvTitle, article.title)
        if (article.desc.isBlank()) holder.setGone(R.id.tvDes, true) else holder.setText(
            R.id.tvDes,
            article.desc
        )
        holder.setText(R.id.tvDate, article.niceDate)
        holder.getView<ImageView>(R.id.ivCollect).load(R.drawable.inset_collect)
    }
}

class CollectArticleCallback : DiffUtil.ItemCallback<CollectArticle>() {
    override fun areItemsTheSame(oldItem: CollectArticle, newItem: CollectArticle): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CollectArticle, newItem: CollectArticle): Boolean {
        return oldItem.title == newItem.title && oldItem.link == newItem.link
    }
}