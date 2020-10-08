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
import com.lei.wanandroid.databinding.FragmentNavigationBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.NavigationAdapter
import com.lei.wanandroid.ui.helper.ContainerView
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.SystemViewModel

class NavigationFragment : BaseLazyFragment<SystemViewModel, FragmentNavigationBinding>() {
    private lateinit var adapter: NavigationAdapter

    override fun initView(savedInstanceState: Bundle?) {
        initRefreshLayout(getBinding().refreshLayout)
        initContainerView(getBinding().container)
        initRecyclerView(getBinding().rvNavigation)
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshNavigationList() }
    }

    private fun initContainerView(containerView: ContainerView) {
        containerView.errorView?.findViewById<TextView>(R.id.tvRefresh)?.setOnClickListener {
            viewModel.refreshNavigationList()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = NavigationAdapter().apply { adapter = this }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_navigation
    }

    override fun provideViewModel(): SystemViewModel {
        return ViewModelProviders.of(this).get(SystemViewModel::class.java)
    }

    override fun initLazy() {
        viewModel.refreshNavigationStateLiveData.observe(
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
        viewModel.getNavigationLiveData().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

}