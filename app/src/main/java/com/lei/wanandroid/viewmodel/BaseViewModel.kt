package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * ViewModel 绝不能引用视图、Lifecycle或可能存储Activity上下文引用的任何类
 * 如果ViewModel 需要Application上下文，则可以扩展 AndroidViewModel
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO)