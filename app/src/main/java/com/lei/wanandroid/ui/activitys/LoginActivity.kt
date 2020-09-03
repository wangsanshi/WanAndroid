package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.databinding.ActivityLoginBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.util.showLongToast
import com.lei.wanandroid.viewmodel.LoginViewModel

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override fun getLayoutId() = R.layout.activity_login

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = viewModel
        mBinding.activity = this
    }

    override fun provideViewModel(): LoginViewModel {
        return ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun initData() {
        viewModel.loginUser.observe(
            this,
            StateOberver(object : IStateCallback<User> {
                override fun onSuccess(value: User) {
                    showLongToast("登录成功")
                    finish()
                }

                override fun onLoading() {

                }

                override fun onFailure(message: String) {
                    showLongToast(message)
                }
            }),
            false
        )
    }

}
