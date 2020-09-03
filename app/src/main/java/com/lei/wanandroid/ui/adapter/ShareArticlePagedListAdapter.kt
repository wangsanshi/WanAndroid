package com.lei.wanandroid.ui.adapter

import android.os.Bundle
import android.widget.ImageView
import coil.api.load
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.util.formatHtmlString

class ShareArticlePagedListAdapter(private val canDelete: Boolean = false) :
    BasePagedListAdapter<Article, BaseViewHolder>(
        R.layout.item_share_article,
        ArticleItemCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: Article?, position: Int) {
        item?.let { bindArticle(holder, it) }
    }

    override fun convert(holder: BaseViewHolder, item: Article?, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        val isCollect = (payloads[0] as Bundle).getBoolean(KEY_IS_COLLECT)
        if (isCollect) {
            holder.getView<ImageView>(R.id.ivCollect).load(R.drawable.inset_collect)
        } else {
            holder.getView<ImageView>(R.id.ivCollect).load(R.drawable.inset_uncollect)
        }
    }

    private fun bindArticle(holder: BaseViewHolder, article: Article) {
        holder.setText(R.id.tvTitle, formatHtmlString(article.title))
        if (article.desc.isBlank()) {
            holder.setGone(R.id.tvDes, true)
        } else {
            holder.setVisible(R.id.tvDes, true)
            holder.setText(R.id.tvDes, article.desc)
        }
        holder.setText(R.id.tvDate, article.niceDate)
        holder.setText(
            R.id.tvAuthor, when {
                article.author.isNotBlank() -> article.author
                article.shareUser.isNotBlank() -> article.shareUser
                else -> "(未知)"
            }
        )
        if (article.collect) holder.getView<ImageView>(R.id.ivCollect)
            .load(R.drawable.inset_collect) else holder.getView<ImageView>(R.id.ivCollect)
            .load(R.drawable.inset_uncollect)
        if (canDelete) holder.setVisible(R.id.ivDelete, true) else holder.setGone(
            R.id.ivDelete,
            true
        )
    }
}