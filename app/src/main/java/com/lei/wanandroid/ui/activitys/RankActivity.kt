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
import com.lei.wanandroid.databinding.ActivityRankBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.ui.adapter.page.CoinRankAdapter
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.helper.getLineItemDecoration
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.MyViewModel

class RankActivity : BaseActivity<MyViewModel, ActivityRankBinding>() {
    private lateinit var coinRankAdapter: CoinRankAdapter
    private lateinit var loadMoreAdapter: LoadMoreAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_rank
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRefreshLayout(mBinding.refreshLayout)
        initToolbar(mBinding.toolbar)
        initRecyclerView(mBinding.rvList)
    }

    override fun initData() {
        viewModel.coinRankRefreshState.observe(this, Observer {
            mBinding.refreshLayout.isRefreshing = it == State.Loading
            when (it) {
                State.Loading -> mBinding.container.showContent()
                State.Failure -> mBinding.container.showError()
                State.SuccessNoData -> mBinding.container.showEmpty()
                else -> {
                }
            }
        })
        viewModel.coinRankLoadMoreState.observe(this, Observer { loadMoreAdapter.setState(it) })
        viewModel.coinRankPagedList.observe(this, Observer { coinRankAdapter.submitList(it) })
        viewModel.getCoinRank()
    }

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshCoinRank() }
        mBinding.container.errorView?.findViewById<TextView>(R.id.tvRefresh)
            ?.setOnClickListener { viewModel.refreshCoinRank() }
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
        coinRankAdapter = CoinRankAdapter()
        loadMoreAdapter = LoadMoreAdapter { viewModel.retryCoinRank() }
        return MergeAdapter(coinRankAdapter, loadMoreAdapter)
    }

}