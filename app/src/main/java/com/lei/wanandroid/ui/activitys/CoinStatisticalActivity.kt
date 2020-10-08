package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityCoinStatisticalBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.adapter.page.CoinStatisticalPagedListAdapter
import com.lei.wanandroid.ui.helper.getLineItemDecoration
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.MyViewModel

class CoinStatisticalActivity : BaseActivity<MyViewModel, ActivityCoinStatisticalBinding>() {
    private lateinit var coinStatisticalAdapter: CoinStatisticalPagedListAdapter
    private lateinit var loadMoreAdapter: LoadMoreAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_coin_statistical
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRefreshLayout(mBinding.refreshLayout)
        initToolbar(mBinding.toolbar)
        initRecyclerView(mBinding.rvList)
    }

    override fun initData() {
        viewModel.coinStatisticalRefreshState.observe(this, Observer {
            mBinding.refreshLayout.isRefreshing = it == State.Loading
            when (it) {
                State.Loading -> mBinding.container.showContent()
                State.Failure -> mBinding.container.showError()
                State.SuccessNoData -> mBinding.container.showEmpty()
                else -> {
                }
            }
        })
        viewModel.coinStatisticalLoadMoreState.observe(
            this,
            Observer { loadMoreAdapter.setState(it) })
        viewModel.coinStatisticalPagedList.observe(
            this,
            Observer { coinStatisticalAdapter.submitList(it) })
        viewModel.getCoinStatistical()
    }

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshCoinStatistical() }
        mBinding.container.errorView?.findViewById<TextView>(R.id.tvRefresh)
            ?.setOnClickListener { viewModel.refreshCoinStatistical() }
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(getLineItemDecoration())
        recyclerView.adapter = getAdapter()
    }

    private fun getAdapter(): RecyclerView.Adapter<*> {
        coinStatisticalAdapter =
            CoinStatisticalPagedListAdapter()
        loadMoreAdapter = LoadMoreAdapter { viewModel.retryCoinStatistical() }
        return MergeAdapter(coinStatisticalAdapter, loadMoreAdapter)
    }
}