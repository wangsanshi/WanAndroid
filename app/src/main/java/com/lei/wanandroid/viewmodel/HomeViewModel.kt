package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
    val articleListingLiveData = MutableLiveData<Listing<Article>>().apply {
        value = repository.getFirstPageArticles()
    }

    //----------------------------------------- 广场 -----------------------------------------
    val squareArticleListing = MutableLiveData<Listing<Article>>().apply {
        value = repository.getSqureArticles()
    }

    //----------------------------------------- 问答 -----------------------------------------
    val questionArticleListing = MutableLiveData<Listing<Article>>().apply {
        value = repository.getQuestionArticles()
    }

    //----------------------------------------- 公众号 ----------------------------------------
    val wechatArticleListing = MutableLiveData<Listing<Article>>()

    val publicAccountsLiveData by lazy { StateLiveData<List<PublicAccount>>() }

    fun getPublicAccounts() {
        viewModelScope.launch { repository.getWechatPublicAccounts(publicAccountsLiveData) }
    }

    fun initWechatArticlesByID(wechatID: Int) {
        wechatArticleListing.value = repository.getWechatArticles(wechatID)
    }

}