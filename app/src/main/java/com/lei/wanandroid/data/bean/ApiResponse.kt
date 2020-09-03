package com.lei.wanandroid.data.bean

data class ApiResponse<T>(val errorCode: Int, val errorMsg: String?, val data: T?) {
    companion object {
        const val SUCCESS_CODE = 0
        const val THROWABLE_CODE = -10000
    }
}