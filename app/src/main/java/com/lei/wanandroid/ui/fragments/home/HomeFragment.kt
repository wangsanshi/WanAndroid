package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.databinding.FragmentHomeBinding
import com.lei.wanandroid.ui.activitys.SearchActivity
import com.lei.wanandroid.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(getBinding().toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        getBinding().model = viewModel
        getBinding().tvSearchHome.setOnClickListener { ActivityUtils.startActivity(SearchActivity::class.java) }
        initTabAndViewPager2(getBinding().tab, getBinding().viewPager2)
    }

    private fun initTabAndViewPager2(tabLayout: TabLayout, viewPager2: ViewPager2) {
        val titles = listOf(
            getString(R.string.first_page),
            getString(R.string.square),
            getString(R.string.question),
            getString(R.string.official_account)
        )
        viewPager2.adapter = HomeViewPager2Adapter()
        viewPager2.offscreenPageLimit = titles.size
        TabLayoutMediator(tabLayout, viewPager2) { t, p ->
            t.text = titles[p]
        }.attach()
    }

    private inner class HomeViewPager2Adapter : FragmentStateAdapter(this) {

        private val tabFragmentsCreators: List<() -> Fragment> = listOf(
            { FirstPageFragment() },
            { SquareFragment() },
            { QuestionFragment() },
            { PublicAccountFragment() }
        )

        override fun getItemCount(): Int {
            return tabFragmentsCreators.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabFragmentsCreators[position].invoke()
        }
    }

}