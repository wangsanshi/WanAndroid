package com.lei.wanandroid.ui.adapter

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import coil.api.load
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.util.formatHtmlString

class ProjectArticlePagedListAdapter(private val lifecycleOwner: LifecycleOwner) :
    BasePagedListAdapter<Article, BaseViewHolder>(
        R.layout.item_project_article,
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

        holder.getView<ImageView>(R.id.ivPicture).load(article.envelopePic) {
            lifecycle(lifecycleOwner)
        }
        holder.setText(R.id.tvDate, article.niceDate)
    }
}