package com.lei.wanandroid.data.source.local

import com.lei.wanandroid.data.bean.User

interface ILocalDataSource {
    fun getLoginUser(): User?

    fun saveLoginUser(user: User)

    fun isNightMode(): Boolean

    fun setNightMode(value: Boolean)

    fun getBannerDao(): BannerDao

    fun getWXPublicAccountDao(): WXPublicAccountDao

    fun getReadArticleDao(): ReadArticleDao

    fun getArticleDao(): ArticleDao

    fun getHotWordDao(): HotWordDao

    fun getTodoDao(): TodoDao

    fun getWebsiteDao(): WebsiteDao

    fun getCollectArticleDao(): CollectArticleDao

    fun getProjectDao(): ProjectDao

    fun getNavigationDao(): NavigationDao

    fun getTreeDao(): TreeDao

    suspend fun withTransaction(block: suspend () -> Unit)
}