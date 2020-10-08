package com.lei.wanandroid.ui.adapter.page

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.CoinInfo
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder

class CoinRankAdapter :
    BasePagedListAdapter<CoinInfo, BaseViewHolder>(R.layout.item_coin_rank,
        CoinRankCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: CoinInfo?, position: Int) {
        item?.let { bindCoinInfo(holder, it) }
    }

    private fun bindCoinInfo(holder: BaseViewHolder, info: CoinInfo) {
        holder.setText(R.id.tvLevel, "Lv.${info.level}")
        holder.setText(R.id.tvCoin, "BP:${info.coinCount}")
        holder.setText(R.id.tvUserName, info.username)
        when (info.rank) {
            "1" -> {
                holder.setBackgroundResource(R.id.tvRank, R.drawable.ic_rank_first)
                holder.setText(R.id.tvRank, "")
            }
            "2" -> {
                holder.setBackgroundResource(R.id.tvRank, R.drawable.ic_rank_second)
                holder.setText(R.id.tvRank, "")
            }
            "3" -> {
                holder.setBackgroundResource(R.id.tvRank, R.drawable.ic_rank_third)
                holder.setText(R.id.tvRank, "")
            }
            else -> {
                holder.getView<TextView>(R.id.tvRank).background = null
                holder.setText(R.id.tvRank, info.rank)
            }
        }
    }
}

class CoinRankCallback : DiffUtil.ItemCallback<CoinInfo>() {
    override fun areItemsTheSame(oldItem: CoinInfo, newItem: CoinInfo): Boolean {
        return oldItem.userId == oldItem.userId
    }

    override fun areContentsTheSame(oldItem: CoinInfo, newItem: CoinInfo): Boolean {
        return oldItem.coinCount == newItem.coinCount && oldItem.level == newItem.level && oldItem.rank == newItem.rank
    }
}