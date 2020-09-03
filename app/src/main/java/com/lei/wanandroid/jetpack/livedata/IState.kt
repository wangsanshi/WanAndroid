package com.lei.wanandroid.jetpack.livedata

import com.lei.wanandroid.jetpack.State

interface IState<T> {
    val state: State

    fun onSuccess(f: (T) -> Unit): IState<T>

    fun onLoading(f: () -> Unit): IState<T>

    fun onFailure(f: (String) -> Unit): IState<T>

}