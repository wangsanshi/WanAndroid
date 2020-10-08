package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentSquareBinding
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
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

    override fun refreshData() {
        super.refreshData()
        viewModel.squareArticleListing.value?.refresh?.invoke()
    }

    override fun initLazy() {
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.squareArticleListing,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = false,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = null,
            needRefresh = true
        )
    }

}