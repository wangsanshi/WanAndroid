package com.lei.wanandroid.data.repository

import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.net.DefaultHttpCallback
import kotlinx.coroutines.withContext

class LoginRepository : BaseRepository() {

    suspend fun login(username: String, password: String) {
        withContext(ioDispatcher) {
            loginUserLiveData.postLoading()
            remoteDataSource.login(
                username,
                password,
                object : DefaultHttpCallback<User>(sErrorCodeHandler) {
                    override fun onSuccess(data: User?) {
                        data?.let {
                            loginUserLiveData.postSuccess(it)
                            saveUser(it)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        super.onFailure(code, message)
                        loginUserLiveData.postFailure(message)
                    }
                })

        }
    }

    private fun saveUser(user: User) {
        launchIO { localDataSource.saveLoginUser(user) }
    }
}