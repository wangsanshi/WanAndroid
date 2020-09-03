package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.ui.activitys.RegisterActivity
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideLoginRepository()

    val versionName = "V${AppUtils.getAppVersionName()}"

    val username = MutableLiveData<String>().apply {
        repository.registerUserLiveData.value?.success?.let {
            value = it.username
        }
    }
    val password = MutableLiveData<String>().apply {
        repository.registerUserLiveData.value?.success?.let {
            value = it.password
        }
    }
    val enable = MediatorLiveData<Boolean>().apply {
        addSource(username) {
            value = !(it.isNullOrEmpty() || password.value.isNullOrEmpty())
        }
        addSource(password) {
            value = !(it.isNullOrEmpty() || username.value.isNullOrEmpty())
        }
    }

    val loginUser = repository.loginUserLiveData

    fun toRegisterAccount() = ActivityUtils.startActivity(RegisterActivity::class.java)

    fun login() {
        viewModelScope.launch {
            repository.login(username.value!!, password.value!!)
        }
    }

}