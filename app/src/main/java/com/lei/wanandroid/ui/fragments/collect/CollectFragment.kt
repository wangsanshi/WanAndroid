package com.lei.wanandroid.ui.fragments.collect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.databinding.FragmentCollectBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.activitys.CollectWebsiteActivity
import com.lei.wanandroid.ui.activitys.LoginActivity
import com.lei.wanandroid.ui.activitys.WebViewActivity
import com.lei.wanandroid.ui.adapter.page.CollectArticlePagedListAdapter
import com.lei.wanandroid.ui.adapter.CollectWebsiteAdapter
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.collectArticleToRead
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.util.*
import com.lei.wanandroid.viewmodel.MyViewModel

class CollectFragment : BaseFragment<MyViewModel, FragmentCollectBinding>() {
    private var type: Byte = 0

    private var collectWebsiteAdapter: CollectWebsiteAdapter? = null
    private var collectArticleAdapter: CollectArticlePagedListAdapter? = null
    private var loadMoreArticleAdapter: LoadMoreAdapter? = null

    companion object {
        private const val TYPE_COLLECT = "type_collect"
        const val COLLECT_TYPE_ARTICLE: Byte = 0
        const val COLLECT_TYPE_WEBSITE: Byte = 1

        fun newInstance(type: Byte): CollectFragment {
            return CollectFragment().apply {
                arguments = Bundle().apply { putByte(TYPE_COLLECT, type) }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        type = arguments?.getByte(TYPE_COLLECT, 0) ?: 0
        initRefreshLayout(getBinding().refreshLayout)
        initContainer(getBinding().container)
        initRecyclerView(getBinding().rvList)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_collect
    }

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { refresh() }
    }

    private fun initContainer(container: ContainerView) {
        container.errorView?.findViewById<TextView>(R.id.tvRefresh)?.setOnClickListener {
            refresh()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(getTransparentItemDecoration())
        recyclerView.adapter = getAdapter()
    }

    private fun getAdapter(): RecyclerView.Adapter<*>? {
        return if (type == COLLECT_TYPE_ARTICLE) {
            collectArticleAdapter = getCollectArticleAdapter()
            loadMoreArticleAdapter = LoadMoreAdapter { viewModel.retryCollectArticle() }
            MergeAdapter(collectArticleAdapter, loadMoreArticleAdapter)
        } else {
            collectWebsiteAdapter = getCollectWebsiteAdapter()
            collectWebsiteAdapter
        }
    }

    private fun getCollectWebsiteAdapter(): CollectWebsiteAdapter {
        val adapter = CollectWebsiteAdapter()
        adapter.onItemClickListener = { _, _, position ->
            openWebPage(adapter.getItem(position).link)
        }
        adapter.addChildClickViewIds(R.id.ivCollect, R.id.ivEdit)
        adapter.onItemChildClickListener = { _, view, position ->
            if (view.id == R.id.ivEdit) {
                CollectWebsiteActivity.toThis(adapter.getItem(position))
            } else if (view.id == R.id.ivCollect && check()) viewModel.deleteCollectWebsite(
                adapter.getItem(position)
            )
        }
        return adapter
    }

    private fun getCollectArticleAdapter(): CollectArticlePagedListAdapter {
        val adapter =
            CollectArticlePagedListAdapter()
        adapter.addChildClickViewIds(R.id.ivCollect)
        adapter.onItemClickListener = { _, _, position ->
            WebViewActivity.toThis(collectArticleToRead(adapter.getItem(position)!!))
        }
        adapter.onItemChildClickListener = { _, view, position ->
            if (view.id == R.id.ivCollect) adapter.getItem(position)
                ?.let { if (check()) viewModel.cancelCollectArticleFromMy(it) }
        }
        return adapter
    }

    private fun openWebPage(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(requireActivity().application.packageManager) != null) {
            ActivityUtils.startActivity(intent)
        } else {
            showGravityShortToast("无法打开该链接", Gravity.CENTER)
        }
    }

    private fun check(): Boolean {
        if (viewModel.isLogin) {
            if (NetworkUtils.isConnected()) {
                return true
            } else {
                ToastUtil.showShortToast("网络不可用，请检查网络连接")
            }
        } else {
            ActivityUtils.startActivity(LoginActivity::class.java)
        }
        return false
    }

    private fun refresh() {
        if (type == COLLECT_TYPE_ARTICLE) {
            viewModel.refreshCollectArticle()
        } else {
            viewModel.refreshCollectWebsites()
        }
    }

    override fun initData() {
        super.initData()
        if (type == COLLECT_TYPE_ARTICLE) initCollectArticle() else initCollectWebSite()
    }

    private fun initCollectArticle() {
        viewModel.collectArticlePagedList.observe(
            viewLifecycleOwner,
            Observer { collectArticleAdapter?.submitList(it) })
        viewModel.collectArticleLoadMoreState.observe(viewLifecycleOwner, Observer {
            loadMoreArticleAdapter?.setState(it)
        })
        viewModel.collectArticleRefreshState.observe(viewLifecycleOwner, Observer {
            getBinding().refreshLayout.isRefreshing = it == State.Loading
            when (it) {
                State.Loading -> getBinding().container.showContent()
                State.Failure -> getBinding().container.showError()
                State.SuccessNoData -> getBinding().container.showEmpty()
                else -> {
                }
            }
        })
        viewModel.cancelCollectArticleState.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    requireActivity().hideLoading(getBinding().refreshLayout)
                }

                override fun onLoading() {
                    requireActivity().showLoading(getBinding().refreshLayout)
                }

                override fun onFailure(message: String) {
                    requireActivity().hideLoading(getBinding().refreshLayout)
                    showShortToast(message)
                }
            })
        )
        viewModel.clearCollectArticals()
        viewModel.getCollectArticle()
    }

    private fun initCollectWebSite() {
        viewModel.refreshWebsitesStateLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<Boolean> {
                override fun onSuccess(value: Boolean) {
                    getBinding().refreshLayout.isRefreshing = false
                    if (!value) getBinding().container.showEmpty()
                }

                override fun onLoading() {
                    getBinding().refreshLayout.isRefreshing = true
                    getBinding().container.showContent()
                }

                override fun onFailure(message: String) {
                    getBinding().refreshLayout.isRefreshing = false
                    showLongToast(message)
                    collectWebsiteAdapter?.let {
                        if (it.isEmpty()) getBinding().container.showError()
                    }
                }
            })
        )
        viewModel.collectWebsitesLiveData.observe(viewLifecycleOwner, Observer {
            collectWebsiteAdapter?.submitList(it, Runnable {
                if (it.isEmpty()) getBinding().container.showEmpty() else getBinding().container.showContent()
            })
        })
        viewModel.deleteCollectWebsiteLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    requireActivity().hideLoading(getBinding().refreshLayout)
                }

                override fun onLoading() {
                    requireActivity().showLoading(getBinding().refreshLayout)
                }

                override fun onFailure(message: String) {
                    requireActivity().hideLoading(getBinding().refreshLayout)
                    showShortToast(message)
                }
            })
        )
        viewModel.clearCollectWebsites()
        viewModel.refreshCollectWebsites()
    }
}