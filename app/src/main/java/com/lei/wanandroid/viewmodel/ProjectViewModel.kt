package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideProjectRepository()

    val refreshProjectStateLiveData by lazy { StateLiveData<Any>() }
    fun refreshProjectClassification() {
        viewModelScope.launch { repository.refreshProjectClassification(refreshProjectStateLiveData) }
    }

    fun getProjectClassificationLiveData() = repository.getProjectClassificationLiveData()

    private val projectArticleListing by lazy { MutableLiveData<Listing<Article>>() }
    val projectArticlePagedList = projectArticleListing.switchMap { it.pagedList }
    val projectArticleRefreshState = projectArticleListing.switchMap { it.refreshState }
    val projectArticleLoadMoreState = projectArticleListing.switchMap { it.loadMoreState }

    fun getProjectArticleLisitng(cid: Int) {
        projectArticleListing.value = repository.getProjectArticleListing(cid)
    }

    fun refreshProjectArticle() {
        projectArticleListing.value?.refresh?.invoke()
    }

    fun retryProjectArticle() {
        projectArticleListing.value?.retry?.invoke()
    }

    /**
     * 修改文章的收藏状态
     */
    fun modifyArticleCollectState(articleId: Int, isCollect: Boolean) {
        RepositoryProviders.provideHomeRepository()
            .modifyArticleCollectStateWithLocal(articleId, isCollect)
    }
}