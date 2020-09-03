package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.CoinInfo
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import kotlinx.coroutines.launch

class UserInfoViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideMyRepository()

    var isLoginUser = false

    fun initUser(userId: Int) {
        isLoginUser = repository.isLoginUser(userId)
    }

    private val shareArticleListing by lazy { MutableLiveData<Listing<Article>>() }
    val coinInfoLiveData by lazy { MutableLiveData<CoinInfo>() }
    val shareArticlePagedList = shareArticleListing.switchMap { it.pagedList }
    val shareArticleRefreshState = shareArticleListing.switchMap { it.refreshState }
    val shareArticleLoadMoreState = shareArticleListing.switchMap { it.loadMoreState }

    fun clearShareArticleIDs() {
        viewModelScope.launch { repository.clearShareArticleIDs() }
    }

    fun getShareArticleListing(userId: Int) {
        shareArticleListing.value = repository.getShareArticleListing(userId, coinInfoLiveData)
    }

    fun refreshShareArticles() {
        shareArticleListing.value?.refresh?.invoke()
    }

    fun retryShareArticles() {
        shareArticleListing.value?.retry?.invoke()
    }

    val shareArticleLiveData by lazy { StateLiveData<Any>() }
    fun shareArticle(title: String, link: String) {
        viewModelScope.launch { repository.shareArticle(title, link, shareArticleLiveData) }
    }

    /**
     * 修改文章的收藏状态
     */
    fun modifyArticleCollectState(articleId: Int, isCollect: Boolean) {
        RepositoryProviders.provideHomeRepository().modifyArticleCollectState(articleId, isCollect)
    }

    val deleteShareArticleStateLiveData by lazy { StateLiveData<Any>() }
    fun deleteShareArticle(article: Article) {
        viewModelScope.launch {
            repository.deleteShareArticle(
                article.id,
                deleteShareArticleStateLiveData
            )
        }
    }
}