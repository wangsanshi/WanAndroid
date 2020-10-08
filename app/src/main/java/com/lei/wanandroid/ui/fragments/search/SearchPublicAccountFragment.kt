package com.lei.wanandroid.ui.fragments.search

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.databinding.FragmentSearchPublicAccountBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.ui.adapter.WechatPublicAccountAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.SearchViewModel

class SearchPublicAccountFragment :
    BaseLazyFragment<SearchViewModel, FragmentSearchPublicAccountBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout, false)
        initAccount(getBinding().rvPublicAccount)
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.wechatListingLiveData,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = true,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = viewModel.clearNotify,
            needRefresh = false
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_public_account
    }

    override fun provideViewModel(): SearchViewModel {
        return ViewModelProviders.of(requireActivity()).get(SearchViewModel::class.java)
    }

    private fun initAccount(recyclerView: RecyclerView) {
        val accountAdapter = getAccountAdapter()
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = accountAdapter
        initContainerView(getBinding().container, accountAdapter)

        viewModel.publicAccountsLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<List<PublicAccount>> {
                override fun onSuccess(value: List<PublicAccount>) {
                    getBinding().refreshLayout.isRefreshing = false
                    accountAdapter.submitListWithDedaultSelected(value)
                }

                override fun onLoading() {
                    getBinding().refreshLayout.isRefreshing = true
                    getBinding().container.showContent()
                }

                override fun onFailure(message: String) {
                    getBinding().refreshLayout.isRefreshing = false
                    getBinding().container.showError()
                }
            })
        )
    }

    override fun initLazy() {
        viewModel.getPublicAccounts()
    }

    private fun getAccountAdapter(): WechatPublicAccountAdapter {
        return WechatPublicAccountAdapter().apply {
            onSelectChangeListener = {
                viewModel.selectedPublicAccountLiveData.value = it
            }
        }
    }

    private fun initContainerView(
        containerView: ContainerView,
        adapter: WechatPublicAccountAdapter
    ) {
        containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)
            ?.setOnClickListener { refresh(adapter, viewModel.wechatListingLiveData) }
    }

    private fun refresh(
        adapter: WechatPublicAccountAdapter,
        listingLiveData: LiveData<Listing<Article>>
    ) {
        if (adapter.isEmpty()) viewModel.getPublicAccounts() else listingLiveData.value?.refresh?.invoke()
    }
}