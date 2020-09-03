package com.lei.wanandroid.ui.fragments.project

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.data.bean.ProjectClassification
import com.lei.wanandroid.databinding.FragmentProjectBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.util.formatHtmlString
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.ProjectViewModel
import kotlinx.android.synthetic.main.layout_error.view.*

class ProjectFragment : BaseFragment<ProjectViewModel, FragmentProjectBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        getBinding().layoutError.tvRefresh.setOnClickListener { viewModel.refreshProjectClassification() }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_project
    }

    override fun provideViewModel(): ProjectViewModel {
        return ViewModelProviders.of(this).get(ProjectViewModel::class.java)
    }

    override fun initData() {
        super.initData()
        viewModel.getProjectClassificationLiveData().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) viewModel.refreshProjectClassification() else initTabAndViewPager2(it)
        })
        viewModel.refreshProjectStateLiveData.observe(
            viewLifecycleOwner,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    getBinding().progressBar.hide()
                }

                override fun onLoading() {
                    getBinding().progressBar.show()
                    getBinding().layoutError.visibility = View.GONE
                }

                override fun onFailure(message: String) {
                    getBinding().progressBar.hide()
                    showShortToast(message)
                    getBinding().layoutError.visibility = View.VISIBLE
                }
            })
        )
    }

    private fun initTabAndViewPager2(projects: List<ProjectClassification>) {
        getBinding().viewPager2.adapter = ProjectViewPager2Adapter(projects)
        getBinding().viewPager2.offscreenPageLimit = projects.size
        TabLayoutMediator(getBinding().tab, getBinding().viewPager2) { t, p ->
            t.text = formatHtmlString(projects[p].name)
        }.attach()
    }

    private inner class ProjectViewPager2Adapter(private val projects: List<ProjectClassification>) :
        FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return projects.size
        }

        override fun createFragment(position: Int): Fragment {
            return ProjectListFragment.newInstance(projects[position])
        }
    }
}