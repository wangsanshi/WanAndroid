package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.databinding.FragmentPublicAccountBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.WechatPublicAccountAdapter
import com.lei.wanandroid.ui.helper.initCommonArticlePagedList
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.HomeViewModel

/**
 * 公众号
 */
class PublicAccountFragment : BaseLazyFragment<HomeViewModel, FragmentPublicAccountBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_public_account
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    private fun initAccounts(refreshLayout: SwipeRefreshLayout, rvPublicAccount: RecyclerView) {
        val accountAdapter = WechatPublicAccountAdapter()
        accountAdapter.onSelectChangeListener = {
            viewModel.initWechatArticlesByID(it.id)
            viewModel.refreshWechatArticles()
        }

        refreshLayout.setOnRefreshListener {
            if (accountAdapter.isEmpty()) {
                viewModel.getPublicAccounts()
            } else {
                viewModel.refreshWechatArticles()
            }
        }

        rvPublicAccount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvPublicAccount.adapter = accountAdapter

        viewModel.publicAccountsLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<List<PublicAccount>> {
                override fun onSuccess(value: List<PublicAccount>) {
                    refreshLayout.isRefreshing = false
                    accountAdapter.submitListWithDedaultSelected(value)
                }

                override fun onLoading() {
                    refreshLayout.isRefreshing = true
                }

                override fun onFailure(message: String) {
                    refreshLayout.isRefreshing = false
                    showShortToast(message)
                }
            })
        )
    }

    override fun initLazyData() {
        initAccounts(getBinding().refreshLayout, getBinding().rvPublicAccount)
        initCommonArticlePagedList(
            this,
            getBinding().refreshLayout,
            getBinding().rvArticle,
            getBinding().container,
            viewModel.wechatArticleRefreshState,
            viewModel.wechatArticleLoadMoreState,
            viewModel.wechatArticlePagedList,
            { viewModel.retryWechatArticle() },
            { viewModel.refreshWechatArticles() },
            null,
            viewModel::modifyArticleCollectState,
            false
        )
        viewModel.getPublicAccounts()
    }

}