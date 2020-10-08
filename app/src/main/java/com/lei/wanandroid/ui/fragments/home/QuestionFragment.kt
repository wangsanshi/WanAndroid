package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentQuestionBinding
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
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

    override fun refreshData() {
        super.refreshData()
        viewModel.questionArticleListing.value?.refresh?.invoke()
    }

    override fun initLazy() {
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.questionArticleListing,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = false,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = null,
            needRefresh = true
        )
    }

}