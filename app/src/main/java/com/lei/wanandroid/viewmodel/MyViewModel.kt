package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.CoinInfo
import com.lei.wanandroid.data.bean.CoinSource
import com.lei.wanandroid.data.bean.CollectArticle
import com.lei.wanandroid.data.bean.WebSite
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideMyRepository()
    val loginUser = repository.loginUserLiveData
    val loginOrRegisterOrName = MutableLiveData<String>()

    val nightMode = MutableLiveData<Boolean>().apply {
        value = repository.localDataSource.isNightMode()
    }

    fun setNightMode(result: Boolean) {
        if (result != repository.localDataSource.isNightMode()) {
            repository.localDataSource.setNightMode(result)
        }
    }

    fun toLoginOrUserInfo() {
        repository.userContext.showUserInfo()
    }

    fun toReadArticlesHistory() {
        repository.userContext.goReadArticle()
    }

    fun toCollect() {
        repository.userContext.toCollect()
    }

    val readArticlesCountLiveData = repository.readArticlesCountLiveData

    val isLogin = repository.isLogin()

    //----------------------------------------- 积分排行榜 -----------------------------------------
    private val coinRankListing by lazy { MutableLiveData<Listing<CoinInfo>>() }
    val coinRankPagedList = coinRankListing.switchMap { it.pagedList }
    val coinRankRefreshState = coinRankListing.switchMap { it.refreshState }
    val coinRankLoadMoreState = coinRankListing.switchMap { it.loadMoreState }

    fun getCoinRank() {
        coinRankListing.value = repository.getCoinRankListing()
    }

    fun refreshCoinRank() {
        coinRankListing.value?.refresh?.invoke()
    }

    fun retryCoinRank() {
        coinRankListing.value?.retry?.invoke()
    }

    //----------------------------------------- 积分统计 -----------------------------------------
    private val coinStatisticalListing by lazy { MutableLiveData<Listing<CoinSource>>() }
    val coinStatisticalPagedList = coinStatisticalListing.switchMap { it.pagedList }
    val coinStatisticalRefreshState = coinStatisticalListing.switchMap { it.refreshState }
    val coinStatisticalLoadMoreState = coinStatisticalListing.switchMap { it.loadMoreState }

    fun getCoinStatistical() {
        coinStatisticalListing.value = repository.getCoinStatisticalListing()
    }

    fun refreshCoinStatistical() {
        coinStatisticalListing.value?.refresh?.invoke()
    }

    fun retryCoinStatistical() {
        coinStatisticalListing.value?.retry?.invoke()
    }

    //----------------------------------------- 我的收藏 -----------------------------------------
    val collectWebsitesLiveData by lazy { repository.getCollectWebsitesLiveData() }
    val refreshWebsitesStateLiveData by lazy { StateLiveData<Boolean>() }
    fun refreshCollectWebsites() {
        viewModelScope.launch { repository.refreshCollectWebsite(refreshWebsitesStateLiveData) }
    }

    fun clearCollectWebsites() = viewModelScope.launch { repository.clearWebsites() }

    private val collectArticleListing by lazy { MutableLiveData<Listing<CollectArticle>>() }
    val collectArticlePagedList = collectArticleListing.switchMap { it.pagedList }
    val collectArticleRefreshState = collectArticleListing.switchMap { it.refreshState }
    val collectArticleLoadMoreState = collectArticleListing.switchMap { it.loadMoreState }

    fun getCollectArticle() {
        collectArticleListing.value = repository.getCollectArticleListing()
    }

    fun refreshCollectArticle() {
        collectArticleListing.value?.refresh?.invoke()
    }

    fun retryCollectArticle() {
        collectArticleListing.value?.retry?.invoke()
    }

    val cancelCollectArticleState by lazy { StateLiveData<Any>() }
    fun cancelCollectArticleFromMy(article: CollectArticle) {
        viewModelScope.launch {
            repository.cancelCollectArticleFromMy(
                article.id,
                article.originId,
                cancelCollectArticleState
            )
        }
    }

    val deleteCollectWebsiteLiveData by lazy { StateLiveData<Any>() }
    fun deleteCollectWebsite(webSite: WebSite) {
        viewModelScope.launch {
            repository.deleteCollectWebsite(
                webSite.id,
                deleteCollectWebsiteLiveData
            )
        }
    }

    fun clearCollectArticals() {
        viewModelScope.launch { repository.clearCollectArticals() }
    }

    val collectOuterArticleStateLiveData by lazy { StateLiveData<Any>() }
    fun collectOuterArticle(title: String, author: String, link: String) {
        viewModelScope.launch {
            repository.collectOuterArticle(
                title,
                author,
                link,
                collectOuterArticleStateLiveData
            )
        }
    }

    val collectWebsiteStateLiveData by lazy { StateLiveData<Any>() }
    fun collectWebsite(name: String, link: String) {
        viewModelScope.launch { repository.collectWebsite(name, link, collectWebsiteStateLiveData) }
    }

    val updateWebsiteStateLiveData by lazy { StateLiveData<Any>() }
    fun updateWebsite(id: Int, newName: String, newLink: String) {
        viewModelScope.launch {
            repository.updateWebsite(
                id,
                newName,
                newLink,
                updateWebsiteStateLiveData
            )
        }
    }
}
