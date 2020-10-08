package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentFirstpageBinding
import com.lei.wanandroid.ui.helper.FirstPageRecyclerViewDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
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

    override fun refreshData() {
        super.refreshData()
        viewModel.articleListingLiveData.value?.refresh?.invoke()
    }

    override fun initLazy() {
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.articleListingLiveData,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = false,
            itemDecoration = FirstPageRecyclerViewDecoration(),
            showTopArticle = true,
            clear = null,
            needRefresh = true
        )
    }

}