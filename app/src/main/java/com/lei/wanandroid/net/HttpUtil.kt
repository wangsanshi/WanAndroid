package com.lei.wanandroid.net

import com.lei.wanandroid.data.bean.ApiResponse
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

typealias ErrorCodeHandler = (Int, String) -> Unit

interface HttpCallback<T> {
    fun onSuccess(data: T?)
    fun onFailure(code: Int, message: String)
}

abstract class DefaultHttpCallback<T>(private val handler: ErrorCodeHandler) : HttpCallback<T> {
    override fun onFailure(code: Int, message: String) {
        handler.invoke(code, message)
    }
}

suspend fun <T> httpRequest(
    callback: HttpCallback<T>, request: suspend () -> ApiResponse<T>
) = try {
    val response = request.invoke()
    if (response.errorCode == ApiResponse.SUCCESS_CODE) {
        callback.onSuccess(response.data)
    } else {
        val msg = response.errorMsg ?: ""
        callback.onFailure(response.errorCode, msg)
    }
} catch (t: Throwable) {
    callback.onFailure(ApiResponse.THROWABLE_CODE, handleThrowable(t))
}

private fun handleThrowable(t: Throwable): String {
    t.printStackTrace()
    var result = "未知错误"
    when (t) {
        is ConnectException, is UnknownHostException -> {
            result = "无法连接到服务器，请检查网络后重试。"
        }
        is SocketTimeoutException -> {
            result = "网络连接超时，请检查网络后重试。"
        }
        is NullPointerException -> {
            result = "没有获取到数据，请稍候重试！"
        }
    }

    return result
}