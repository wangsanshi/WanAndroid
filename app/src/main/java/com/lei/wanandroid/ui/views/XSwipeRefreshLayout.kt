package com.lei.wanandroid.ui.views

import android.content.Context
import android.os.Looper
import android.os.MessageQueue
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

class XSwipeRefreshLayout(context: Context, attributeSet: AttributeSet) :
    SwipeRefreshLayout(context, attributeSet) {

    private val mTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
    private var mIsDragger = false
    private var startX = .0f
    private var startY = .0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = it.x
                    startY = it.y
                    mIsDragger = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mIsDragger) {
                        return false
                    }
                    val endY = it.y
                    val endX = it.x
                    val distanceX = abs(endX - startX)
                    val distanceY = abs(endY - startY)
                    if (distanceX > mTouchSlop && distanceX > distanceY) {
                        mIsDragger = true
                        return false
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    mIsDragger = false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

}