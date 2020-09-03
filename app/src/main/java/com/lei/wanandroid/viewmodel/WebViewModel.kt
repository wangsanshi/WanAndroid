package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import kotlinx.coroutines.launch

class WebViewModel(application: Application) : BaseViewModel(application) {
    val title = MutableLiveData<String>()

    fun saveReadArticle(article: ReadArticle) {
        viewModelScope.launch {
            RepositoryProviders.provideWebViewRepository().saveReadArticle(article)
        }
    }

}