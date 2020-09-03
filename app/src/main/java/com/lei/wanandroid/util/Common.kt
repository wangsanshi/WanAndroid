package com.lei.wanandroid.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson

fun showGravityShortToast(msg: String, gravity: Int) = ToastUtil.showGravityShortToast(msg, gravity)
fun showShortToast(msg: String) = ToastUtil.showShortToast(msg)
fun showLongToast(msg: String) = ToastUtil.showLongToast(msg)

fun Activity.showLoading(loadingView: View? = null) {
    disableWindow(window)
    loadingView?.let {
        if (it is SwipeRefreshLayout) it.isRefreshing = true else it.visibility = View.VISIBLE
    }
}

fun Activity.hideLoading(loadingView: View? = null) {
    enableWindow(window)
    loadingView?.let {
        if (it is SwipeRefreshLayout) it.isRefreshing = false else it.visibility = View.GONE
    }
}

fun disableWindow(window: Window) = window.setFlags(
    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
)

fun enableWindow(window: Window) = window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

val sGson: Gson by lazy {
    Gson()
}

fun copyText(context: Context, text: CharSequence) {
    (context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
        primaryClip = ClipData.newPlainText("text", text)
    }
}

fun formatHtmlString(src: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(src, FROM_HTML_MODE_COMPACT)
} else {
    @Suppress("DEPRECATION")
    Html.fromHtml(src)
}

