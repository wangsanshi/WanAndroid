package com.lei.wanandroid.viewmodel

import android.app.Application
import com.lei.wanandroid.data.repository.base.RepositoryProviders

class ReadArticlesHistoryViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideReadArticlesHistoryRepository()

    fun getPagedListLiveData() = repository.getReadArticlesHistory()

}