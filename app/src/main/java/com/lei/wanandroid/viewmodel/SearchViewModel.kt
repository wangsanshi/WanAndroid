package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.HotWord
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : BaseViewModel(application) {
    val repository = RepositoryProviders.provideHomeRepository()

    val clearNotify by lazy { MutableLiveData<Unit>() }

    /**
     * 修改文章的收藏状态
     */
    fun modifyArticleCollectState(articleId: Int, isCollect: Boolean) {
        repository.modifyArticleCollectState(articleId, isCollect)
    }

    //----------------------------------------- 关键字 -----------------------------------------
    private val keywordListing by lazy { MutableLiveData<Listing<Article>>() }
    val keywordArticleList = keywordListing.switchMap { it.pagedList }
    val keywordRefreshState = keywordListing.switchMap { it.refreshState }
    val keywordLoadMoreState = keywordListing.switchMap { it.loadMoreState }

    val hotwordsLiveData = repository.hotWordsLiveData
    val selectedHotWord by lazy { MutableLiveData<HotWord>() }

    fun getHotWords() {
        viewModelScope.launch { repository.getHotWords() }
    }

    fun searchByKeyWord(keyword: String) {
        clearNotify.value = Unit
        keywordListing.value = repository.searchByKeyWord(keyword)
    }

    fun refreshKeyWordArticles() {
        keywordListing.value?.refresh?.invoke()
    }

    fun retryKeyWordArticles() {
        keywordListing.value?.retry?.invoke()
    }

    //----------------------------------------- 作者 ------------------------------------------
    private val authorListing by lazy { MutableLiveData<Listing<Article>>() }
    val authorArticleList = authorListing.switchMap { it.pagedList }
    val authorRefreshState = authorListing.switchMap { it.refreshState }
    val authorLoadMoreState = authorListing.switchMap { it.loadMoreState }

    fun searchByAuthor(author: String) {
        clearNotify.value = Unit
        authorListing.value = repository.searchByAuthor(author)
    }

    fun refreshAuthorArticles() {
        authorListing.value?.refresh?.invoke()
    }

    fun retryAuthorArticles() {
        authorListing.value?.retry?.invoke()
    }

    //----------------------------------------- 公众号 -----------------------------------------

    val selectedPublicAccountLiveData by lazy { MutableLiveData<PublicAccount>() }
    val publicAccountsLiveData by lazy { StateLiveData<List<PublicAccount>>() }

    private val wechatListing by lazy { MutableLiveData<Listing<Article>>() }
    val wechatPagedList = wechatListing.switchMap { it.pagedList }
    val wechatRefreshState = wechatListing.switchMap { it.refreshState }
    val wechatLoadMoreState = wechatListing.switchMap { it.loadMoreState }

    fun getPublicAccounts() {
        viewModelScope.launch { repository.getWechatPublicAccounts(publicAccountsLiveData) }
    }

    fun searchByWechatPublicAccount(account: PublicAccount, keyword: String?) {
        clearNotify.value = Unit
        wechatListing.value = repository.searchByWechat(account.id, keyword)
    }

    fun refreshWechatArticles() {
        wechatListing.value?.refresh?.invoke()
    }

    fun retryWechatArticles() {
        wechatListing.value?.retry?.invoke()
    }
}