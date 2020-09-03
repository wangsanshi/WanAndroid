package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityRegisterBinding
import com.lei.wanandroid.viewmodel.RegisterViewModel

class RegisterActivity : BaseActivity<RegisterViewModel, ActivityRegisterBinding>() {
    override fun getLayoutId() = R.layout.activity_register

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = viewModel
        mBinding.activity = this
    }

    override fun initData() {
//        viewModel.registerState.observe(
//            this,
//            StateOberver(RegisterCallback())
//        )
    }

    override fun provideViewModel(): RegisterViewModel {
        return ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

//    private inner class RegisterCallback :
//        IStateCallback<User> {
//        override fun onSuccess(value: User?) {
//            showLongToast("注册成功")
//            finish()
//        }
//
//        override fun onLoading(message: String, progress: Float) {
//
//        }
//
//        override fun onFailure(message: String, cause: Throwable?) {
//            showLongToast(message)
//        }
//    }
}