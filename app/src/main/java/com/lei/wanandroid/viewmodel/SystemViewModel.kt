package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import kotlinx.coroutines.launch

class SystemViewModel(application: Application) : BaseViewModel(application) {
    private val repository by lazy { RepositoryProviders.provideSystemRepository() }

    //----------------------------------------- 体系 ----------------------------------------
    val refreshTreeStateLiveData by lazy { StateLiveData<Any>() }
    fun getTreeLiveData() = repository.getTreeLiveData().apply {
        refreshTreeList()
    }

    fun refreshTreeList() {
        viewModelScope.launch { repository.refreshTreeList(refreshTreeStateLiveData) }
    }

    //----------------------------------------- 导航 ----------------------------------------
    val refreshNavigationStateLiveData by lazy { StateLiveData<Any>() }

    fun getNavigationLiveData() =
        repository.getNavigationLiveData().apply { refreshNavigationList() }

    fun refreshNavigationList() {
        viewModelScope.launch { repository.refreshNavigationList(refreshNavigationStateLiveData) }
    }

}