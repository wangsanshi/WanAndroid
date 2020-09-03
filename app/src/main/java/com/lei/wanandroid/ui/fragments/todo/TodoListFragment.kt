package com.lei.wanandroid.ui.fragments.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.databinding.FragmentTodoListBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.activitys.TodoDetailActivity
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.adapter.TodoPagedListAdapter
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.viewmodel.TodoViewModel

class TodoListFragment : BaseFragment<TodoViewModel, FragmentTodoListBinding>() {

    companion object {
        const val TODO_STATUS_KEY = "todo_status_key"
    }

    private lateinit var logoutView: View
    private lateinit var loadErrorView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoutView = layoutInflater.inflate(R.layout.layout_todo_logout, container, false)
        logoutView.findViewById<TextView>(R.id.tvLoginOrRegister)
            .setOnClickListener { viewModel.toLoginActivity() }
        loadErrorView = layoutInflater.inflate(R.layout.layout_error, container, false)
        loadErrorView.findViewById<TextView>(R.id.tvRefresh)
            .setOnClickListener { viewModel.refreshTodos() }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setSwipeRefreshLayoutStyle(getBinding().refreshLayout)
        viewModel.loginUser.observe(viewLifecycleOwner, StateOberver(object : IStateCallback<User> {
            override fun onSuccess(value: User) {
                getBinding().container.errorView = loadErrorView
                getBinding().container.showContent()
                initContent()
            }

            override fun onLoading() {
            }

            override fun onFailure(message: String) {
                getBinding().container.errorView = logoutView
                getBinding().container.showError()
            }
        }))
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_todo_list
    }

    override fun provideViewModel(): TodoViewModel {
        return ViewModelProviders.of(this).get(TodoViewModel::class.java)
    }

    private fun initContent() {
        val pagedAdapter = TodoPagedListAdapter()
        pagedAdapter.onItemClickListener = { _, _, position ->
            pagedAdapter.getItem(position)?.let {
                viewModel.currentTodoLiveData.value = it
                ActivityUtils.startActivity(TodoDetailActivity::class.java)
            }
        }
        val loadMoreAdapter = LoadMoreAdapter { viewModel.retryTodos() }
        val adapter = MergeAdapter(pagedAdapter, loadMoreAdapter)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        getBinding().rvTodoList.layoutManager = layoutManager
        getBinding().rvTodoList.adapter = adapter
        getBinding().refreshLayout.setOnRefreshListener { viewModel.refreshTodos() }

        viewModel.todoPagedList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                getBinding().container.showContent()
            }
            pagedAdapter.submitList(it)
        })
        viewModel.todoRefreshState.observe(
            viewLifecycleOwner,
            Observer {
                getBinding().refreshLayout.isRefreshing = it == State.Loading
                when (it) {
                    State.Loading -> getBinding().container.showContent()
                    State.Failure -> if (pagedAdapter.isEmpty()) {
                        getBinding().container.showError()
                    }
                    State.SuccessNoData -> getBinding().container.showEmpty()
                    else -> {
                    }
                }
            })
        viewModel.todoLoadMoreState.observe(
            viewLifecycleOwner,
            Observer { loadMoreAdapter.setState(it) })
        viewModel.initTodos(arguments?.getInt(TODO_STATUS_KEY) ?: TODO_STATUS_UNFINISH)
        viewModel.refreshTodos()
    }

}