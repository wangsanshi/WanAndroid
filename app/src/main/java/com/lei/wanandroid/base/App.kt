package com.lei.wanandroid.base

import android.app.Application
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.BuildConfig
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化工具类
        Utils.init(this)
        //初始化Bugly
        initBugly()
    }

    private fun initBugly() {
        val strategy = CrashReport.UserStrategy(this)
        strategy.setUploadProcess(ProcessUtils.getCurrentProcessName() == packageName)
        CrashReport.initCrashReport(this, "16f75fc9c7", BuildConfig.DEBUG, strategy)
    }
}
