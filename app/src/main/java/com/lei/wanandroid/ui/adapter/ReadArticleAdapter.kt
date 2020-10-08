package com.lei.wanandroid.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class ReadArticleAdapter :
    BasePagedListAdapter<ReadArticle, BaseViewHolder>(
        R.layout.item_read_article,
        ReadArticleItemCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: ReadArticle?, position: Int) {
        item?.let {
            val author = when {
                it.author.isNotEmpty() -> it.author
                else -> "(未知)"
            }
            holder.setText(R.id.tvName, author)
            holder.setText(R.id.tvTitle, it.title)
            if (it.desc.isEmpty()) {
                holder.setGone(R.id.tvDes, true)
            } else {
                holder.setVisible(R.id.tvDes, true)
                holder.setText(R.id.tvDes, it.desc)
            }
        }
    }

}

class ReadArticleItemCallback : DiffUtil.ItemCallback<ReadArticle>() {
    override fun areItemsTheSame(oldItem: ReadArticle, newItem: ReadArticle): Boolean {
        return oldItem.articleId == newItem.articleId
    }

    override fun areContentsTheSame(oldItem: ReadArticle, newItem: ReadArticle): Boolean {
        return oldItem.title == newItem.title && oldItem.link == newItem.link && oldItem.readRime == newItem.readRime
    }
}