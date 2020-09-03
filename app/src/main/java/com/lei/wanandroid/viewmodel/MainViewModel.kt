package com.lei.wanandroid.viewmodel

import android.app.Application
import com.lei.wanandroid.data.source.UserContext

class MainViewModel(application: Application) : BaseViewModel(application) {
    val userContext = UserContext
}