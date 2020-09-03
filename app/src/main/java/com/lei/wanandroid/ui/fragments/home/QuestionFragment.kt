package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentQuestionBinding
import com.lei.wanandroid.ui.helper.initCommonArticlePagedList
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.HomeViewModel

/**
 * 问答
 */
class QuestionFragment : BaseLazyFragment<HomeViewModel, FragmentQuestionBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_question
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
            viewModel.questionArticleRefreshState,
            viewModel.questionArticleLoadMoreState,
            viewModel.questionArticlePagedList,
            { viewModel.retryQuestionArticle() },
            { viewModel.refreshQuestionArticles() },
            null,
            viewModel::modifyArticleCollectState,
            false
        )
        viewModel.initQuestionArticles()
        viewModel.refreshQuestionArticles()
    }

}