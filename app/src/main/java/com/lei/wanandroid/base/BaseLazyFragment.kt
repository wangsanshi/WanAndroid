package com.lei.wanandroid.base

import androidx.databinding.ViewDataBinding
import com.lei.wanandroid.viewmodel.BaseViewModel

/**
 * for ViewPager2
 */
abstract class BaseLazyFragment<VM : BaseViewModel, VDB : ViewDataBinding> :
    BaseFragment<VM, VDB>() {

    private var isInitData = false

    override fun onResume() {
        super.onResume()
        if (!isInitData) {
            initLazyData()
            isInitData = true
        }
    }

    abstract fun initLazyData()

}