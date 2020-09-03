package com.lei.wanandroid.data.repository.base

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.lei.wanandroid.data.source.UserContext
import com.lei.wanandroid.data.source.local.ILocalDataSource
import com.lei.wanandroid.data.source.local.LocalDataSource
import com.lei.wanandroid.data.source.remote.IRemoteDataSource
import com.lei.wanandroid.data.source.remote.RemoteDataSource
import com.lei.wanandroid.net.ErrorCodeHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseRepository(
    val localDataSource: ILocalDataSource = LocalDataSource,
    val remoteDataSource: IRemoteDataSource = RemoteDataSource,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val userContext = UserContext
    val loginUserLiveData = userContext.loginUserLiveData
    val registerUserLiveData = userContext.registerUserLiveData

    protected fun launchIO(f: suspend () -> Unit) {
        GlobalScope.launch(ioDispatcher) { f.invoke() }
    }

    fun getLoginUser() = localDataSource.getLoginUser()

    fun isLoginUser(userId: Int) = getLoginUser()?.id == userId

    fun isLogin() = userContext.isLogin()

    companion object {
        private val PAGING_DEFAULT_PAGE_SIZE = 20
        private val PAGING_DEFAULT_PREFETCH_DISTANCE = 5

        //全局errorCode处理
        val sErrorCodeHandler: ErrorCodeHandler = { code, message ->
        }

        fun <Key, Value> getPagedListLiveData(
            factory: DataSource.Factory<Key, Value>,
            boundaryCallback: PagedList.BoundaryCallback<Value>? = null,
            pageSize: Int = PAGING_DEFAULT_PAGE_SIZE,
            prefetchDistance: Int = PAGING_DEFAULT_PREFETCH_DISTANCE
        ): LiveData<PagedList<Value>> {
            return LivePagedListBuilder(
                factory, PagedList.Config.Builder()
                    .setPageSize(pageSize)
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(prefetchDistance)
                    .build()
            )
                .setBoundaryCallback(boundaryCallback)
                .build()
        }
    }
}