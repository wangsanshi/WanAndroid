package com.lei.wanandroid.ui.adapter.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.ui.adapter.base.setListeners
import com.lei.wanandroid.util.formatHtmlString

class FirstPageArticlePagedListAdapter :
    BasePagedListAdapter<Article, BaseViewHolder>(R.layout.item_article,
        ArticleItemCallback()
    ) {
    companion object {
        private const val TYPE_TOP_ARTICLE = 1
    }

    private val itemTopArticleId = R.layout.item_top_article

    init {
        addChildClickViewIds(R.id.ivCollect, R.id.llContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == itemTopArticleId) BaseViewHolder.get(
            parent.context,
            parent,
            itemTopArticleId
        ).apply {
            setListeners(
                this,
                childClickViewIds,
                childLongClickViewIds,
                onItemClickListener,
                onItemLongClickListener,
                onItemChildClickListener,
                onItemChildLongClickListener
            )
        } else super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.type == TYPE_TOP_ARTICLE) itemTopArticleId else super.getItemViewType(
            position
        )
    }

    override fun convert(holder: BaseViewHolder, item: Article?, position: Int) {
        item?.let { if (it.type == 1) bindTopArticle(holder, it) else bindArticle(holder, it) }
    }

    private fun bindTopArticle(holder: BaseViewHolder, article: Article) {
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

    @Suppress("DEPRECATION")
    private fun bindArticle(holder: BaseViewHolder, article: Article) {
        holder.setText(R.id.tvTitle, formatHtmlString(article.title))

        holder.getView<ImageView>(R.id.ivCollect).run {
            if (article.collect) this.setImageResource(R.drawable.inset_collect) else this.setImageResource(
                R.drawable.inset_uncollect
            )
        }

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
        holder.getView<ImageView>(R.id.ivCollect).run {
            if (isCollect) this.setImageResource(R.drawable.inset_collect) else this.setImageResource(
                R.drawable.inset_uncollect
            )
        }
    }
}