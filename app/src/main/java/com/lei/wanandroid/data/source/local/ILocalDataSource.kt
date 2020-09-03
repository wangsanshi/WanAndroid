package com.lei.wanandroid.data.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.lei.wanandroid.data.bean.*

interface ILocalDataSource {
    fun getLoginUser(): User?

    fun saveLoginUser(user: User)

    fun isNightMode(): Boolean

    fun setNightMode(value: Boolean)

    fun getArticlesDataSourceFactory(articleType: Int): DataSource.Factory<Int, Article>

    suspend fun saveArticleID(articles: List<Article>, articleType: Int)

    fun getReadArticlesHistoryDataSourceFactory(): DataSource.Factory<Int, ReadArticle>

    fun getWechatArticlesDataSourceFactory(chapterId: Int): DataSource.Factory<Int, Article>

    suspend fun updateArticleCollectState(articleId: Int, isCollect: Boolean)

    fun getTodosFactoryByFinishDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    fun getTodosFactoryByFinishDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    fun getTodosFactoryByCreateDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    fun getTodosFactoryByCreateDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    fun getReadArticlesCountLiveData(): LiveData<Int>

    fun getBannerLiveData(): LiveData<List<BannerBean>>

    fun getTopArticleLiveData(): LiveData<List<Article>>

    suspend fun saveBanners(bannerBeans: List<BannerBean>)

    suspend fun saveReadArticle(article: ReadArticle)

    suspend fun saveTodos(todos: List<Todo>)

    suspend fun deleteTodo(todoId: Int)

    suspend fun saveWXPublicAccounts(accounts: List<PublicAccount>)

    suspend fun getWXPublicAccountList(): List<PublicAccount>

    suspend fun saveArticles(articles: List<Article>)

    suspend fun updateArticle(article: Article)

    suspend fun deleteAllArticles()

    suspend fun getHotWords(): List<HotWord>

    suspend fun saveHotWords(hotwords: List<HotWord>)

    fun getWebsitesLiveData(): LiveData<List<WebSite>>

    suspend fun saveWebsites(datas: List<WebSite>)

    suspend fun deleteWebsite(id: Int)

    suspend fun clearWebsites()

    fun getCollectArticleDataSourceFactory(): DataSource.Factory<Int, CollectArticle>

    suspend fun saveCollectArticles(datas: List<CollectArticle>)

    suspend fun deleteCollectArticleById(id: Int)

    suspend fun clearCollectArticles()

    fun getShareArticleDataSourceFactory(): DataSource.Factory<Int, Article>

    suspend fun saveShareArticleID(datas: List<ShareArticleID>)

    suspend fun clearShareArticleIDs()

    suspend fun removeShareArticleID(articleId: Int)

    fun getProjectClassificationLiveData(): LiveData<List<ProjectClassification>>

    suspend fun saveProjectClassification(datas: List<ProjectClassification>)

    fun getProjectArticleDataSourceFactory(classificationId: Int): DataSource.Factory<Int, Article>

    suspend fun saveProjectArticleID(datas: List<ProjectArticleID>)
}