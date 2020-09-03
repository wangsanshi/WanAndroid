package com.lei.wanandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lei.wanandroid.R
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class LoadMoreAdapter(private val retryCallback: () -> Unit) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var state = State.Idle

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_load_more
    }

    override fun getItemCount() = when (state) {
        State.Loading, State.Failure -> 1
        else -> 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (state) {
            State.Loading -> {
                holder.setVisible(R.id.progressBar, true)
                holder.setGone(R.id.tvError, true)
            }
            State.Failure -> {
                holder.setVisible(R.id.tvError, true)
                holder.setGone(R.id.progressBar, true)
                holder.getView<TextView>(R.id.tvError).setOnClickListener { retryCallback.invoke() }
            }
            else -> {
                //ignore
            }
        }
    }

    fun setState(newState: State) {
        if (state != newState) {
            state = newState
            notifyDataSetChanged()
        }
    }
}