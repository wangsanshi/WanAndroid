package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityMainBinding
import com.lei.wanandroid.ui.fragments.home.HomeFragment
import com.lei.wanandroid.ui.fragments.my.MyFragment
import com.lei.wanandroid.ui.fragments.project.ProjectFragment
import com.lei.wanandroid.ui.fragments.todo.TodoFragment
import com.lei.wanandroid.ui.helper.switchFragment
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.MainViewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    private var currentShowFragmentTag: String? = null
    private var lastTimeMillis = 0L

    override fun getLayoutId() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            currentShowFragmentTag = switchFragment(
                HomeFragment::class.java,
                supportFragmentManager,
                currentShowFragmentTag,
                R.id.fragment_container
            )
            initBottomNavigationBar()
            initFloatActionButton(mBinding.fab)
        }
    }

    override fun initData() {

    }

    override fun provideViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun initFloatActionButton(fab: FloatingActionButton) {
        fab.setOnClickListener {
            if (mBinding.bottomNavigationView.selectedItemId == R.id.bottom_tab_todo) viewModel.userContext.addTodo() else if (mBinding.bottomNavigationView.selectedItemId == R.id.bottom_tab_home)
                viewModel.userContext.shareArticle()

        }
    }

    private fun initBottomNavigationBar() {
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            changeFabState(it.itemId, mBinding.fab)
            when (it.itemId) {
                R.id.bottom_tab_todo -> {
                    currentShowFragmentTag = switchFragment(
                        TodoFragment::class.java,
                        supportFragmentManager,
                        currentShowFragmentTag,
                        R.id.fragment_container
                    )
                }
                R.id.bottom_tab_home -> {
                    currentShowFragmentTag = switchFragment(
                        HomeFragment::class.java,
                        supportFragmentManager,
                        currentShowFragmentTag,
                        R.id.fragment_container
                    )
                }
                R.id.bottom_tab_my -> {
                    currentShowFragmentTag = switchFragment(
                        MyFragment::class.java,
                        supportFragmentManager,
                        currentShowFragmentTag,
                        R.id.fragment_container
                    )
                }
                R.id.bottom_tab_project -> {
                    currentShowFragmentTag = switchFragment(
                        ProjectFragment::class.java,
                        supportFragmentManager,
                        currentShowFragmentTag,
                        R.id.fragment_container
                    )
                }
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tag", currentShowFragmentTag)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        currentShowFragmentTag = savedInstanceState?.getString("tag")
        changeFabState(mBinding.bottomNavigationView.selectedItemId, mBinding.fab)
        initFloatActionButton(mBinding.fab)
        initBottomNavigationBar()
    }

    override fun onBackPressed() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastTimeMillis > 2 * 1000) {
            showShortToast("再点一次退出")
            lastTimeMillis = currentTimeMillis
            if (mBinding.bottomNavigationView.selectedItemId != R.id.bottom_tab_home) {
                mBinding.bottomNavigationView.selectedItemId = R.id.bottom_tab_home
            }
        } else {
            finish()
        }
    }

    private fun changeFabState(@IdRes id: Int, fab: FloatingActionButton) {
        when (id) {
            R.id.bottom_tab_home, R.id.bottom_tab_project -> {
                fab.setImageResource(R.drawable.ic_fab_send_white)
                fab.show()
            }
            R.id.bottom_tab_todo -> {
                fab.setImageResource(R.drawable.ic_fab_add_white)
                fab.show()
            }
            else -> fab.hide()
        }
    }

}
