package com.lei.wanandroid.ui.fragments.project

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.data.bean.ProjectClassification
import com.lei.wanandroid.databinding.FragmentProjectListBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.ui.activitys.WebViewActivity
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.adapter.ProjectArticlePagedListAdapter
import com.lei.wanandroid.ui.helper.articleToRead
import com.lei.wanandroid.ui.helper.getLineItemDecoration
import com.lei.wanandroid.ui.helper.modifyArticleCollectStateWithCheckLogin
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.ProjectViewModel

class ProjectListFragment : BaseLazyFragment<ProjectViewModel, FragmentProjectListBinding>() {
    private lateinit var projectClassification: ProjectClassification
    private lateinit var articleAdapter: ProjectArticlePagedListAdapter
    private lateinit var loadMoreAdapter: LoadMoreAdapter

    override fun initView(savedInstanceState: Bundle?) {
        initRefreshLayout(getBinding().refreshLayout)
        initRecyclerView(getBinding().rvList)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_project_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        projectClassification =
            arguments?.getParcelable(KEY_PROJECT)
                ?: throw IllegalArgumentException("Unkown Project Classification.")
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshProjectArticle() }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(getLineItemDecoration())
        recyclerView.adapter = getAdapter()
    }

    private fun getAdapter(): RecyclerView.Adapter<*> {
        articleAdapter = ProjectArticlePagedListAdapter(viewLifecycleOwner)
        articleAdapter.onItemClickListener = { _, _, p ->
            articleAdapter.getItem(p)?.let {
                WebViewActivity.toThis(articleToRead(it))
            }
        }
        articleAdapter.addChildClickViewIds(R.id.ivCollect)
        articleAdapter.onItemChildClickListener = { _, v, p ->
            if (v.id == R.id.ivCollect) articleAdapter.getItem(p)?.let {
                modifyArticleCollectStateWithCheckLogin(
                    articleAdapter,
                    it,
                    p,
                    viewModel::modifyArticleCollectState,
                    false
                )
            }
        }
        loadMoreAdapter = LoadMoreAdapter { viewModel.retryProjectArticle() }
        return MergeAdapter(articleAdapter, loadMoreAdapter)
    }

    companion object {
        private const val KEY_PROJECT = "project"

        fun newInstance(project: ProjectClassification): ProjectListFragment {
            return ProjectListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_PROJECT, project)
                }
            }
        }
    }

    override fun provideViewModel(): ProjectViewModel {
        return ViewModelProviders.of(this).get(ProjectViewModel::class.java)
    }

    override fun initLazyData() {
        viewModel.projectArticlePagedList.observe(viewLifecycleOwner, Observer {
            articleAdapter.submitList(it)
        })
        viewModel.projectArticleLoadMoreState.observe(viewLifecycleOwner, Observer {
            loadMoreAdapter.setState(it)
        })
        viewModel.projectArticleRefreshState.observe(this, Observer {
            getBinding().refreshLayout.isRefreshing = it == State.Loading
            when (it) {
                State.Loading -> getBinding().container.showContent()
                State.SuccessNoData -> getBinding().container.showEmpty()
                State.Failure -> if (articleAdapter.isEmpty()) getBinding().container.showError()
                else -> {
                }
            }
        })
        viewModel.getProjectArticleLisitng(projectClassification.id)
        viewModel.refreshProjectArticle()
    }
}