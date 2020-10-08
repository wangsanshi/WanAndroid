package com.lei.wanandroid.ui.adapter.page

import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.CoinSource
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.util.getDateByMillis
import com.lei.wanandroid.util.sdf3

class CoinStatisticalPagedListAdapter :
    BasePagedListAdapter<CoinSource, BaseViewHolder>(
        R.layout.item_coin_statistical,
        CoinStatisticalCallback()
    ) {
    override fun convert(holder: BaseViewHolder, item: CoinSource?, position: Int) {
        item?.let {
            holder.setText(R.id.tvSerial, (position + 1).toString())
            holder.setText(R.id.tvReason, it.reason)
            holder.setText(R.id.tvDate, getDateByMillis(it.date, sdf3.get()!!))
            holder.setText(R.id.tvCoin, Utils.getApp().getString(R.string.add_coin, it.coinCount))
        }
    }
}

class CoinStatisticalCallback : DiffUtil.ItemCallback<CoinSource>() {
    override fun areItemsTheSame(oldItem: CoinSource, newItem: CoinSource): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CoinSource, newItem: CoinSource): Boolean {
        return oldItem.coinCount == newItem.coinCount && oldItem.reason == newItem.reason && oldItem.type == newItem.type && oldItem.date == newItem.date
    }

}