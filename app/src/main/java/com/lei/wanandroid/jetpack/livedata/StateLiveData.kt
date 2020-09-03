package com.lei.wanandroid.jetpack.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.lei.wanandroid.jetpack.State

class StateLiveData<T> : LiveData<StateImpl<T>>() {

    fun postSuccess(value: T) {
        postValue(StateImpl(state = State.SuccessData, success = value))
    }

    fun postLoading() {
        postValue(StateImpl(state = State.Loading))
    }

    fun postFailure(failureMessage: String = "加载出错") {
        postValue(
            StateImpl(
                state = State.Failure,
                failureMessage = failureMessage
            )
        )
    }

    /**
     * @param isSticky 是否为粘性事件（即是否接收最后一次事件）
     */
    fun observe(
        owner: LifecycleOwner,
        observer: Observer<in StateImpl<T>>,
        isSticky: Boolean = true
    ) {
        super.observe(owner, observer)
        if (!isSticky) {
            try {
                LiveDataHelper.hookObserver(this, observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
