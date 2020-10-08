package com.lei.wanandroid.data.repository

import androidx.lifecycle.switchMap
import androidx.paging.DataSource
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.*
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
class HomeRepository(private val ioExecutor: Executor) : BaseRepository() {

    //----------------------------------------- Banner -----------------------------------------
    fun getBannerLiveData() = localDataSource.getBannerDao().getBannerLiveData()
    suspend fun refreshBanners(refreshBannerStateLiveData: StateLiveData<Any>) {
        withContext(ioDispatcher) {
            refreshBannerStateLiveData.postLoading()
            remoteDataSource.getBanners(object :
                DefaultHttpCallback<List<BannerBean>>(sErrorCodeHandler) {
                override fun onSuccess(data: List<BannerBean>?) {
                    refreshBannerStateLiveData.postSuccess(Any())
                    data?.let {
                        if (it.isNotEmpty()) launchIO {
                            localDataSource.getBannerDao().saveBanners(it)
                        }
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshBannerStateLiveData.postFailure(message)
                }
            })
        }
    }

    private fun getArticleDataSourceFactoryByType(articleType: Int): DataSource.Factory<Int, Article> {
        return when (articleType) {
            ARTICLE_LOCAL_TYPE_FIRST_PAGE -> localDataSource.getArticleDao()
                .getFirstPageArticleDataSourceFactory()
            ARTICLE_LOCAL_TYPE_SQUARE -> localDataSource.getArticleDao()
                .getSquareArticleDataSourceFactory()
            ARTICLE_LOCAL_TYPE_QUESTION -> localDataSource.getArticleDao()
                .getQuestionArticleDataSourceFactory()
            else -> throw IllegalArgumentException("getArticleDataSourceFactoryByType(),articleType=$articleType not impl.")
        }
    }

    private suspend fun saveArticleIDByType(articles: List<Article>, articleType: Int) {
        when (articleType) {
            ARTICLE_LOCAL_TYPE_FIRST_PAGE -> localDataSource.getArticleDao()
                .saveFirstPageArticleID(articles.map { FirstPageArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_QUESTION -> localDataSource.getArticleDao()
                .saveQuestionArticleID(articles.map { QuestionArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_SQUARE -> localDataSource.getArticleDao()
                .saveSquareArticleID(articles.map { SquareArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_WECHAT -> localDataSource.getArticleDao()
                .saveWechatArticleID(articles.map {
                    WechatArticleID(it.id, it.chapterId)
                })
            else -> throw IllegalArgumentException("saveArticleIDByType(),articleType=$articleType not impl.")
        }
    }

    //----------------------------------------- 文章相关 -----------------------------------------

    fun getFirstPageArticles(): Listing<Article> {
        val callback = FirstPageArticlePageBoundaryCallback(
            startPage = 0,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.NEXT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getArticles(it) }
        val sourceFactory = getArticleDataSourceFactoryByType(ARTICLE_LOCAL_TYPE_FIRST_PAGE)
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
        localDataSource.withTransaction {
            insertArticlesToDb(articles)
            saveArticleIDByType(articles, articleType)
        }
    }

    private suspend fun insertArticlesToDb(articles: List<Article>) {
        localDataSource.getArticleDao().saveArticles(articles)
    }

    fun getSqureArticles(): Listing<Article> {
        val callback = ArticlePageBoundaryCallback(
            articleType = ARTICLE_LOCAL_TYPE_SQUARE,
            startPage = 0,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.NEXT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getSquareArticles(it) }
        val sourceFactory = getArticleDataSourceFactoryByType(ARTICLE_LOCAL_TYPE_SQUARE)
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
            startPage = 1,
            ioExecutor = ioExecutor,
            policy = ApiPagingPolicy.DEFAULT,
            handleResponse = this::insertArticlesToDdByType
        ) { DefaultRetrofitClient.getService().getQuestions(it) }
        val sourceFactory = getArticleDataSourceFactoryByType(ARTICLE_LOCAL_TYPE_QUESTION)
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
            val localCaches = localDataSource.getWXPublicAccountDao().getWXPublicAccountList()
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
                        launchIO {
                            localDataSource.getWXPublicAccountDao().saveWXPublicAccounts(data)
                        }
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
            localDataSource.getArticleDao().getWechatArticleDataSourceFactory(wechatAccountID)
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
            val localHotWords = localDataSource.getHotWordDao().getHotWords()
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
                        GlobalScope.launch { localDataSource.getHotWordDao().saveHotWords(data) }
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
            localDataSource.getArticleDao().updateArticleCollectState(articleId, isCollect)
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
                        localDataSource.getArticleDao().updateArticleCollectState(
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
                        localDataSource.getArticleDao().updateArticleCollectState(
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