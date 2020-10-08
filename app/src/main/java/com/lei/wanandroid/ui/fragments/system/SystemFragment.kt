package com.lei.wanandroid.ui.fragments.system

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.databinding.FragmentSystemBinding
import com.lei.wanandroid.viewmodel.SystemViewModel

class SystemFragment : BaseFragment<SystemViewModel, FragmentSystemBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        initTabAndViewPager2(getBinding().tab, getBinding().viewPager2)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_system
    }

    override fun provideViewModel(): SystemViewModel {
        return ViewModelProviders.of(this).get(SystemViewModel::class.java)
    }

    private fun initTabAndViewPager2(tab: TabLayout, viewPager2: ViewPager2) {
        val titles = listOf(getString(R.string.system), getString(R.string.navigation))
        viewPager2.adapter = SystemViewPager2Adapter()
        viewPager2.offscreenPageLimit = titles.size
        TabLayoutMediator(tab, viewPager2) { t, p ->
            t.text = titles[p]
        }.attach()
    }

    private inner class SystemViewPager2Adapter : FragmentStateAdapter(this) {
        private val tabFragmentsCreators: List<() -> Fragment> =
            listOf({ TreeSystemFragment() }, { NavigationFragment() })

        override fun getItemCount(): Int {
            return tabFragmentsCreators.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabFragmentsCreators[position].invoke()
        }
    }
}