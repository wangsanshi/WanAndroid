package com.lei.wanandroid.ui.fragments.my

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.databinding.FragmentMyBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.viewmodel.MyViewModel

class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {

    override fun getLayoutId() = R.layout.fragment_my

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        getBinding().model = viewModel
        viewModel.loginUser.observe(viewLifecycleOwner, StateOberver(object : IStateCallback<User> {
            override fun onSuccess(value: User) {
                getBinding().tvReadArticleCount.visibility = View.VISIBLE
                viewModel.loginOrRegisterOrName.value = value.nickname
            }

            override fun onLoading() {
            }

            override fun onFailure(message: String) {
                getBinding().tvReadArticleCount.visibility = View.GONE
                viewModel.loginOrRegisterOrName.value = getString(R.string.login_or_register)
            }
        }))
        viewModel.readArticlesCountLiveData.observe(viewLifecycleOwner, Observer {
            getBinding().tvReadArticleCount.text = getString(R.string.read_article_count_format, it)
        })
    }

}