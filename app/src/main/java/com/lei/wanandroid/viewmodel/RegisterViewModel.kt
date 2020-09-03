package com.lei.wanandroid.viewmodel

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : BaseViewModel(application) {
    val username = MutableLiveData<String>().apply { value = "" }
    val password = MutableLiveData<String>().apply { value = "" }
    val repassword = MutableLiveData<String>().apply { value = "" }
    val enable = MutableLiveData<Boolean>().apply { value = false }
    val versionName = "V${AppUtils.getAppVersionName()}"

    private val repository = RepositoryProviders.provideRegisterRepository()
    val registerState = repository.registerUserLiveData

    val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                username.value = it.toString()
            }
            verify()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                password.value = it.toString()
            }
            verify()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    val repasswordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                repassword.value = it.toString()
            }
            verify()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun verify() {
        enable.value =
            username.value!!.isNotBlank() && password.value!!.isNotBlank() && repassword.value!!.isNotBlank()
    }

    fun register() {
        viewModelScope.launch {
            repository.register(
                username.value!!,
                password.value!!,
                repassword.value!!
            )
        }
    }
}