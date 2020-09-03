package com.lei.wanandroid.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.blankj.utilcode.util.Utils

object ToastUtil {
    private val sHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var toast: Toast? = null

    fun showShortToast(msg: String) {
        if (Thread.currentThread() == sHandler.looper.thread) {
            toast?.cancel()
            toast = Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_SHORT).apply {
                show()
            }
        } else {
            sHandler.post { showShortToast(msg) }
        }
    }

    fun showLongToast(msg: String) {
        if (Thread.currentThread() == sHandler.looper.thread) {
            toast?.cancel()
            toast = Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_SHORT).apply {
                show()
            }
        } else {
            sHandler.post { showLongToast(msg) }
        }
    }

    fun showGravityShortToast(msg: String, gravity: Int) {
        if (Thread.currentThread() == sHandler.looper.thread) {
            toast?.cancel()
            toast = Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_SHORT).apply {
                setGravity(gravity, 0, 0)
                show()
            }
        } else {
            sHandler.post { showGravityShortToast(msg, gravity) }
        }
    }
}