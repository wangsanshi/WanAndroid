package com.lei.wanandroid.jetpack.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.net.HttpCallback
import com.lei.wanandroid.net.httpRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseIntPageKeyedDataSource<Value> : PageKeyedDataSource<Int, Value>() {
    protected var retry: (() -> Any)? = null

    val refreshState = MutableLiveData<State>()
    val loadMoreState = MutableLiveData<State>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}

abstract class AbstractIntPageKeyedDataSource<S, D>(
    private val startPage: Int = 0,
    private val policy: ApiPagingPolicy = ApiPagingPolicy.DEFAULT,
    private val request: suspend (Int) -> ApiResponse<S>
) : BaseIntPageKeyedDataSource<D>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, D>
    ) {
        GlobalScope.launch {
            refreshState.postValue(State.Loading)
            httpRequest(object : HttpCallback<S> {
                override fun onSuccess(data: S?) {
                    retry = null
                    if (data == null) {
                        refreshState.postValue(State.SuccessNoData)
                        callback.onResult(emptyList(), null, null)
                    } else {
                        val page = convert(data)
                        if (page.datas.isEmpty()) refreshState.postValue(State.SuccessNoData) else refreshState.postValue(
                            State.SuccessData
                        )
                        callback.onResult(
                            page.datas,
                            null,
                            getNextPage(policy, page.curPage, page.over)
                        )
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    refreshState.postValue(State.Failure)
                    retry = { loadInitial(params, callback) }
                }
            }) { request.invoke(startPage) }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, D>) {
        GlobalScope.launch {
            loadMoreState.postValue(State.Loading)
            httpRequest(object : HttpCallback<S> {
                override fun onSuccess(data: S?) {
                    retry = null
                    if (data == null) {
                        loadMoreState.postValue(State.SuccessNoData)
                        callback.onResult(emptyList(), null)
                    } else {
                        val page = convert(data)
                        if (page.datas.isEmpty()) loadMoreState.postValue(State.SuccessNoData) else loadMoreState.postValue(
                            State.SuccessData
                        )
                        callback.onResult(page.datas, getNextPage(policy, page.curPage, page.over))
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    loadMoreState.postValue(State.Failure)
                    retry = { loadAfter(params, callback) }
                }
            }) { request.invoke(params.key) }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, D>) {
        //ignore
    }

    abstract fun convert(source: S): Page<D>
}

//----------------------------------------------- DataSource Impl -----------------------------------------------

class SearchArticleIntPageKeyedDataSource(
    startPage: Int,
    policy: ApiPagingPolicy,
    request: suspend (Int) -> ApiResponse<Page<Article>>
) : AbstractIntPageKeyedDataSource<Page<Article>, Article>(startPage, policy, request) {
    override fun convert(source: Page<Article>): Page<Article> {
        return source
    }
}

class ShareArticleIntPageKeyedDataSource(
    startPage: Int,
    policy: ApiPagingPolicy,
    request: suspend (Int) -> ApiResponse<ShareArticle>,
    private val coinInfoLiveData: MutableLiveData<CoinInfo>
) : AbstractIntPageKeyedDataSource<ShareArticle, Article>(startPage, policy, request) {
    override fun convert(source: ShareArticle): Page<Article> {
        coinInfoLiveData.postValue(source.coinInfo)
        return source.shareArticles
    }
}

class CoinRankIntPageKeyedDataSource(
    startPage: Int,
    policy: ApiPagingPolicy,
    request: suspend (Int) -> ApiResponse<Page<CoinInfo>>
) : AbstractIntPageKeyedDataSource<Page<CoinInfo>, CoinInfo>(startPage, policy, request) {
    override fun convert(source: Page<CoinInfo>): Page<CoinInfo> {
        return source
    }
}

class CoinStatisticalIntPageKeyedDataSource(
    startPage: Int,
    policy: ApiPagingPolicy,
    request: suspend (Int) -> ApiResponse<Page<CoinSource>>
) : AbstractIntPageKeyedDataSource<Page<CoinSource>, CoinSource>(startPage, policy, request) {
    override fun convert(source: Page<CoinSource>): Page<CoinSource> {
        return source
    }
}

class CollectArticleIntPageKeyedDataSource(
    startPage: Int,
    policy: ApiPagingPolicy,
    request: suspend (Int) -> ApiResponse<Page<CollectArticle>>
) : AbstractIntPageKeyedDataSource<Page<CollectArticle>, CollectArticle>(
    startPage,
    policy,
    request
) {
    override fun convert(source: Page<CollectArticle>): Page<CollectArticle> {
        return source
    }
}

//----------------------------------------------- DataSource Factory Impl -----------------------------------------------

class SearchArticlePageFactory(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val request: suspend (Int) -> ApiResponse<Page<Article>>
) : DataSource.Factory<Int, Article>() {
    val sourceLiveData = MutableLiveData<SearchArticleIntPageKeyedDataSource>()
    override fun create(): DataSource<Int, Article> {
        val source = SearchArticleIntPageKeyedDataSource(startPage, policy, request)
        sourceLiveData.postValue(source)
        return source
    }
}

class ShareArticlePageFactory(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val coinInfoLiveData: MutableLiveData<CoinInfo>,
    private val request: suspend (Int) -> ApiResponse<ShareArticle>
) : DataSource.Factory<Int, Article>() {
    val sourceLiveData = MutableLiveData<ShareArticleIntPageKeyedDataSource>()
    override fun create(): DataSource<Int, Article> {
        val source =
            ShareArticleIntPageKeyedDataSource(startPage, policy, request, coinInfoLiveData)
        sourceLiveData.postValue(source)
        return source
    }
}

class CoinRankPageFactory(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val request: suspend (Int) -> ApiResponse<Page<CoinInfo>>
) : DataSource.Factory<Int, CoinInfo>() {
    val sourceLiveData = MutableLiveData<CoinRankIntPageKeyedDataSource>()
    override fun create(): DataSource<Int, CoinInfo> {
        val source = CoinRankIntPageKeyedDataSource(startPage, policy, request)
        sourceLiveData.postValue(source)
        return source
    }
}

class CoinStatisticalPageFactory(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val request: suspend (Int) -> ApiResponse<Page<CoinSource>>
) : DataSource.Factory<Int, CoinSource>() {
    val sourceLiveData = MutableLiveData<CoinStatisticalIntPageKeyedDataSource>()
    override fun create(): DataSource<Int, CoinSource> {
        val source = CoinStatisticalIntPageKeyedDataSource(startPage, policy, request)
        sourceLiveData.postValue(source)
        return source
    }
}

class CollectArticlePageFactory(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val request: suspend (Int) -> ApiResponse<Page<CollectArticle>>
) : DataSource.Factory<Int, CollectArticle>() {
    val sourceLiveData = MutableLiveData<CollectArticleIntPageKeyedDataSource>()
    override fun create(): DataSource<Int, CollectArticle> {
        val source = CollectArticleIntPageKeyedDataSource(startPage, policy, request)
        sourceLiveData.postValue(source)
        return source
    }
}

