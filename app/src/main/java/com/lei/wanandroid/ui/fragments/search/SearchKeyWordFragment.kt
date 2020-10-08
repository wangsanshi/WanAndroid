package com.lei.wanandroid.ui.fragments.search

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.HotWord
import com.lei.wanandroid.databinding.FragmentSearchKeyWordBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.HotWordAdapter
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.initCommonArticlePage
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.SearchViewModel

class SearchKeyWordFragment : BaseLazyFragment<SearchViewModel, FragmentSearchKeyWordBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout, false)
        initHotWords(getBinding().rvHotWords)
        initCommonArticlePage(
            fragment = this,
            refreshLayout = getBinding().refreshLayout,
            recyclerView = getBinding().rvArticle,
            containerView = getBinding().container,
            listingLiveData = viewModel.keywordListingLiveData,
            modifyArticleCollectState = viewModel::modifyArticleCollectState,
            needNotifyItemChange = true,
            itemDecoration = getTransparentItemDecoration(),
            showTopArticle = false,
            clear = viewModel.clearNotify,
            needRefresh = true
        )
    }

    private fun initHotWords(recyclerView: RecyclerView) {
        val layoutManager = FlexboxLayoutManager()
        layoutManager.flexWrap = FlexWrap.WRAP//设置换行
        layoutManager.flexDirection = FlexDirection.ROW//设置主轴排列方式
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerView.layoutManager = layoutManager
        val adapter = HotWordAdapter()
        adapter.onSelectChangeListener = { viewModel.selectedHotWord.value = it }
        recyclerView.adapter = adapter
        viewModel.hotwordsLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<List<HotWord>> {
                override fun onSuccess(value: List<HotWord>) {
                    adapter.submitListWithNoSelected(value)
                }

                override fun onLoading() {

                }

                override fun onFailure(message: String) {
                    showShortToast(message)
                }
            })
        )
        viewModel.selectedHotWord.observe(viewLifecycleOwner, Observer {
            if (it == null) adapter.clearSelectedState()
        })
    }
    
    override fun getLayoutId(): Int {
        return R.layout.fragment_search_key_word
    }

    override fun provideViewModel(): SearchViewModel {
        return ViewModelProviders.of(requireActivity()).get(SearchViewModel::class.java)
    }

    override fun initLazy() {
        viewModel.getHotWords()
    }

}