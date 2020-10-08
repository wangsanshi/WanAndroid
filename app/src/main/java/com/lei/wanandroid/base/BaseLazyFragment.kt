package com.lei.wanandroid.base

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.NetworkUtils
import com.lei.wanandroid.jetpack.bus.LiveDataBus
import com.lei.wanandroid.jetpack.lifecycle.NetworkManager
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
            initLazy()
            if (NetworkUtils.isConnected()) refreshData()
            LiveDataBus.with(NetworkManager.EVENT_TAG_NETWORK_CHANGE).observe(this,
                Observer {
                    Log.d(NetworkManager.EVENT_TAG_NETWORK_CHANGE, "receive event($it)")
                    it.let { if (it is Boolean && it) refreshData() }
                })
            isInitData = true
        }
    }

    abstract fun initLazy()

    open fun refreshData() {

    }
}