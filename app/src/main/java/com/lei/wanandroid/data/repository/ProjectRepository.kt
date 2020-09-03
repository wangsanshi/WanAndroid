package com.lei.wanandroid.data.repository

import androidx.lifecycle.LiveData
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.data.bean.ProjectArticleID
import com.lei.wanandroid.data.bean.ProjectClassification
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.ApiPagingPolicy
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.jetpack.paging.ProjectArticleBoundaryCallback
import com.lei.wanandroid.net.DefaultHttpCallback
import com.lei.wanandroid.net.DefaultRetrofitClient
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class ProjectRepository(private val ioExecutor: Executor) : BaseRepository() {

    fun getProjectClassificationLiveData(): LiveData<List<ProjectClassification>> {
        return localDataSource.getProjectClassificationLiveData()
    }

    suspend fun refreshProjectClassification(refreshState: StateLiveData<Any>? = null) {
        withContext(ioDispatcher) {
            refreshState?.postLoading()
            remoteDataSource.getProjectClassification(object :
                DefaultHttpCallback<List<ProjectClassification>>(sErrorCodeHandler) {
                override fun onSuccess(data: List<ProjectClassification>?) {
                    if (data != null && data.isNotEmpty()) {
                        launchIO {
                            localDataSource.saveProjectClassification(data)
                            refreshState?.postSuccess(Any())
                        }
                    } else {
                        refreshState?.postSuccess(Any())
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshState?.postFailure(message)
                }
            })
        }
    }

    fun getProjectArticleListing(cid: Int): Listing<Article> {
        val callback = ProjectArticleBoundaryCallback(
            handleResponse = this::insertArticleToDb,
            cid = cid,
            startPage = 1,
            policy = ApiPagingPolicy.DEFAULT,
            ioExecutor = ioExecutor
        ) { DefaultRetrofitClient.getService().getProjectArticleList(it, cid) }
        val sourceFactory =
            localDataSource.getProjectArticleDataSourceFactory(cid)
        val livePagedList = getPagedListLiveData(sourceFactory, callback)
        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    private suspend fun insertArticleToDb(articles: List<Article>, cid: Int) {
        localDataSource.saveArticles(articles)
        localDataSource.saveProjectArticleID(articles.map { ProjectArticleID(cid, it.id) })
    }

}