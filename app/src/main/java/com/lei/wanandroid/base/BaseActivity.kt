package com.lei.wanandroid.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lei.wanandroid.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel, VDB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mBinding: VDB
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewDataBinding()
        initViewModel()
        initView(savedInstanceState)
        initData()
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun initView(savedInstanceState: Bundle?)

    protected abstract fun initData()

    protected abstract fun provideViewModel(): VM

    private fun initViewDataBinding() {
        val clazz =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*>

        if (ViewDataBinding::class.java.isAssignableFrom(clazz)) {
            mBinding = DataBindingUtil.setContentView(this, getLayoutId())
            mBinding.lifecycleOwner = this
        } else {
            setContentView(getLayoutId())
        }
    }

    private fun initViewModel() {
        viewModel = provideViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

}