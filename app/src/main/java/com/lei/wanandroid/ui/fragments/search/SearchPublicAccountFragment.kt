package com.lei.wanandroid.ui.fragments.search

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.databinding.FragmentSearchPublicAccountBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.WechatPublicAccountAdapter
import com.lei.wanandroid.ui.helper.initCommonArticlePagedList
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.SearchViewModel

class SearchPublicAccountFragment :
    BaseLazyFragment<SearchViewModel, FragmentSearchPublicAccountBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout, false)
        initAccount(getBinding().rvPublicAccount)
        initCommonArticlePagedList(
            this,
            getBinding().refreshLayout,
            getBinding().rvArticle,
            getBinding().container,
            viewModel.wechatRefreshState,
            viewModel.wechatLoadMoreState,
            viewModel.wechatPagedList,
            { viewModel.retryWechatArticles() },
            { viewModel.refreshWechatArticles() },
            viewModel.clearNotify,
            viewModel::modifyArticleCollectState,
            true
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_public_account
    }

    override fun provideViewModel(): SearchViewModel {
        return ViewModelProviders.of(requireActivity()).get(SearchViewModel::class.java)
    }

    private fun initAccount(recyclerView: RecyclerView) {
        val accountAdapter = WechatPublicAccountAdapter()
        accountAdapter.onSelectChangeListener =
            { viewModel.selectedPublicAccountLiveData.value = it }
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = accountAdapter
        viewModel.publicAccountsLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<List<PublicAccount>> {
                override fun onSuccess(value: List<PublicAccount>) {
                    accountAdapter.submitListWithDedaultSelected(value)
                }

                override fun onLoading() {
                }

                override fun onFailure(message: String) {
                    showShortToast(message)
                }
            })
        )
    }

    override fun initLazyData() {
        viewModel.getPublicAccounts()
    }
}