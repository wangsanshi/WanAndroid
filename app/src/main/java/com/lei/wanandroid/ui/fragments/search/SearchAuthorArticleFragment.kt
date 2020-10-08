package com.lei.wanandroid.ui.fragments.search

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentSearchAuthorArticleBinding
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.SearchViewModel

class SearchAuthorArticleFragment :
    BaseLazyFragment<SearchViewModel, FragmentSearchAuthorArticleBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout, false)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_author_article
    }

    override fun provideViewModel(): SearchViewModel {
        return ViewModelProviders.of(requireActivity()).get(SearchViewModel::class.java)
    }

    override fun initLazy() {
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.authorListingLiveData,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = true,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = viewModel.clearNotify,
            needRefresh = true
        )
    }
}