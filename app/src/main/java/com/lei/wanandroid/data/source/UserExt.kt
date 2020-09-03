package com.lei.wanandroid.data.source

import com.blankj.utilcode.util.ActivityUtils
import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.data.source.local.LocalDataSource
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.activitys.*
import com.tencent.bugly.crashreport.CrashReport

object UserContext {
    val loginUserLiveData = StateLiveData<User>().apply {
        val loginUser = LocalDataSource.getLoginUser()
        if (loginUser != null) {
            postSuccess(loginUser)
        } else {
            postFailure("未登录")
        }
    }
    val registerUserLiveData by lazy { StateLiveData<User>() }

    private lateinit var mState: IUserState
    private var isLogin = false

    init {
        loginUserLiveData.observeForever(StateOberver(object : IStateCallback<User> {
            override fun onSuccess(value: User) {
                isLogin = true
                mState = LoginState()
                //记录异常日志的用户id
                CrashReport.setUserId(value.id.toString())
            }

            override fun onLoading() {
            }

            override fun onFailure(message: String) {
                isLogin = false
                mState = LogoutState()
            }
        }))
    }

    fun isLogin() = isLogin

    fun showUserInfo() {
        mState.showUserInfo()
    }

    fun addTodo() {
        mState.addTodo()
    }

    fun shareArticle() {
        mState.shareArticle()
    }

    fun goReadArticle() {
        mState.goReadArticle()
    }

    fun toCollect() {
        mState.toCollect()
    }
}

interface IUserState {
    fun showUserInfo()

    fun addTodo()

    fun shareArticle()

    fun goReadArticle()

    fun toCollect()
}

class LoginState : IUserState {
    override fun showUserInfo() {
        UserInfoActivity.toThis(
            LocalDataSource.getLoginUser()!!.id,
            LocalDataSource.getLoginUser()!!.username
        )
    }

    override fun addTodo() {
        RepositoryProviders.provideTodoRepository().currentTodoLiveData.value = null
        ActivityUtils.startActivity(TodoDetailActivity::class.java)
    }

    override fun shareArticle() {
    }

    override fun goReadArticle() {
        ActivityUtils.startActivity(ReadArticlesHistoryActivity::class.java)
    }

    override fun toCollect() {
        ActivityUtils.startActivity(CollectActivity::class.java)
    }

}

class LogoutState : IUserState {
    override fun showUserInfo() {
        goLoginActivity()
    }

    override fun addTodo() {
        goLoginActivity()
    }

    override fun shareArticle() {
        goLoginActivity()
    }

    override fun goReadArticle() {
        goLoginActivity()
    }

    override fun toCollect() {
        goLoginActivity()
    }

    private fun goLoginActivity() {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }
}