package com.lei.wanandroid.data.source.remote

import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.net.DefaultRetrofitClient
import com.lei.wanandroid.net.HttpCallback
import com.lei.wanandroid.net.httpRequest

object RemoteDataSource : IRemoteDataSource {
    override suspend fun login(username: String, password: String, callback: HttpCallback<User>) {
        val params = mapOf<String, Any>("username" to username, "password" to password)
        httpRequest(callback) {
            DefaultRetrofitClient.getService().login(params)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        repassword: String,
        callback: HttpCallback<User>
    ) {
        val params = mapOf<String, Any>(
            "username" to username,
            "password" to password,
            "repassword" to repassword
        )
        httpRequest(callback) {
            DefaultRetrofitClient.getService().register(params)
        }
    }

    override suspend fun getBanners(callback: HttpCallback<List<BannerBean>>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().getBanner()
        }
    }

    override suspend fun getTopArticles(callback: HttpCallback<List<Article>>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().getTopArticles()
        }
    }

    override suspend fun getPublicAccounts(calback: HttpCallback<List<PublicAccount>>) {
        httpRequest(calback) {
            DefaultRetrofitClient.getService().getWXPublicAccounts()
        }
    }

    override suspend fun getHotWords(callback: HttpCallback<List<HotWord>>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().getHotWords()
        }
    }

    override suspend fun deleteTodo(id: Int, callback: HttpCallback<Any?>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().deleteTodo(id)
        }
    }

    override suspend fun addTodo(params: Map<String, Any>, callback: HttpCallback<Todo>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().addTodo(params)
        }
    }

    override suspend fun updateTodo(
        id: Int,
        params: Map<String, Any>,
        callback: HttpCallback<Todo>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().updateTodo(id, params)
        }
    }

    override suspend fun updateTodoStatus(id: Int, status: Int, callback: HttpCallback<Todo>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().updateTodoStatus(id, status)
        }
    }

    override suspend fun collectInnerArticle(articleId: Int, callback: HttpCallback<Any?>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().collectInnerArticle(articleId)
        }
    }

    override suspend fun collectOuterArticle(
        title: String,
        author: String,
        link: String,
        callback: HttpCallback<CollectArticle>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().collectOuterArticle(title, author, link)
        }
    }

    override suspend fun cancelCollectFromArticleList(
        articleId: Int,
        callback: HttpCallback<Any?>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().cancelCollectFromArticleList(articleId)
        }
    }

    override suspend fun getCollectWebsiteList(callback: HttpCallback<List<WebSite>>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().getCollectWebsiteList()
        }
    }

    override suspend fun shareArticle(title: String, link: String, callback: HttpCallback<Any?>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().shareArticle(title, link)
        }
    }

    override suspend fun cancelCollectArticleFromMy(
        id: Int,
        originId: Int,
        callback: HttpCallback<Any?>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().cancelCollectFromMy(id, originId)
        }
    }

    override suspend fun deleteCollectWebsite(id: Int, callback: HttpCallback<Any?>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().deleteCollectWebsite(id)
        }
    }

    override suspend fun collectWebsite(
        name: String,
        link: String,
        callback: HttpCallback<WebSite>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().collectWebsite(name, link)
        }
    }

    override suspend fun updateWebsite(
        id: Int,
        newName: String,
        newLink: String,
        callback: HttpCallback<WebSite>
    ) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().updateWebsite(id, newName, newLink)
        }
    }

    override suspend fun deleteShareArticle(articleId: Int, callback: HttpCallback<Any?>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().deleteShareArticle(articleId)
        }
    }

    override suspend fun getProjectClassification(callback: HttpCallback<List<ProjectClassification>>) {
        httpRequest(callback) {
            DefaultRetrofitClient.getService().getProjectClassification()
        }
    }

}