package com.lei.wanandroid.ui.helper

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.google.android.flexbox.*
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.CollectArticle
import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.data.source.URL_LABELS
import com.lei.wanandroid.data.source.UserContext
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.ui.activitys.LoginActivity
import com.lei.wanandroid.ui.activitys.UserInfoActivity
import com.lei.wanandroid.ui.activitys.WebViewActivity
import com.lei.wanandroid.ui.adapter.*
import com.lei.wanandroid.util.ToastUtil

fun setSwipeRefreshLayoutStyle(refreshLayout: SwipeRefreshLayout, enableRefresh: Boolean = true) {
    refreshLayout.setColorSchemeResources(R.color.colorAccent)
    refreshLayout.isEnabled = enableRefresh
}

fun initCommonArticlePagedList(
    fragment: Fragment,
    refreshLayout: SwipeRefreshLayout,
    recyclerView: RecyclerView,
    containerView: ContainerView,
    refreshState: LiveData<State>,
    loadMoreState: LiveData<State>,
    pagedList: LiveData<PagedList<Article>>,
    retry: () -> Unit,
    refresh: () -> Unit,
    clear: LiveData<Unit>?,
    modifyArticleCollectState: (Int, Boolean) -> Unit,
    needNotifyItemChange: Boolean
) {
    val articleAdapter = CommonArticlePagedListAdapter()
    articleAdapter.onItemClickListener = { _, _, position ->
        WebViewActivity.toThis(articleToRead(articleAdapter.getItem(position)!!))
    }
    articleAdapter.onItemChildClickListener = { _, view, position ->
        val article = articleAdapter.getItem(position)!!
        if (view.id == R.id.ivCollect) {
            modifyArticleCollectStateWithCheckLogin(
                articleAdapter,
                article,
                position,
                modifyArticleCollectState,
                needNotifyItemChange
            )
        } else if (view.id == R.id.llContainer) {
            if (article.author.isNotEmpty()) ToastUtil.showShortToast(
                Utils.getApp().getString(R.string.deny_see_share_article)
            ) else UserInfoActivity.toThis(article.userId, article.shareUser)
        }
    }

    val loadMoreAdapter = LoadMoreAdapter(retry)
    val adapter = MergeAdapter(articleAdapter, loadMoreAdapter)
    recyclerView.layoutManager = DealExceptionLinearLayoutManager(fragment.requireContext())
    recyclerView.addItemDecoration(getTransparentItemDecoration())
    recyclerView.adapter = adapter

    refreshLayout.setOnRefreshListener { refresh.invoke() }
    containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)
        ?.setOnClickListener { refresh.invoke() }

    loadMoreState.observe(fragment.viewLifecycleOwner, Observer { loadMoreAdapter.setState(it) })
    refreshState.observe(fragment.viewLifecycleOwner, Observer {
        refreshLayout.isRefreshing = it == State.Loading
        when (it) {
            State.Loading -> containerView.showContent()
            State.Failure -> if (articleAdapter.isEmpty()) containerView.showError()
            State.SuccessNoData -> containerView.showEmpty()
            else -> {
            }
        }
    })

    pagedList.observe(fragment.viewLifecycleOwner, Observer {
        articleAdapter.submitList(it)
    })
    clear?.observe(fragment.viewLifecycleOwner, Observer { articleAdapter.submitList(null) })
}

fun initFirstArticlePagedList(
    fragment: Fragment,
    refreshLayout: SwipeRefreshLayout,
    recyclerView: RecyclerView,
    containerView: ContainerView,
    refreshState: LiveData<State>,
    loadMoreState: LiveData<State>,
    pagedList: LiveData<PagedList<Article>>,
    retry: () -> Unit,
    refresh: () -> Unit,
    isShowTopArticles: MutableLiveData<Boolean>,
    topArticles: LiveData<List<Article>>,
    modifyArticleCollectState: (Int, Boolean) -> Unit,
    needNotifyItemChange: Boolean
) {
    val topArticleHeaderAdapter = TopArticleHeaderAdapter { isShowTopArticles.value = false }
    val topArticlesAdapter = TopArticleAdapter {
        WebViewActivity.toThis(articleToRead(it))
    }
    val articleAdapter = CommonArticlePagedListAdapter()
    articleAdapter.onItemClickListener = { _, _, position ->
        WebViewActivity.toThis(articleToRead(articleAdapter.getItem(position)!!))
    }
    articleAdapter.onItemChildClickListener = { _, view, position ->
        val article = articleAdapter.getItem(position)!!
        if (view.id == R.id.ivCollect) {
            modifyArticleCollectStateWithCheckLogin(
                articleAdapter,
                article,
                position,
                modifyArticleCollectState,
                needNotifyItemChange
            )
        } else if (view.id == R.id.llContainer) {
            if (article.author.isNotEmpty()) ToastUtil.showShortToast(
                Utils.getApp().getString(R.string.deny_see_share_article)
            ) else UserInfoActivity.toThis(article.userId, article.shareUser)
        }
    }
    val loadMoreAdapter = LoadMoreAdapter(retry)
    val adapter = MergeAdapter(
        MergeAdapter.Config.Builder().setIsolateViewTypes(false).build(),
        topArticleHeaderAdapter,
        topArticlesAdapter,
        articleAdapter,
        loadMoreAdapter
    )

    recyclerView.layoutManager = DealExceptionLinearLayoutManager(fragment.requireContext())
    recyclerView.addItemDecoration(FirstPageRecyclerViewDecoration())
    recyclerView.adapter = adapter

    refreshLayout.setOnRefreshListener { refresh.invoke() }
    containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)
        ?.setOnClickListener { refresh.invoke() }

    refreshState.observe(fragment.viewLifecycleOwner, Observer {
        refreshLayout.isRefreshing = it == State.Loading
        when (it) {
            State.Loading -> containerView.showContent()
            State.Failure -> if (articleAdapter.isEmpty() && topArticlesAdapter.isEmpty()) {
                containerView.showError()
            }
            State.SuccessNoData -> if (topArticlesAdapter.isEmpty()) containerView.showEmpty()
            else -> {
            }
        }
    })
    topArticles.observe(fragment.viewLifecycleOwner, Observer {
        topArticleHeaderAdapter.hasData = it.isNotEmpty()
        topArticlesAdapter.submitList(it)
    })
    isShowTopArticles.observe(fragment.viewLifecycleOwner, Observer {
        if (!it) {
            adapter.removeAdapter(topArticleHeaderAdapter)
            adapter.removeAdapter(topArticlesAdapter)
        }
    })
    loadMoreState.observe(fragment.viewLifecycleOwner, Observer { loadMoreAdapter.setState(it) })
    pagedList.observe(fragment.viewLifecycleOwner, Observer {
        articleAdapter.submitList(it)
    })
}

fun modifyArticleCollectStateWithCheckLogin(
    adapter: PagedListAdapter<*, *>,
    article: Article,
    position: Int,
    modifyArticleCollectState: (Int, Boolean) -> Unit,
    needNotifyItemChange: Boolean
) {
    if (UserContext.isLogin()) {
        if (NetworkUtils.isConnected()) {
            val destCollectState = !article.collect
            if (needNotifyItemChange) {
                article.collect = destCollectState
                adapter.notifyItemChanged(position)
            }
            modifyArticleCollectState.invoke(article.id, destCollectState)
        } else {
            ToastUtil.showShortToast("网络不可用，请检查网络连接")
        }
    } else {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }
}

fun setViewsEnabledState(
    root: ViewGroup,
    isEnabled: Boolean,
    excludeViewsClass: List<Class<*>> = emptyList()
) {
    root.forEach {
        if (excludeViewsClass.isEmpty() || !excludeViewsClass.contains(it.javaClass)) {
            it.isEnabled = isEnabled
            if (it is ViewGroup) {
                setViewsEnabledState(it, isEnabled, excludeViewsClass)
            }
        }
    }
}

fun articleToRead(article: Article) = ReadArticle(
    article.id,
    System.currentTimeMillis(),
    article.title,
    article.link,
    article.desc,
    article.envelopePic,
    article.author
)

fun collectArticleToRead(article: CollectArticle) = ReadArticle(
    article.id,
    System.currentTimeMillis(),
    article.title,
    article.link,
    article.desc,
    article.envelopePic,
    article.author
)

fun initUrlLabelRecyclerView(recyclerView: RecyclerView, onItemClick: (String) -> Unit) {
    val layoutManager = FlexboxLayoutManager()
    layoutManager.flexWrap = FlexWrap.WRAP//设置换行
    layoutManager.flexDirection = FlexDirection.ROW//设置主轴排列方式
    layoutManager.alignItems = AlignItems.FLEX_START
    layoutManager.justifyContent = JustifyContent.SPACE_AROUND
    val adapter = UrlLabelAdapter()
    adapter.onItemClickListener = { _, _, position ->
        onItemClick.invoke(adapter.getItem(position))
    }
    recyclerView.adapter = adapter
    recyclerView.layoutManager = layoutManager
    adapter.submitList(URL_LABELS)
}



