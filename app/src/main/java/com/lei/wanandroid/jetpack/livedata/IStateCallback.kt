package com.lei.wanandroid.jetpack.livedata

interface IStateCallback<T> {

    fun onSuccess(value: T)

    fun onLoading()

    fun onFailure(message: String)

}