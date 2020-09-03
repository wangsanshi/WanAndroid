package com.lei.wanandroid.data.repository

import androidx.lifecycle.switchMap
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.data.repository.base.BaseArticleRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.ApiPagingPolicy
import com.lei.wanandroid.jetpack.paging.ArticlePageBoundaryCallback
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.jetpack.paging.SearchArticlePageFactory
import com.lei.wanandroid.net.DefaultHttpCallback
import com.lei.wanandroid.net.DefaultRetrofitClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

/**
 * 首页、广场、问答、公众号中的文章使用了本地数据库作缓存
 * 搜索模块的文章直接从网络获取
 */
class HomeRepository(private val ioExecutor: Executor) : BaseArticleRepository() {

    //----------------------------------------- Banner -----------------------------------------
    fun getBannerLiveData() = localDataSource.getBannerLiveData()
    suspend fun refreshBanners(refreshBannerStateLiveData: StateLiveData<Any>) {
        withContext(ioDispatcher) {
            refreshBannerStateLiveData.postLoading()
            remoteDataSource.getBanners(object :
                DefaultHttpCallback<List<BannerBean>>(sErrorCodeHandler) {
                override fun onSuccess(data: List<BannerBean>?) {
                    refreshBannerStateLiveData.postSuccess(Any())
                    data?.let { if (it.isNotEmpty()) launchIO { localDataSource.saveBanners(it) } }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshBannerStateLiveData.postFailure(message)
                }
            })
        }
    }

    //----------------------------------------- 文章相关 -----------------------------------------
    fun getTopArticlesLiveData() = localDataSource.getTopArticleLiveData()

    suspend fun refreshTopArticles() {
        withContext(ioDispatcher) {
            remoteDataSource.getTopArticles(object : DefaultHttpCallback<List<Article>>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: List<Article>?) {
                    data?.let { launchIO { localDataSource.saveArticles(it) } }
                }
            })
        }
    }

    fun getFirstPageArticles(): Listing<Article> {
        val callback = ArticlePageBoundaryCallback(
            articleType = ARTICLE_LOCAL_TYPE_FIRST_PAGE,
            startPage = 0,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.NEXT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getArticles(it) }
        val sourceFactory =
            localDataSource.getArticlesDataSourceFactory(ARTICLE_LOCAL_TYPE_FIRST_PAGE)
        val livePagedList = getPagedListLiveData(sourceFactory, callback)
        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    private suspend fun insertArticlesToDdByType(articles: List<Article>, articleType: Int) {
        insertArticlesToDb(articles)
        localDataSource.saveArticleID(articles, articleType)
    }

    private suspend fun insertArticlesToDb(articles: List<Article>) {
        localDataSource.saveArticles(articles)
    }

    fun getSqureArticles(): Listing<Article> {
        val callback = ArticlePageBoundaryCallback(
            articleType = ARTICLE_LOCAL_TYPE_SQUARE,
            startPage = 0,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.NEXT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getSquareArticles(it) }
        val sourceFactory = localDataSource.getArticlesDataSourceFactory(ARTICLE_LOCAL_TYPE_SQUARE)
        val livePagedList = getPagedListLiveData(sourceFactory, callback)
        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    fun getQuestionArticles(): Listing<Article> {
        val callback = ArticlePageBoundaryCallback(
            articleType = ARTICLE_LOCAL_TYPE_QUESTION,
            startPage = 0,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.DEFAULT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getQuestions(it) }
        val sourceFactory =
            localDataSource.getArticlesDataSourceFactory(ARTICLE_LOCAL_TYPE_QUESTION)
        val livePagedList = getPagedListLiveData(sourceFactory, callback)
        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    suspend fun getWechatPublicAccounts(publicAccountsLiveData: StateLiveData<List<PublicAccount>>) {
        publicAccountsLiveData.postLoading()
        withContext(ioDispatcher) {
            //先从数据库里查
            val localCaches = localDataSource.getWXPublicAccountList()
            if (localCaches.isNotEmpty()) {
                publicAccountsLiveData.postSuccess(localCaches)
                return@withContext
            }
            //数据库没有，从网络获取
            remoteDataSource.getPublicAccounts(object : DefaultHttpCallback<List<PublicAccount>>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: List<PublicAccount>?) {
                    if (data != null && data.isNotEmpty()) {
                        publicAccountsLiveData.postSuccess(data)
                        GlobalScope.launch { localDataSource.saveWXPublicAccounts(data) }
                    } else {
                        publicAccountsLiveData.postFailure("未获取到公众号列表")
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    publicAccountsLiveData.postFailure(message)
                }
            })
        }
    }

    fun getWechatArticles(wechatAccountID: Int): Listing<Article> {
        val callback = ArticlePageBoundaryCallback(
            articleType = ARTICLE_LOCAL_TYPE_WECHAT,
            startPage = 1,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.DEFAULT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getWechatArticles(wechatAccountID, it) }
        val sourceFactory =
            localDataSource.getWechatArticlesDataSourceFactory(wechatAccountID)
        val livePagedList = getPagedListLiveData(sourceFactory, callback)
        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    //----------------------------------------- 搜索相关 -----------------------------------------
    val hotWordsLiveData by lazy { StateLiveData<List<HotWord>>() }

    suspend fun getHotWords() {
        hotWordsLiveData.postLoading()
        withContext(ioDispatcher) {
            val localHotWords = localDataSource.getHotWords()
            if (localHotWords.isNotEmpty()) {
                hotWordsLiveData.postSuccess(localHotWords)
                return@withContext
            }

            remoteDataSource.getHotWords(object : DefaultHttpCallback<List<HotWord>>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: List<HotWord>?) {
                    if (data != null && data.isNotEmpty()) {
                        hotWordsLiveData.postSuccess(data)
                        GlobalScope.launch { localDataSource.saveHotWords(data) }
                    } else {
                        hotWordsLiveData.postFailure("未获取到热词")
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    hotWordsLiveData.postFailure(message)
                }
            })
        }
    }

    fun searchByKeyWord(keyword: String): Listing<Article> {
        val sourceFactory = SearchArticlePageFactory(
            startPage = 0,
            policy = ApiPagingPolicy.NEXT
        ) {
            DefaultRetrofitClient.getService().searchByKeyWord(it, keyword)
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

    fun searchByAuthor(author: String): Listing<Article> {
        val sourceFactory = SearchArticlePageFactory(
            startPage = 0,
            policy = ApiPagingPolicy.NEXT
        ) {
            DefaultRetrofitClient.getService().searchByAuthor(it, author)
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

    fun searchByWechat(accountId: Int, keyword: String?): Listing<Article> {
        val sourceFactory = SearchArticlePageFactory(
            startPage = 1,
            policy = ApiPagingPolicy.DEFAULT
        ) {
            DefaultRetrofitClient.getService().searchByWechat(accountId, it, keyword)
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

    //----------------------------------------- 收藏相关 -----------------------------------------

    fun modifyArticleCollectStateWithLocal(articleId: Int, isCollect: Boolean) {
        launchIO {
            localDataSource.updateArticleCollectState(articleId, isCollect)
            modifyArticleCollectState(articleId, isCollect, false)
        }
    }

    fun modifyArticleCollectState(
        articleId: Int,
        isCollect: Boolean,
        needUpdateLocal: Boolean = true
    ) {
        if (isCollect) {
            launchIO { collectInnerArticle(articleId, needUpdateLocal) }
        } else {
            launchIO { cancelCollectFromArticleList(articleId, needUpdateLocal) }
        }
    }

    /**
     * 收藏站内文章
     */
    private suspend fun collectInnerArticle(
        id: Int,
        needUpdateLocal: Boolean,
        collectStateLiveData: StateLiveData<Any>? = null
    ) {
        collectStateLiveData?.postLoading()
        remoteDataSource.collectInnerArticle(id,
            object : DefaultHttpCallback<Any?>(sErrorCodeHandler) {
                override fun onSuccess(data: Any?) {
                    collectStateLiveData?.postSuccess(Any())
                    if (needUpdateLocal) launchIO {
                        localDataSource.updateArticleCollectState(
                            id,
                            true
                        )
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    collectStateLiveData?.postFailure(message)
                }
            })
    }

    private suspend fun cancelCollectFromArticleList(
        id: Int,
        needUpdateLocal: Boolean,
        collectStateLiveData: StateLiveData<Any>? = null
    ) {
        collectStateLiveData?.postLoading()
        remoteDataSource.cancelCollectFromArticleList(id,
            object : DefaultHttpCallback<Any?>(sErrorCodeHandler) {
                override fun onSuccess(data: Any?) {
                    collectStateLiveData?.postSuccess(Any())
                    if (needUpdateLocal) launchIO {
                        localDataSource.updateArticleCollectState(
                            id,
                            false
                        )
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    collectStateLiveData?.postFailure(message)
                }
            })
    }

}