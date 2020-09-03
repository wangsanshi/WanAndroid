package com.lei.wanandroid.ui.fragments.todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.databinding.FragmentTodoBinding
import com.lei.wanandroid.viewmodel.TodoViewModel

class TodoFragment : BaseFragment<TodoViewModel, FragmentTodoBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        initTab()
    }

    private fun initTab() {
        getBinding().viewPager.adapter = TodoViewPager2Adapter()
        TabLayoutMediator(getBinding().tab, getBinding().viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.tab_todo_unfinish_selector)
                    tab.text = "未完成"
                }
                1 -> {
                    tab.setIcon(R.drawable.tab_todo_finish_selector)
                    tab.text = "已完成"
                }
            }
        }.attach()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_todo
    }

    override fun provideViewModel(): TodoViewModel {
        return ViewModelProviders.of(requireActivity()).get(TodoViewModel::class.java)
    }

    private inner class TodoViewPager2Adapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = TodoListFragment()
            fragment.arguments = Bundle().apply {
                putInt(
                    TodoListFragment.TODO_STATUS_KEY, if (position == 0) {
                        TODO_STATUS_UNFINISH
                    } else {
                        TODO_STATUS_FINISH
                    }
                )
            }
            return fragment
        }
    }
}