package com.lei.wanandroid.data.repository

import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.net.DefaultHttpCallback
import kotlinx.coroutines.withContext

class RegisterRepository : BaseRepository() {

    suspend fun register(username: String, password: String, repassword: String) {
        withContext(ioDispatcher) {
            registerUserLiveData.postLoading()
            remoteDataSource.register(
                username,
                password,
                repassword,
                object : DefaultHttpCallback<User>(sErrorCodeHandler) {
                    override fun onSuccess(data: User?) {
                        data?.let {
                            registerUserLiveData.postSuccess(it)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        super.onFailure(code, message)
                        registerUserLiveData.postFailure(message)
                    }
                })
        }
    }

}