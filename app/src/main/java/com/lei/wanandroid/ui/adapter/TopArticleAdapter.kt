package com.lei.wanandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lei.wanandroid.R
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class TopArticleHeaderAdapter(private val closeCallback: (() -> Unit)? = null) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.header_top_articles, parent, false)
    )

    override fun getItemCount() = 1

    override fun getItemViewType(position: Int) = R.layout.header_top_articles

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        closeCallback?.let {
            holder.getViewOrNull<ImageView>(R.id.ivClose)?.setOnClickListener { _ -> it.invoke() }
        }
    }

}