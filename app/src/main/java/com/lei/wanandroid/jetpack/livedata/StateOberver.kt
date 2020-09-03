package com.lei.wanandroid.jetpack.livedata

import androidx.lifecycle.Observer

class StateOberver<T>(private val callback: IStateCallback<T>) : Observer<IState<T>> {
    override fun onChanged(t: IState<T>?) {
        t?.onSuccess(callback::onSuccess)
            ?.onFailure(callback::onFailure)
            ?.onLoading(callback::onLoading)
    }
}