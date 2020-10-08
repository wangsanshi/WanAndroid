package com.lei.wanandroid.jetpack.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.net.DefaultRetrofitClient
import com.lei.wanandroid.net.HttpCallback
import com.lei.wanandroid.net.httpRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor

/**
 * Available request types.
 */
enum class RequestType {
    /**
     * Corresponds to an initial request made to a [DataSource] or the empty state for
     * a [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
     */
    INITIAL,

    /**
     * Corresponds to the `loadBefore` calls in [DataSource] or
     * `onItemAtFrontLoaded` in
     * [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
     */
    BEFORE,

    /**
     * Corresponds to the `loadAfter` calls in [DataSource] or
     * `onItemAtEndLoaded` in
     * [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
     */
    AFTER
}

fun getNextPage(policy: ApiPagingPolicy, curPage: Int, over: Boolean): Int? {
    var nextPage: Int? = null
    if (policy == ApiPagingPolicy.DEFAULT) {
        nextPage = if (over) null else curPage + 1
    } else if (policy == ApiPagingPolicy.NEXT) {
        nextPage = if (over) null else curPage
    }
    return nextPage
}

abstract class BasePageBoundaryCallback<S, D>(
    private val startPage: Int,
    private val policy: ApiPagingPolicy,
    private val ioExecutor: Executor,
    private val request: suspend (Int) -> ApiResponse<S>
) : PagedList.BoundaryCallback<D>() {
    companion object {
        private const val TAG = "PageBoundaryCallback"
    }

    protected var nextPage: Int? = null
    val refreshState = MutableLiveData<State>()
    val loadState = MutableLiveData<State>()

    protected var retry: (() -> Any)? = null
    protected var refresh: () -> Any = { onZeroItemsLoaded() }

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    fun refresh() {
        refresh.invoke()
    }

    override fun onZeroItemsLoaded() {
        GlobalScope.launch {
            Log.d(TAG, "onZeroItemsLoaded，初始化数据(thread=${Thread.currentThread().name})")
            refreshState.postValue(State.Loading)
            httpRequest(object : HttpCallback<S> {
                override fun onSuccess(data: S?) {
                    Log.d(
                        TAG,
                        "onZeroItemsLoaded，加载初始化数据成功(thread=${Thread.currentThread().name})"
                    )
                    retry = null
                    val page = convert(data)
                    if (page == null || page.datas.isEmpty()) {
                        nextPage = null
                        refreshState.postValue(State.SuccessNoData)
                    } else {
                        nextPage = getNextPage(policy, page.curPage, page.over)
                        handleResponse(page.datas, RequestType.INITIAL)
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    Log.w(
                        TAG,
                        "onZeroItemsLoaded，初始化数据失败(code=$code,message=$message,thread=${Thread.currentThread().name})"
                    )
                    refreshState.postValue(State.Failure)
                }
            }) {
                request.invoke(startPage)
            }
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: D) {
        GlobalScope.launch {
            if (nextPage != null) {
                Log.d(
                    TAG,
                    "onItemAtEndLoaded，加载下一页数据(nextPage=$nextPage,thread=${Thread.currentThread().name})"
                )
                loadState.postValue(State.Loading)
                httpRequest(object : HttpCallback<S> {
                    override fun onSuccess(data: S?) {
                        Log.d(
                            TAG,
                            "onItemAtEndLoaded，加载下一页数据成功(thread=${Thread.currentThread().name})"
                        )
                        retry = null
                        val page = convert(data)
                        if (page == null || page.datas.isEmpty()) {
                            loadState.postValue(State.SuccessNoData)
                            nextPage = null
                        } else {
                            nextPage = getNextPage(policy, page.curPage, page.over)
                            handleResponse(page.datas, RequestType.AFTER)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        Log.w(
                            TAG,
                            "onItemAtEndLoaded，初始化数据失败(code=$code,message=$message,thread=${Thread.currentThread().name})"
                        )
                        loadState.postValue(State.Failure)
                        retry = { onItemAtEndLoaded(itemAtEnd) }
                    }
                }) {
                    request.invoke(nextPage!!)
                }
            } else {
                Log.w(TAG, "onItemAtEndLoaded，没有更多数据了(thread=${Thread.currentThread().name})")
            }
        }
    }

    protected fun handleResponse(data: List<D>, type: RequestType) {
        ioExecutor.execute {
            runBlocking {
                handle(data)
                if (type == RequestType.INITIAL) {
                    refreshState.postValue(State.SuccessData)
                } else if (type == RequestType.AFTER) {
                    loadState.postValue(State.SuccessData)
                }
            }
        }
    }

    abstract fun convert(source: S?): Page<D>?

    abstract suspend fun handle(data: List<D>)
}

open class ArticlePageBoundaryCallback(
    private val articleType: Int,
    private val handleResponse: suspend (List<Article>, Int) -> Unit,
    startPage: Int,
    policy: ApiPagingPolicy,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<Page<Article>>
) :
    BasePageBoundaryCallback<Page<Article>, Article>(startPage, policy, ioExecutor, request) {
    override suspend fun handle(data: List<Article>) {
        handleResponse.invoke(data, articleType)
    }

    override fun convert(source: Page<Article>?): Page<Article>? {
        return source
    }
}

class FirstPageArticlePageBoundaryCallback(
    handleResponse: suspend (List<Article>, Int) -> Unit,
    startPage: Int,
    policy: ApiPagingPolicy,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<Page<Article>>
) : ArticlePageBoundaryCallback(
    ARTICLE_LOCAL_TYPE_FIRST_PAGE,
    handleResponse,
    startPage,
    policy,
    ioExecutor,
    request
) {
    override fun onZeroItemsLoaded() {
        //首页文章刷新先刷新置顶文章，刷新成功再刷新
        GlobalScope.launch {
            refreshState.postValue(State.Loading)
            httpRequest(object : HttpCallback<List<Article>> {
                override fun onSuccess(data: List<Article>?) {
                    data?.let { handleResponse(it, RequestType.INITIAL) }
                    refreshState.postValue(State.SuccessData)
                    doSuper()
                }

                override fun onFailure(code: Int, message: String) {
                    refreshState.postValue(State.Failure)
                }
            }) {
                DefaultRetrofitClient.getService().getTopArticles()
            }
        }
    }

    private fun doSuper() = super.onZeroItemsLoaded()

}

class TodoPageBoundaryCallback(
    private val handleResponse: suspend (List<Todo>) -> Unit,
    startPage: Int = 1,
    policy: ApiPagingPolicy = ApiPagingPolicy.DEFAULT,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<Page<Todo>>
) : BasePageBoundaryCallback<Page<Todo>, Todo>(startPage, policy, ioExecutor, request) {
    override suspend fun handle(data: List<Todo>) {
        handleResponse.invoke(data)
    }

    override fun convert(source: Page<Todo>?): Page<Todo>? {
        return source
    }
}

class CollectArticleBoundaryCallback(
    private val handleResponse: suspend (List<CollectArticle>) -> Unit,
    startPage: Int = 0,
    policy: ApiPagingPolicy = ApiPagingPolicy.NEXT,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<Page<CollectArticle>>
) : BasePageBoundaryCallback<Page<CollectArticle>, CollectArticle>(
    startPage,
    policy,
    ioExecutor,
    request
) {
    override suspend fun handle(data: List<CollectArticle>) {
        handleResponse.invoke(data)
    }

    override fun convert(source: Page<CollectArticle>?): Page<CollectArticle>? {
        return source
    }
}

class ShareArticleBoundaryCallback(
    private val handleResponse: suspend (List<Article>) -> Unit,
    private val coinInfoLiveData: MutableLiveData<CoinInfo>,
    startPage: Int,
    policy: ApiPagingPolicy,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<ShareArticle>
) : BasePageBoundaryCallback<ShareArticle, Article>(startPage, policy, ioExecutor, request) {
    override fun convert(source: ShareArticle?): Page<Article>? {
        source?.let { coinInfoLiveData.postValue(it.coinInfo) }
        return source?.shareArticles
    }

    override suspend fun handle(data: List<Article>) {
        handleResponse.invoke(data)
    }

}

class ProjectArticleBoundaryCallback(
    private val handleResponse: suspend (List<Article>, Int) -> Unit,
    private val cid: Int,
    startPage: Int,
    policy: ApiPagingPolicy,
    ioExecutor: Executor,
    request: suspend (Int) -> ApiResponse<Page<Article>>
) : BasePageBoundaryCallback<Page<Article>, Article>(
    startPage,
    policy,
    ioExecutor,
    request
) {
    override suspend fun handle(data: List<Article>) {
        handleResponse.invoke(data, cid)
    }

    override fun convert(source: Page<Article>?): Page<Article>? {
        return source
    }
}

