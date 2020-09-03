package com.lei.wanandroid.jetpack.bus

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class DefaultEvent(
    private val liveDataMap: HashMap<String, MutableLiveData<Any?>>,
    private val tag: String
) : Event {

    override fun observe(activity: ComponentActivity, observer: Observer<Any?>) {
        subscribe(activity, observer)
    }

    override fun observe(fragment: Fragment, observer: Observer<Any?>) {
        subscribe(fragment, observer)
    }

    override fun observeForever(activity: ComponentActivity, observer: Observer<Any?>) {
        subscribeForever(activity, observer)
    }

    override fun observeForever(fragment: Fragment, observer: Observer<Any?>) {
        subscribeForever(fragment, observer)
    }

    private fun subscribe(owner: LifecycleOwner, observer: Observer<Any?>) {
        liveDataMap[tag] = MutableLiveData()
        liveDataMap[tag]?.observe(owner, Observer { o ->
            observer.onChanged(o)
        })
    }

    private fun subscribeForever(owner: LifecycleOwner, observer: Observer<Any?>) {
        liveDataMap[tag] = MutableLiveData()
        liveDataMap[tag]?.observeForever { o ->
            observer.onChanged(o)
        }
    }

}