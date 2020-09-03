package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.PublicAccount
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideHomeRepository()

    /**
     * 修改文章的收藏状态
     */
    fun modifyArticleCollectState(articleId: Int, isCollect: Boolean) {
        repository.modifyArticleCollectStateWithLocal(articleId, isCollect)
    }

    //----------------------------------------- 推荐 -----------------------------------------
    val bannerLiveData by lazy { repository.getBannerLiveData() }
    val refreshBannerStateLiveData by lazy { StateLiveData<Any>() }
    fun refreshBanners() {
        viewModelScope.launch { repository.refreshBanners(refreshBannerStateLiveData) }
    }

    //----------------------------------------- 首页 -----------------------------------------
    private val articleListing = MutableLiveData<Listing<Article>>()
    val articlesPagedList = articleListing.switchMap { it.pagedList }
    val articleRefreshState = articleListing.switchMap { it.refreshState }
    val articleLoadMoreState = articleListing.switchMap { it.loadMoreState }
    val isShowTopArticles by lazy { MutableLiveData<Boolean>().apply { value = true } }

    val topArticlesLiveData = repository.getTopArticlesLiveData()

    fun initArticles() {
        articleListing.value = repository.getFirstPageArticles()
    }

    fun refreshArticles() {
        if (isShowTopArticles.value != false) {
            viewModelScope.launch { repository.refreshTopArticles() }
        }
        articleListing.value?.refresh?.invoke()
    }

    fun retryArticle() {
        articleListing.value?.retry?.invoke()
    }

    //----------------------------------------- 广场 -----------------------------------------
    private val squareArticleListing = MutableLiveData<Listing<Article>>()
    val squareArticlePagedList = squareArticleListing.switchMap { it.pagedList }
    val squareArticleRefreshState = squareArticleListing.switchMap { it.refreshState }
    val squareArticleLoadMoreState = squareArticleListing.switchMap { it.loadMoreState }

    fun initSquareArticles() {
        squareArticleListing.value = repository.getSqureArticles()
    }

    fun refreshSquareArticles() {
        squareArticleListing.value?.refresh?.invoke()
    }

    fun retrySquareArticle() {
        squareArticleListing.value?.retry?.invoke()
    }

    //----------------------------------------- 问答 -----------------------------------------
    private val questionArticleListing = MutableLiveData<Listing<Article>>()
    val questionArticlePagedList = questionArticleListing.switchMap { it.pagedList }
    val questionArticleRefreshState = questionArticleListing.switchMap { it.refreshState }
    val questionArticleLoadMoreState = questionArticleListing.switchMap { it.loadMoreState }

    fun initQuestionArticles() {
        questionArticleListing.value = repository.getQuestionArticles()
    }

    fun refreshQuestionArticles() {
        questionArticleListing.value?.refresh?.invoke()
    }

    fun retryQuestionArticle() {
        questionArticleListing.value?.retry?.invoke()
    }

    //----------------------------------------- 公众号 ----------------------------------------
    private val wechatArticleListing = MutableLiveData<Listing<Article>>()
    val wechatArticlePagedList = wechatArticleListing.switchMap { it.pagedList }
    val wechatArticleRefreshState = wechatArticleListing.switchMap { it.refreshState }
    val wechatArticleLoadMoreState = wechatArticleListing.switchMap { it.loadMoreState }

    val publicAccountsLiveData by lazy { StateLiveData<List<PublicAccount>>() }

    fun getPublicAccounts() {
        viewModelScope.launch { repository.getWechatPublicAccounts(publicAccountsLiveData) }
    }

    fun initWechatArticlesByID(wechatID: Int) {
        wechatArticleListing.value = repository.getWechatArticles(wechatID)
    }

    fun refreshWechatArticles() {
        wechatArticleListing.value?.refresh?.invoke()
    }

    fun retryWechatArticle() {
        wechatArticleListing.value?.retry?.invoke()
    }
}