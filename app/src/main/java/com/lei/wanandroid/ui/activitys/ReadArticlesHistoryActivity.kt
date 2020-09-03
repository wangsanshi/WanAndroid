package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityReadArticlesHistoryBinding
import com.lei.wanandroid.ui.adapter.ReadArticleAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.DealExceptionLinearLayoutManager
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.viewmodel.ReadArticlesHistoryViewModel

class ReadArticlesHistoryActivity :
    BaseActivity<ReadArticlesHistoryViewModel, ActivityReadArticlesHistoryBinding>() {
    private lateinit var adapter: ReadArticleAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_read_articles_history
    }

    override fun initView(savedInstanceState: Bundle?) {
        initContainer(mBinding.container)
        initToolbar(mBinding.toolbar)
        initRecyclerView(mBinding.rvArticle)
    }

    private fun initContainer(container: ContainerView) {
        container.showEmpty()
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initRecyclerView(rvArticle: RecyclerView) {
        rvArticle.layoutManager = DealExceptionLinearLayoutManager(this)
        rvArticle.addItemDecoration(getTransparentItemDecoration())
        adapter = ReadArticleAdapter()
        adapter.onItemClickListener = { _, _, position ->
            adapter.getItem(position)?.let {
                it.readRime = System.currentTimeMillis()
                WebViewActivity.toThis(it)
            }
        }
        rvArticle.adapter = adapter
    }

    override fun initData() {
        viewModel.getPagedListLiveData().observe(this, Observer {
            if (it.isNotEmpty()) mBinding.container.showContent()
            adapter.submitList(it)
        })
    }

    override fun provideViewModel(): ReadArticlesHistoryViewModel {
        return ViewModelProviders.of(this).get(ReadArticlesHistoryViewModel::class.java)
    }
}