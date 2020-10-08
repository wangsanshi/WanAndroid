package com.lei.wanandroid.jetpack.lifecycle

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.NetworkUtils
import com.lei.wanandroid.jetpack.bus.LiveDataBus

class NetworkManager : LifecycleObserver {
    private var mNetworkStatusChangedListener: NetworkUtils.OnNetworkStatusChangedListener? = null

    companion object {
        const val EVENT_TAG_NETWORK_CHANGE = "tag_network_change"
    }

    fun init(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerListener() {
        Log.d(EVENT_TAG_NETWORK_CHANGE, "registerListener")
        mNetworkStatusChangedListener = object : NetworkUtils.OnNetworkStatusChangedListener {
            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                Log.d(EVENT_TAG_NETWORK_CHANGE, "onConnected")
                LiveDataBus.send(EVENT_TAG_NETWORK_CHANGE, true)
            }

            override fun onDisconnected() {
                Log.d(EVENT_TAG_NETWORK_CHANGE, "onDisconnected")
                LiveDataBus.send(EVENT_TAG_NETWORK_CHANGE, false)
            }
        }.apply {
            NetworkUtils.registerNetworkStatusChangedListener(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun unregisterListener() {
        Log.d(EVENT_TAG_NETWORK_CHANGE, "unregisterListener")
        mNetworkStatusChangedListener?.let {
            NetworkUtils.unregisterNetworkStatusChangedListener(it)
        }
        mNetworkStatusChangedListener = null
    }
}