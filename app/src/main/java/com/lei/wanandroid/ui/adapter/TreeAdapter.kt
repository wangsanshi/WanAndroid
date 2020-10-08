package com.lei.wanandroid.ui.adapter

import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.google.android.flexbox.FlexboxLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Children
import com.lei.wanandroid.data.bean.Tree
import com.lei.wanandroid.ui.adapter.base.BaseQuickAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class TreeAdapter :
    BaseQuickAdapter<Tree, BaseViewHolder>(R.layout.item_navigation, TreeItemCallback()) {
    override fun convert(holder: BaseViewHolder, item: Tree) {
        holder.setText(R.id.tvName, item.name)
        bindChildren(holder.getView(R.id.flexLayout), item.children)
    }

    private fun bindChildren(layout: FlexboxLayout, childrens: List<Children>) {
        for (children in childrens) {
            (LayoutInflater.from(layout.context)
                .inflate(
                    R.layout.item_navigation_child,
                    layout,
                    false
                ) as AppCompatTextView).apply {
                text = children.name
                layout.addView(this)
            }
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        holder.getView<FlexboxLayout>(R.id.flexLayout).removeAllViews()
    }
}

private class TreeItemCallback : DiffUtil.ItemCallback<Tree>() {
    override fun areItemsTheSame(oldItem: Tree, newItem: Tree): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tree, newItem: Tree): Boolean {
        return oldItem.name == newItem.name
    }

}