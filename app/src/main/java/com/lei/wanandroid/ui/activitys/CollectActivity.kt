package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityMyCollectBinding
import com.lei.wanandroid.ui.fragments.collect.CollectFragment
import com.lei.wanandroid.viewmodel.MyViewModel

class CollectActivity : BaseActivity<MyViewModel, ActivityMyCollectBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_my_collect
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(mBinding.toolbar)
        initTabAndViewPager2(mBinding.tab, mBinding.viewPager2)
        initFab()
    }

    override fun initData() {
    }

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initTabAndViewPager2(tab: TabLayout, viewpager2: ViewPager2) {
        viewpager2.adapter = CollectViewPager2Adapter()
        viewpager2.offscreenPageLimit = 1
        TabLayoutMediator(tab, viewpager2) { t, p ->
            when (p) {
                0 -> t.text = "文章"
                1 -> t.text = "网站"
            }
        }.attach()
    }

    private fun initFab() {
        mBinding.fabArticle.setOnClickListener {
            mBinding.fabMenu.toggle()
            CollectOuterArticleActivity.toThis()
        }
        mBinding.fabWebsite.setOnClickListener {
            mBinding.fabMenu.toggle()
            CollectWebsiteActivity.toThis()
        }
    }

    private inner class CollectViewPager2Adapter : FragmentStateAdapter(this) {
        private val tabFragmentsCreators: List<() -> Fragment> =
            listOf(
                { CollectFragment.newInstance(CollectFragment.COLLECT_TYPE_ARTICLE) },
                { CollectFragment.newInstance(CollectFragment.COLLECT_TYPE_WEBSITE) })

        override fun getItemCount(): Int {
            return tabFragmentsCreators.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabFragmentsCreators[position].invoke()
        }
    }
}