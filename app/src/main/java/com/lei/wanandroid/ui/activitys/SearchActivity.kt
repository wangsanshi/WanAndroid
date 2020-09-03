package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivitySearchBinding
import com.lei.wanandroid.ui.fragments.search.SearchAuthorArticleFragment
import com.lei.wanandroid.ui.fragments.search.SearchKeyWordFragment
import com.lei.wanandroid.ui.fragments.search.SearchPublicAccountFragment
import com.lei.wanandroid.util.showGravityShortToast
import com.lei.wanandroid.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
        initEditText(mBinding.etSearch)
        initTabAndViewPager2(mBinding.tabSearch, mBinding.viewPager)
    }

    private fun initTabAndViewPager2(tabLayout: TabLayout, viewPager2: ViewPager2) {
        val titles =
            listOf(
                getString(R.string.search_tab_key_word),
                getString(R.string.search_tab_author),
                getString(R.string.search_tab_wechat_public_account)
            )
        viewPager2.adapter = SearchViewPager2Adapter()
        viewPager2.offscreenPageLimit = titles.size
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.let {
                    mBinding.etSearch.hint = when (it.position) {
                        0 -> getString(R.string.search_article_hint)
                        1 -> getString(R.string.search_author_hint)
                        else -> getString(R.string.search_wechat_public_account_hint)
                    }
                }
            }
        })
        TabLayoutMediator(tabLayout, viewPager2) { t, p ->
            t.text = titles[p]
        }.attach()
    }

    private fun initEditText(etSearch: AppCompatEditText) {
        etSearch.requestFocus()
        etSearch.addTextChangedListener(afterTextChanged = {
            val selectedHotWord = viewModel.selectedHotWord.value
            if (selectedHotWord != null) {
                if (it.isNullOrEmpty() || it.toString() != selectedHotWord.name) {
                    viewModel.selectedHotWord.value = null
                }
            }
        })
        etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val selectedTabPosition = mBinding.tabSearch.selectedTabPosition
                    val searchText = etSearch.text

                    //对公众号进行搜索时可以不输入关键字
                    if (selectedTabPosition == 2) {
                        return if (viewModel.selectedPublicAccountLiveData.value == null) {
                            showGravityShortToast("抱歉,未获取到公众号信息", Gravity.CENTER)
                            true
                        } else {
                            viewModel.searchByWechatPublicAccount(
                                viewModel.selectedPublicAccountLiveData.value!!,
                                searchText.toString().trim()
                            )
                            false
                        }
                    }

                    if (searchText != null && searchText.isNotBlank()) {
                        when (selectedTabPosition) {
                            0 -> viewModel.searchByKeyWord(searchText.toString().trim())
                            1 -> viewModel.searchByAuthor(searchText.toString().trim())
                        }
                        return false
                    } else {
                        when (selectedTabPosition) {
                            0 -> showGravityShortToast("请输入合法的关键字", Gravity.CENTER)
                            1 -> showGravityShortToast("请输入合法的作者昵称", Gravity.CENTER)
                        }
                    }
                }

                return true
            }
        })
        //选择热词
        viewModel.selectedHotWord.observe(this, Observer {
            if (it != null) {
                mBinding.etSearch.setText(it.name)
                mBinding.etSearch.setSelection(it.name.length)
            }
        })
    }

    override fun initData() {

    }

    override fun provideViewModel(): SearchViewModel {
        return ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    private inner class SearchViewPager2Adapter : FragmentStateAdapter(this) {
        private val tabFragmentsCreators: List<() -> Fragment> = listOf(
            { SearchKeyWordFragment() },
            { SearchAuthorArticleFragment() },
            { SearchPublicAccountFragment() }
        )

        override fun getItemCount(): Int {
            return tabFragmentsCreators.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabFragmentsCreators[position].invoke()
        }

    }
}