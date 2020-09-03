package com.lei.wanandroid.jetpack.livedata

import com.lei.wanandroid.jetpack.State

class StateImpl<T> constructor(
    override val state: State,
    val success: T? = null,
    val failureMessage: String = ""
) : IState<T> {

    override fun onSuccess(f: (T) -> Unit): IState<T> {
        if (state == State.SuccessData) {
            success?.let {
                f.invoke(it)
            }
        }

        return this
    }

    override fun onLoading(f: () -> Unit): IState<T> {
        if (state == State.Loading) {
            f.invoke()
        }
        return this
    }

    override fun onFailure(f: (String) -> Unit): IState<T> {
        if (state == State.Failure) {
            f.invoke(failureMessage)
        }
        return this
    }

}