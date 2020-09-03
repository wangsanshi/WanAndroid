package com.lei.wanandroid.ui.helper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setDefaultSettings() {
    val settings = settings
    //5.0以上开启混合模式加载
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    settings.loadWithOverviewMode = true
    settings.useWideViewPort = true
    //允许js代码
    settings.javaScriptEnabled = true
    //允许SessionStorage/LocalStorage存储
    settings.domStorageEnabled = true
    //禁用放缩
    settings.displayZoomControls = false
    settings.builtInZoomControls = false
    //禁用文字缩放
    settings.textZoom = 100
    //允许缓存，设置缓存位置
    settings.setAppCacheEnabled(true)
    settings.setAppCachePath(context.getDir("webcache", 0).path)
    //允许WebView使用File协议
    settings.allowFileAccess = true
    //自动加载图片
    settings.setLoadsImagesAutomatically(true)
}

/**
 * 第一个参数表示水平方向的位置
 * 第二个参数表示竖直方向的位置
 * 第三个参数表示竖直方向上滑动的速率
 */
typealias ScorllCallback = (Int, Int, Float) -> Unit

class CustomWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    private lateinit var tracker: VelocityTracker
    private var scrollCallback: ScorllCallback? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollCallback?.invoke(l, t, tracker.yVelocity)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        tracker.addMovement(event)
        event?.let {
            if (it.action == MotionEvent.ACTION_UP) {
                tracker.computeCurrentVelocity(1000)
            }
        }
        return super.onTouchEvent(event)
    }

    fun setScrollCallback(callback: ScorllCallback) {
        this.scrollCallback = callback
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        tracker = VelocityTracker.obtain()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        tracker.recycle()
    }

}