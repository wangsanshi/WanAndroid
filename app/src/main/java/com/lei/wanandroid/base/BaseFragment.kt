package com.lei.wanandroid.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.lei.wanandroid.util.getClassByParameterizedType
import com.lei.wanandroid.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel, VDB : ViewDataBinding> : Fragment() {
    private var mBinding: VDB? = null
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val clazz = getClassByParameterizedType(javaClass.genericSuperclass as ParameterizedType, 1)

        if (ViewDataBinding::class.java.isAssignableFrom(clazz)) {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
            mBinding!!.lifecycleOwner = this
            return mBinding!!.root
        }

        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData()
    }

    override fun onDestroyView() {
        mBinding?.unbind()
        mBinding = null
        super.onDestroyView()
    }

    private fun initViewModel() {
        viewModel = provideViewModel()
    }

    protected abstract fun initView(savedInstanceState: Bundle?)

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun provideViewModel(): VM

    protected open fun initData() {

    }

    protected fun getBinding(): VDB = mBinding ?: throw IllegalArgumentException(
        "${getClassByParameterizedType(
            javaClass.genericSuperclass as ParameterizedType,
            1
        ).name} not found , please check."
    )

    protected fun getBindingOrNull(): VDB? = mBinding

}