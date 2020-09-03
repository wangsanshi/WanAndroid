package com.lei.wanandroid.ui.adapter

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import coil.api.load
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.util.formatHtmlString

const val KEY_IS_COLLECT = "key_is_collect"

class CommonArticlePagedListAdapter(canShowUserInfo: Boolean = true) :
    BasePagedListAdapter<Article, BaseViewHolder>(R.layout.item_article, ArticleItemCallback()) {

    init {
        addChildClickViewIds(R.id.ivCollect)
        if (canShowUserInfo) addChildClickViewIds(R.id.llContainer)
    }

    override fun convert(holder: BaseViewHolder, item: Article?, position: Int) {
        item?.let { bindArticle(holder, it) }
    }

    @Suppress("DEPRECATION")
    private fun bindArticle(holder: BaseViewHolder, article: Article) {
        holder.setText(R.id.tvTitle, formatHtmlString(article.title))

        if (article.collect) holder.getView<ImageView>(R.id.ivCollect)
            .load(R.drawable.inset_collect) else holder.getView<ImageView>(R.id.ivCollect)
            .load(R.drawable.inset_uncollect)

        val tvType = holder.getView<TextView>(R.id.tvType)
        val tvInfo = holder.getView<TextView>(R.id.tvName)
        if (article.author.isNotEmpty()) {
            //该文章是自己发表的
            tvType.text = Utils.getApp().getString(R.string.publish)
            tvType.setTextColor(Utils.getApp().resources.getColor(R.color.color_article_type_publish))
            tvType.background.level = 1
            tvInfo.text = article.author
        } else {
            //该文章为分享的
            tvType.text = Utils.getApp().getString(R.string.share)
            tvType.setTextColor(Utils.getApp().resources.getColor(R.color.color_article_type_share))
            tvType.background.level = 0
            tvInfo.text = article.shareUser
        }

        val tvDes = holder.getView<TextView>(R.id.tvDes)
        if (article.desc.isNotEmpty()) {
            tvDes.text = formatHtmlString(article.desc)
            tvDes.visibility = View.VISIBLE
        } else {
            tvDes.text = ""
            tvDes.visibility = View.GONE
        }
        val chapter = when {
            article.chapterName.isNotEmpty() -> article.chapterName
            article.superChapterName.isNotEmpty() -> article.superChapterName
            else -> ""
        }
        holder.setText(R.id.tvChapterName, chapter)
        holder.setText(R.id.tvDate, article.niceDate)
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
}

class ArticleItemCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.title == newItem.title && oldItem.link == newItem.link && oldItem.collect == newItem.collect
    }

    override fun getChangePayload(oldItem: Article, newItem: Article): Any? {
        return if (oldItem.collect != newItem.collect) Bundle().apply {
            putBoolean(
                KEY_IS_COLLECT,
                newItem.collect
            )
        } else null
    }
}