package com.lei.wanandroid.data.source.remote

import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.net.HttpCallback

interface IRemoteDataSource {
    suspend fun login(username: String, password: String, callback: HttpCallback<User>)

    suspend fun register(
        username: String,
        password: String,
        repassword: String,
        callback: HttpCallback<User>
    )

    suspend fun getBanners(callback: HttpCallback<List<BannerBean>>)

    suspend fun getTopArticles(callback: HttpCallback<List<Article>>)

    suspend fun getPublicAccounts(calback: HttpCallback<List<PublicAccount>>)

    suspend fun getHotWords(callback: HttpCallback<List<HotWord>>)

    suspend fun deleteTodo(id: Int, callback: HttpCallback<Any?>)

    suspend fun addTodo(params: Map<String, Any>, callback: HttpCallback<Todo>)

    suspend fun updateTodo(id: Int, params: Map<String, Any>, callback: HttpCallback<Todo>)

    suspend fun updateTodoStatus(id: Int, status: Int, callback: HttpCallback<Todo>)

    suspend fun collectInnerArticle(articleId: Int, callback: HttpCallback<Any?>)

    suspend fun collectOuterArticle(
        title: String,
        author: String,
        link: String,
        callback: HttpCallback<CollectArticle>
    )

    suspend fun cancelCollectFromArticleList(articleId: Int, callback: HttpCallback<Any?>)

    suspend fun getCollectWebsiteList(callback: HttpCallback<List<WebSite>>)

    suspend fun shareArticle(title: String, link: String, callback: HttpCallback<Any?>)

    suspend fun cancelCollectArticleFromMy(id: Int, originId: Int, callback: HttpCallback<Any?>)

    suspend fun deleteCollectWebsite(id: Int, callback: HttpCallback<Any?>)

    suspend fun collectWebsite(name: String, link: String, callback: HttpCallback<WebSite>)

    suspend fun updateWebsite(
        id: Int,
        newName: String,
        newLink: String,
        callback: HttpCallback<WebSite>
    )

    suspend fun deleteShareArticle(articleId: Int, callback: HttpCallback<Any?>)

    suspend fun getProjectClassification(callback: HttpCallback<List<ProjectClassification>>)
}