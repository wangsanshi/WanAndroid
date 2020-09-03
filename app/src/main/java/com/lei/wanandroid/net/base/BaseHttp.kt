package com.lei.wanandroid.net.base

import com.blankj.utilcode.util.Utils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.lei.wanandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

abstract class BaseHttp {
    companion object {
        private const val DEFAULT_TIMEOUT_MILLIOS = 15L
    }

    protected abstract val baseUrl: String

    protected open val timeout = DEFAULT_TIMEOUT_MILLIOS

    //okhttp 拦截器
    protected open val interceptors: Iterable<Interceptor> = emptyList()

    //CallAdapter转换器
    protected open val callAdapterFactories: Iterable<CallAdapter.Factory> = emptyList()

    //Converter转换器
    protected open val converterFactories: Iterable<Converter.Factory> = emptyList()

    protected open val retrofit: Retrofit by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED,
        ::createRetrofit
    )

    private fun createRetrofit() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createOkhttpClient())
        .apply {
            callAdapterFactories.forEach { addCallAdapterFactory(it) }
            converterFactories.forEach { addConverterFactory(it) }
        }
        .build()

    private fun createOkhttpClient() =
        OkHttpClient.Builder()
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .apply {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
                addInterceptor(loggingInterceptor)
                interceptors.forEach {
                    addInterceptor(it)
                }
                val cookieJar = PersistentCookieJar(
                    SetCookieCache(),
                    SharedPrefsCookiePersistor(Utils.getApp())
                )
                cookieJar(cookieJar)
            }
            .build()
}