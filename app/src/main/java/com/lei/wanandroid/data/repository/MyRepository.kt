package com.lei.wanandroid.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.*
import com.lei.wanandroid.net.DefaultHttpCallback
import com.lei.wanandroid.net.DefaultRetrofitClient
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class MyRepository(private val ioExecutor: Executor) : BaseRepository() {
    val readArticlesCountLiveData = localDataSource.getReadArticlesCountLiveData()

    fun getCollectWebsitesLiveData() = localDataSource.getWebsitesLiveData()

    suspend fun clearWebsites() = withContext(ioDispatcher) { localDataSource.clearWebsites() }

    suspend fun refreshCollectWebsite(refreshWebsitesStateLiveData: StateLiveData<Boolean>) {
        withContext(ioDispatcher) {
            refreshWebsitesStateLiveData.postLoading()
            remoteDataSource.getCollectWebsiteList(object : DefaultHttpCallback<List<WebSite>>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: List<WebSite>?) {
                    if (data != null && data.isNotEmpty()) {
                        launchIO {
                            localDataSource.saveWebsites(data)
                            refreshWebsitesStateLiveData.postSuccess(true)
                        }
                    } else {
                        refreshWebsitesStateLiveData.postSuccess(false)
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshWebsitesStateLiveData.postFailure(message)
                }
            })
        }
    }

    suspend fun deleteCollectWebsite(
        id: Int,
        deleteCollectWebsiteLiveData: StateLiveData<Any>? = null
    ) {
        withContext(ioDispatcher) {
            deleteCollectWebsiteLiveData?.postLoading()
            remoteDataSource.deleteCollectWebsite(id, object : DefaultHttpCallback<Any?>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: Any?) {
                    launchIO {
                        localDataSource.deleteWebsite(id)
                        deleteCollectWebsiteLiveData?.postSuccess(Any())
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    deleteCollectWebsiteLiveData?.postFailure(message)
                }
            })
        }
    }

    private suspend fun insertArticlesToDb(articles: List<Article>) {
        localDataSource.saveArticles(articles)
        localDataSource.saveShareArticleID(articles.map { ShareArticleID(it.id) })
    }

    suspend fun clearShareArticleIDs() {
        localDataSource.clearShareArticleIDs()
    }

    fun getShareArticleListing(
        userId: Int,
        coinInfoLiveData: MutableLiveData<CoinInfo>
    ): Listing<Article> {
        val callback = ShareArticleBoundaryCallback(
            this::insertArticlesToDb,
            coinInfoLiveData,
            1,
            ApiPagingPolicy.DEFAULT,
            ioExecutor
        ) {
            if (isLoginUser(userId)) {
                DefaultRetrofitClient.getService().getMyShareArticles(it)
            } else {
                DefaultRetrofitClient.getService().getShareArticlesByUserId(userId, it)
            }
        }
        val sourceFactory = localDataSource.getShareArticleDataSourceFactory()
        val livePagedList = getPagedListLiveData(sourceFactory, callback)

        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    suspend fun shareArticle(
        title: String,
        link: String,
        shareArticleLiveData: StateLiveData<Any>
    ) {
        withContext(ioDispatcher) {
            shareArticleLiveData.postLoading()
            remoteDataSource.shareArticle(title, link, object : DefaultHttpCallback<Any?>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: Any?) {
                    shareArticleLiveData.postSuccess(Any())
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    shareArticleLiveData.postFailure(message)
                }
            })
        }
    }

    fun getCoinRankListing(): Listing<CoinInfo> {
        val sourceFactory = CoinRankPageFactory(
            startPage = 1,
            policy = ApiPagingPolicy.DEFAULT
        ) {
            DefaultRetrofitClient.getService().getCoinRankList(it)
        }
        val livePagedList =
            getPagedListLiveData(sourceFactory, pageSize = 30, prefetchDistance = 15)

        return Listing(
            pagedList = livePagedList,
            refreshState = sourceFactory.sourceLiveData.switchMap { it.refreshState },
            loadMoreState = sourceFactory.sourceLiveData.switchMap { it.loadMoreState },
            refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
            retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() }
        )
    }

    fun getCoinStatisticalListing(): Listing<CoinSource> {
        val sourceFactory = CoinStatisticalPageFactory(
            startPage = 1,
            policy = ApiPagingPolicy.DEFAULT
        ) {
            DefaultRetrofitClient.getService().getCoinSourceList(it)
        }
        val livePagedList = getPagedListLiveData(sourceFactory)

        return Listing(
            pagedList = livePagedList,
            refreshState = sourceFactory.sourceLiveData.switchMap { it.refreshState },
            loadMoreState = sourceFactory.sourceLiveData.switchMap { it.loadMoreState },
            refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
            retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() }
        )
    }

    private suspend fun saveCollectArticles(datas: List<CollectArticle>) {
        localDataSource.saveCollectArticles(datas)
    }

    fun getCollectArticleListing(): Listing<CollectArticle> {
        val callback = CollectArticleBoundaryCallback(
            handleResponse = this::saveCollectArticles,
            startPage = 0,
            policy = ApiPagingPolicy.NEXT,
            ioExecutor = ioExecutor
        ) {
            DefaultRetrofitClient.getService().getCollectArticles(it)
        }
        val sourceFactory = localDataSource.getCollectArticleDataSourceFactory()
        val livePagedList = getPagedListLiveData(sourceFactory, callback)

        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    suspend fun clearCollectArticals() {
        withContext(ioDispatcher) { localDataSource.clearCollectArticles() }
    }

    suspend fun cancelCollectArticleFromMy(
        id: Int,
        originId: Int,
        stateLiveData: StateLiveData<Any>
    ) {
        withContext(ioDispatcher) {
            stateLiveData.postLoading()
            remoteDataSource.cancelCollectArticleFromMy(
                id,
                originId,
                object : DefaultHttpCallback<Any?>(sErrorCodeHandler) {
                    override fun onSuccess(data: Any?) {
                        Log.d(
                            "my collect",
                            "cancel collect success, articleId=$id, originId=$originId"
                        )
                        //originId表示文章的原来id
                        launchIO {
                            localDataSource.deleteCollectArticleById(id)
                            if (originId != -1) localDataSource.updateArticleCollectState(
                                originId,
                                false
                            )
                            stateLiveData.postSuccess(Any())
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        super.onFailure(code, message)
                        stateLiveData.postFailure(message)
                    }
                })
        }
    }

    /**
     * 收藏站外文章
     */
    suspend fun collectOuterArticle(
        title: String,
        author: String,
        link: String,
        collectOuterArticleStateLiveData: StateLiveData<Any>? = null
    ) {
        withContext(ioDispatcher) {
            collectOuterArticleStateLiveData?.postLoading()
            remoteDataSource.collectOuterArticle(title, author, link,
                object : DefaultHttpCallback<CollectArticle>(sErrorCodeHandler) {
                    override fun onSuccess(data: CollectArticle?) {
                        if (data != null) {
                            launchIO {
                                localDataSource.saveCollectArticles(listOf(data))
                                collectOuterArticleStateLiveData?.postSuccess(Any())
                            }
                        } else {
                            collectOuterArticleStateLiveData?.postSuccess(Any())
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        collectOuterArticleStateLiveData?.postFailure(message)
                    }
                })
        }
    }

    suspend fun collectWebsite(
        name: String,
        link: String,
        collectWebsiteStateLiveData: StateLiveData<Any>? = null
    ) {
        withContext(ioDispatcher) {
            collectWebsiteStateLiveData?.postLoading()
            remoteDataSource.collectWebsite(name, link, object : DefaultHttpCallback<WebSite>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: WebSite?) {
                    if (data == null) collectWebsiteStateLiveData?.postSuccess(Any()) else launchIO {
                        localDataSource.saveWebsites(listOf(data))
                        collectWebsiteStateLiveData?.postSuccess(Any())
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    collectWebsiteStateLiveData?.postFailure(message)
                }
            })
        }
    }

    suspend fun updateWebsite(
        id: Int,
        newName: String,
        newLink: String,
        updateWebsiteStateLiveData: StateLiveData<Any>? = null
    ) {
        withContext(ioDispatcher) {
            updateWebsiteStateLiveData?.postLoading()
            remoteDataSource.updateWebsite(
                id,
                newName,
                newLink,
                object : DefaultHttpCallback<WebSite>(
                    sErrorCodeHandler
                ) {
                    override fun onSuccess(data: WebSite?) {
                        if (data == null) updateWebsiteStateLiveData?.postSuccess(Any()) else launchIO {
                            localDataSource.saveWebsites(listOf(data))
                            updateWebsiteStateLiveData?.postSuccess(Any())
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        super.onFailure(code, message)
                        updateWebsiteStateLiveData?.postFailure(message)
                    }
                })
        }
    }

    suspend fun deleteShareArticle(
        articleId: Int,
        deleteShareArticleStateLiveData: StateLiveData<Any>? = null
    ) {
        withContext(ioDispatcher) {
            deleteShareArticleStateLiveData?.postLoading()
            remoteDataSource.deleteShareArticle(articleId, object : DefaultHttpCallback<Any?>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: Any?) {
                    launchIO {
                        localDataSource.removeShareArticleID(articleId)
                        deleteShareArticleStateLiveData?.postSuccess(Any())
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    deleteShareArticleStateLiveData?.postFailure(message)
                }
            })
        }
    }
}