package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.databinding.FragmentPublicAccountBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.ui.adapter.WechatPublicAccountAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.HomeViewModel

/**
 * 公众号
 */
class PublicAccountFragment : BaseLazyFragment<HomeViewModel, FragmentPublicAccountBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_public_account
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun initLazy() {
        initAccounts(getBinding().refreshLayout, getBinding().rvPublicAccount)

        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.wechatArticleListing,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = false,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = null,
            needRefresh = false
        )

        viewModel.getPublicAccounts()
    }

    private fun initAccounts(refreshLayout: SwipeRefreshLayout, rvPublicAccount: RecyclerView) {
        val accountAdapter = getAccountAdapter()
        rvPublicAccount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvPublicAccount.adapter = accountAdapter

        initRefreshLayout(getBinding().refreshLayout, accountAdapter)
        initContainerView(getBinding().container, accountAdapter)

        viewModel.publicAccountsLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<List<PublicAccount>> {
                override fun onSuccess(value: List<PublicAccount>) {
                    refreshLayout.isRefreshing = false
                    accountAdapter.submitListWithDedaultSelected(value)
                }

                override fun onLoading() {
                    refreshLayout.isRefreshing = true
                    getBinding().container.showContent()
                }

                override fun onFailure(message: String) {
                    refreshLayout.isRefreshing = false
                    getBinding().container.showError()
                }
            })
        )
    }

    private fun initRefreshLayout(
        refreshLayout: SwipeRefreshLayout,
        adapter: WechatPublicAccountAdapter
    ) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener {
            refresh(
                adapter,
                viewModel.wechatArticleListing
            )
        }
    }

    private fun initContainerView(
        containerView: ContainerView,
        adapter: WechatPublicAccountAdapter
    ) {
        containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)
            ?.setOnClickListener { refresh(adapter, viewModel.wechatArticleListing) }
    }

    private fun refresh(
        adapter: WechatPublicAccountAdapter,
        listingLiveData: LiveData<Listing<Article>>
    ) {
        if (adapter.isEmpty()) viewModel.getPublicAccounts() else listingLiveData.value?.refresh?.invoke()
    }

    private fun getAccountAdapter(): WechatPublicAccountAdapter {
        return WechatPublicAccountAdapter().apply {
            onSelectChangeListener = {
                viewModel.initWechatArticlesByID(it.id)
                viewModel.wechatArticleListing.value?.refresh?.invoke()
            }
        }
    }
}