package com.lei.wanandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.util.formatHtmlString

class TopArticleAdapter(private val onItemClickListener: ((Article) -> Unit)? = null) :
    ListAdapter<Article, BaseViewHolder>(ArticleItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_top_article, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_top_article

    fun isEmpty() = super.getItemCount() == 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindToArticle(holder, getItem(position))
    }

    private fun bindToArticle(holder: BaseViewHolder, article: Article) {
        onItemClickListener?.let {
            holder.itemView.setOnClickListener { _ ->
                it.invoke(article)
            }
        }
        holder.setText(R.id.tvTitle, formatHtmlString(article.title))
        val author = when {
            article.author.isNotEmpty() -> article.author
            article.shareUser.isNotEmpty() -> article.shareUser
            else -> "(未知)"
        }
        holder.setText(R.id.tvName, author)
        holder.setText(R.id.tvDate, article.niceDate)
        holder.setText(R.id.tvZan, Utils.getApp().getString(R.string.zan_format, article.zan))
    }
}

class TopArticleHeaderAdapter(private val closeCallback: (() -> Unit)? = null) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var hasData = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.header_top_articles, parent, false)
    )

    override fun getItemCount() = if (hasData) 1 else 0

    override fun getItemViewType(position: Int) = R.layout.header_top_articles

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        closeCallback?.let {
            holder.getViewOrNull<ImageView>(R.id.ivClose)?.setOnClickListener { _ -> it.invoke() }
        }
    }

}