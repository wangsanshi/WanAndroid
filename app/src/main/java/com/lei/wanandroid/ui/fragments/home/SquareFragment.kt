package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentSquareBinding
import com.lei.wanandroid.ui.helper.initCommonArticlePagedList
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.HomeViewModel

/**
 * 广场
 */
class SquareFragment : BaseLazyFragment<HomeViewModel, FragmentSquareBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_square
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun initLazyData() {
        initCommonArticlePagedList(
            this,
            getBinding().refreshLayout,
            getBinding().rvArticle,
            getBinding().container,
            viewModel.squareArticleRefreshState,
            viewModel.squareArticleLoadMoreState,
            viewModel.squareArticlePagedList,
            { viewModel.retrySquareArticle() },
            { viewModel.refreshSquareArticles() },
            null,
            viewModel::modifyArticleCollectState,
            false
        )
        viewModel.initSquareArticles()
        viewModel.refreshSquareArticles()
    }

}