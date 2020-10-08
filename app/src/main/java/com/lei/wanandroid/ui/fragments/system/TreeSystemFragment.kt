package com.lei.wanandroid.ui.fragments.system

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseLazyFragment
import com.lei.wanandroid.databinding.FragmentTreeSystemBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.TreeAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.SystemViewModel

class TreeSystemFragment : BaseLazyFragment<SystemViewModel, FragmentTreeSystemBinding>() {
    private lateinit var adapter: TreeAdapter

    override fun initView(savedInstanceState: Bundle?) {
        initRefreshLayout(getBinding().refreshLayout)
        initContainerView(getBinding().container)
        initRecyclerView(getBinding().rvTreeSystem)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tree_system
    }

    override fun provideViewModel(): SystemViewModel {
        return ViewModelProviders.of(this).get(SystemViewModel::class.java)
    }

    override fun initLazy() {
        viewModel.getTreeLiveData().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.refreshTreeStateLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    getBinding().refreshLayout.isRefreshing = false
                }

                override fun onLoading() {
                    getBinding().refreshLayout.isRefreshing = true
                    getBinding().container.showContent()
                }

                override fun onFailure(message: String) {
                    getBinding().refreshLayout.isRefreshing = false
                    if (adapter.isEmpty()) getBinding().container.showError()
                }
            })
        )
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshTreeList() }
    }

    private fun initContainerView(containerView: ContainerView) {
        containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)?.setOnClickListener {
            viewModel.refreshTreeList()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TreeAdapter().apply { adapter = this }
    }
}