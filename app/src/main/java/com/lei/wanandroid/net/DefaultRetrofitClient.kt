package com.lei.wanandroid.net

import com.lei.wanandroid.net.api.ApiService
import com.lei.wanandroid.net.base.BaseHttp
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory

object DefaultRetrofitClient : BaseHttp() {
    private val api by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val baseUrl: String
        get() = ApiService.BASE_URL_WAN_ANDROID

    override val converterFactories: Iterable<Converter.Factory>
        get() = listOf(GsonConverterFactory.create())

    fun getService(): ApiService {
        return api
    }
}