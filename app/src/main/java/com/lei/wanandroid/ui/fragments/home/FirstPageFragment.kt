package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentFirstpageBinding
import com.lei.wanandroid.ui.helper.initFirstArticlePagedList
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.HomeViewModel

/**
 * 首页
 */
class FirstPageFragment : BaseLazyFragment<HomeViewModel, FragmentFirstpageBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_firstpage
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout)
    }

    override fun initLazyData() {
        initFirstArticlePagedList(
            this,
            getBinding().refreshLayout,
            getBinding().rvArticle,
            getBinding().container,
            viewModel.articleRefreshState,
            viewModel.articleLoadMoreState,
            viewModel.articlesPagedList,
            { viewModel.retryArticle() },
            { viewModel.refreshArticles() },
            viewModel.isShowTopArticles,
            viewModel.topArticlesLiveData,
            viewModel::modifyArticleCollectState,
            false
        )
        viewModel.initArticles()
        viewModel.refreshArticles()
    }

}