package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
    val keywordListingLiveData by lazy { MutableLiveData<Listing<Article>>() }

    val hotwordsLiveData = repository.hotWordsLiveData
    val selectedHotWord by lazy { MutableLiveData<HotWord>() }

    fun getHotWords() {
        viewModelScope.launch { repository.getHotWords() }
    }

    fun searchByKeyWord(keyword: String) {
        clearNotify.value = Unit
        keywordListingLiveData.value = repository.searchByKeyWord(keyword)
    }

    //----------------------------------------- 作者 ------------------------------------------
    val authorListingLiveData by lazy { MutableLiveData<Listing<Article>>() }

    fun searchByAuthor(author: String) {
        clearNotify.value = Unit
        authorListingLiveData.value = repository.searchByAuthor(author)
    }

    //----------------------------------------- 公众号 -----------------------------------------

    val selectedPublicAccountLiveData by lazy { MutableLiveData<PublicAccount>() }
    val publicAccountsLiveData by lazy { StateLiveData<List<PublicAccount>>() }

    val wechatListingLiveData by lazy { MutableLiveData<Listing<Article>>() }

    fun getPublicAccounts() {
        viewModelScope.launch { repository.getWechatPublicAccounts(publicAccountsLiveData) }
    }

    fun searchByWechatPublicAccount(account: PublicAccount, keyword: String?) {
        clearNotify.value = Unit
        wechatListingLiveData.value = repository.searchByWechat(account.id, keyword)
    }
}